package com.pg.owner.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Property {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(length = 50)
    private String propertyId;

    @Column(length = 50, nullable = false)
    private String propertyName;

    @Column(length = 100)
    private String address;

    @Column(length = 10)
    private String zipCode;

    @Column(precision = 10, scale = 2)
    private BigDecimal rent;

    private Boolean isVerified;

    private Boolean isRemoved;

    private int numberOfRooms;

    @ElementCollection
    @CollectionTable(name = "images", joinColumns = @JoinColumn(name = "property_id"))
    @Column(name = "imageName")
    private List<String> imageList;

    @ElementCollection
    @CollectionTable(name = "facilities", joinColumns = @JoinColumn(name = "property_id"))
    @Column(name = "facility")
    private List<String> facilities;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    @JsonIgnore
    private Owner owner;
}
