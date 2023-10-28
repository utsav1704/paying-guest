package com.pg.authserver.domain;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthenticationModel {
    private String username;
    private String password;
}
