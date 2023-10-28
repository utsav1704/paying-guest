package com.pg.apigateway.config;

import com.pg.apigateway.filter.AuthenticationPreFilter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RouteConfiguration {

    @Bean
    public RouteLocator routes(RouteLocatorBuilder routeLocatorBuilder, AuthenticationPreFilter authenticationPreFilter) {
        return routeLocatorBuilder.routes()
                .route("auth-service-route", asr -> asr.path("/login", "/token/validate")
                        .filters(f -> f.filter(authenticationPreFilter.apply(new AuthenticationPreFilter.Config())))
                        .uri("lb://authentication-service"))
                .route("owner-service-route", r -> r.path("/owner/**", "/property/**")
                        .filters(f ->
                                f.filter(authenticationPreFilter.apply(
                                        new AuthenticationPreFilter.Config())))
                        .uri("lb://owner-service"))
                .build();
    }
}
