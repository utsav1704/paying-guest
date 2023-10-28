package com.pg.authserver.domain;


import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ValidationResponse {
    private String username;
    private Boolean isAuthenticated;
    private List<String> authorities;

}
