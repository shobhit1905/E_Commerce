package com.ecommerce.project.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "addresses")
@ToString
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long addressId ;

    @NotBlank(message = "Apartment Number should not be blank")
    private String apartmentNumber ;

    @NotBlank(message = "Street should not be blank")
    private String street ;

    @NotBlank(message = "City should not be blank")
    private String city ;


    @NotBlank(message = "State should not be blank")
    private String state ;

    @NotBlank(message = "Country should not be blank")
    private String country ;

    @NotBlank(message = "Zip Code should not be blank")
    private String zipCode ;

    @ToString.Exclude
    @ManyToMany(mappedBy = "addresses")
    private List<User> user = new ArrayList<>() ;



    public Address(String zipCode, String country, String state, String city, String street, String apartmentNumber) {
        this.zipCode = zipCode;
        this.country = country;
        this.state = state;
        this.city = city;
        this.street = street;
        this.apartmentNumber = apartmentNumber;
    }
}
