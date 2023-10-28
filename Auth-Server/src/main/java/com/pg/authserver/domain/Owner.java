package com.pg.authserver.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Owner {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(length = 60)
    private String ownerId;

    @Column(length = 50)
    private String firstName;

    @Column(length = 50)
    private String lastName;

    @Column(length = 70)
    private String email;

    @Column(length = 100)
    @JsonIgnore
    private String password;

    @Column(length = 50)
    private String username;

    private Boolean isVerified;

    private String identityImage;

    private String idNumber;

    private String idType;

    private Boolean isDeleted;
}
