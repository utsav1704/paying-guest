package com.pg.apigateway.service.proxy;

import com.pg.apigateway.constant.SecurityConstant;
import com.pg.apigateway.domain.ValidationResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "AUTHENTICATION-SERVICE")
public interface AuthenticationServiceProxy {

    @GetMapping(value = "/token/validate", produces = {MediaType.APPLICATION_JSON_VALUE})
    ResponseEntity<ValidationResponse> validateConnection(@RequestHeader(SecurityConstant.TOKEN_HEADER) String authToken);
}
