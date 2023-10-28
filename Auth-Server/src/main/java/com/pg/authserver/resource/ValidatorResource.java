package com.pg.authserver.resource;

import com.pg.authserver.domain.ValidationResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/token")
public class ValidatorResource {

    @GetMapping(path = "/validate", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<ValidationResponse> validate(HttpServletRequest request) {
        String username = (String) request.getAttribute("username");
        List<GrantedAuthority> authorities = (List<GrantedAuthority>) request.getAttribute("authorities");
        List<String> authorityString = authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());
        return ResponseEntity.ok(ValidationResponse.builder()
                .isAuthenticated(true)
                .username(username)
                .authorities(authorityString)
                .build());
    }

}
