package com.pg.authserver.domain;

import lombok.*;

import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    private String username;
    private String password;
    private Boolean isEnable;
    private List<String> authorities;
}
