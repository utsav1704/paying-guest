package com.pg.authserver.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pg.authserver.constant.SecurityConstant;
import com.pg.authserver.domain.AuthenticationModel;
import com.pg.authserver.domain.UserPrincipal;
import com.pg.authserver.domain.ValidationResponse;
import com.pg.authserver.utility.JWTTokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JWTTokenProvider jwtTokenProvider;
    private ObjectMapper mapper = new ObjectMapper();

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            AuthenticationModel authenticationModel = mapper.readValue(request.getInputStream(), AuthenticationModel.class);
            log.info("Username is : {}", authenticationModel.getUsername());
            Authentication authentication = new UsernamePasswordAuthenticationToken(authenticationModel.getUsername(), authenticationModel.getPassword());
            authentication = authenticationManager.authenticate(authentication);
            return authentication;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        Object object = authResult.getPrincipal();
        if (object instanceof User user) {
            UserPrincipal userPrincipal = new UserPrincipal(com.pg.authserver.domain.User.builder()
                    .username(user.getUsername())
                    .isEnable(user.isEnabled())
                    .password("")
                    .authorities(user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                    .build());
            String token = jwtTokenProvider.generateJwtToken(userPrincipal);
            log.info("Token is : {}", token);

            response.addHeader(SecurityConstant.TOKEN_HEADER, SecurityConstant.TOKEN_PREFIX + token);

            ValidationResponse validationResponse = ValidationResponse.builder()
                    .username(userPrincipal.getUsername())
                    .isAuthenticated(true)
                    .build();

            response.addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
            response.getOutputStream().write(mapper.writeValueAsBytes(validationResponse));
        }
    }
}
