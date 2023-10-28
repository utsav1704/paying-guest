package com.pg.owner.entity;


import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Owner {

    private String ownerId;

    private String firstName;

    private String lastName;

    private String email;

    private String password;

    private String username;

    private Boolean isVerified;

    private String identityImage;

    private String idNumber;

    private String idType;

    private Boolean isDeleted;

    private List<Property> properties;
}
