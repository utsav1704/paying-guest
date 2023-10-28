package com.pg.apigateway.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pg.apigateway.constant.SecurityConstant;
import com.pg.apigateway.domain.ExceptionResponseModel;
import com.pg.apigateway.domain.ValidationResponse;
import com.pg.apigateway.service.proxy.AuthenticationServiceProxy;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.*;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.Date;
import java.util.function.Predicate;

@Component
@Slf4j
public class AuthenticationPreFilter extends AbstractGatewayFilterFactory<AuthenticationPreFilter.Config> {


    private final WebClient.Builder webClientBuilder;

    public AuthenticationPreFilter(WebClient.Builder webClientBuilder, @Lazy AuthenticationServiceProxy authenticationServiceProxy) {
        super(Config.class);
        this.webClientBuilder = webClientBuilder;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            String token = request.getHeaders().getFirst(SecurityConstant.TOKEN_HEADER);
            log.info("Token : " + token);

            if (isSecured.test(request)) {
                return webClientBuilder.build()
                        .get()
                        .uri("lb://AUTHENTICATION-SERVICE/token/validate")
                        .header(SecurityConstant.TOKEN_HEADER, token)
                        .retrieve().bodyToMono(ValidationResponse.class)
                        .map(response -> {
                            exchange.getRequest().mutate().header("username", response.getUsername());
                            exchange.getRequest().mutate().header("authorities", response.getAuthorities().stream()
                                    .reduce("", (a, b) -> a + "," + b));
                            return exchange;
                        }).flatMap(chain::filter).onErrorResume(error -> {
                            log.error("Error occurred!!");

                            HttpStatusCode errorCode = null;
                            String errorMsg = "";

                            if (error instanceof WebClientResponseException webClientResponseException) {
                                errorCode = webClientResponseException.getStatusCode();
                                errorMsg = webClientResponseException.getMessage();
                            } else {
                                errorCode = HttpStatus.BAD_GATEWAY;
                                errorMsg = HttpStatus.BAD_REQUEST.getReasonPhrase();
                            }

                            return onError(exchange, String.valueOf(errorCode.value()), errorMsg, "JWT Authentication failed.", errorCode);
                        });
            }
            return chain.filter(exchange);
        };
    }

    public Predicate<ServerHttpRequest> isSecured = serverHttpRequest -> Arrays.stream(SecurityConstant.PUBLIC_URLS).noneMatch(uri -> serverHttpRequest.getURI().getPath().contains(uri));

    private Mono<Void> onError(ServerWebExchange exchange, String errCode, String err, String errDetails, HttpStatusCode httpStatusCode) {
        ServerHttpResponse response = exchange.getResponse();
        DataBufferFactory dataBufferFactory = response.bufferFactory();
        ObjectMapper mapper = new ObjectMapper();
        response.setStatusCode(httpStatusCode);

        try {
            response.getHeaders().add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
            ExceptionResponseModel responseModel = ExceptionResponseModel.builder()
                    .errorCode(errCode)
                    .err(err)
                    .errorDetails(errDetails)
                    .date(new Date())
                    .build();
            byte[] bytesData = mapper.writeValueAsBytes(responseModel);
            return response.writeWith(Mono.just(bytesData).map(dataBufferFactory::wrap));
        } catch (JsonProcessingException jsonProcessingException) {
            log.error("Error while processing JSON : {}", jsonProcessingException.getMessage());
        }
        return response.setComplete();
    }

    @NoArgsConstructor
    public static class Config {
    }
}
