package com.pg.owner.entity;


import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Property {

    private String propertyId;

    private String propertyName;

    private String address;

    private String zipCode;

    private BigDecimal rent;

    private Boolean isVerified;

    private Boolean isRemoved;

    private int numberOfRooms;

    private List<String> imageList;

    private List<String> facilities;

    private Owner owner;
}
