package com.ecommerce.project.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "Addresses")
@Getter
@Setter
@ToString
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long addressId ;

    @NotBlank
    @Size(min = 5, message = "Street name must contain atleast 5 characters")
    private String street ;

    @NotBlank
    @Size(min = 5, message = "Building name must contain atleast 5 characters")
    private String buildingName ;

    @NotBlank
    @Size(min = 4, message = "City name must contains atleast 4 characters")
    private String city ;

    @NotBlank
    @Size(min = 2, message = "State name must contains atleast 2 characters")
    private String state ;

    @NotBlank
    @Size(min = 6, message = "Zipcode must contains atleast 2 characters")
    private String zipcode ;

    @NotBlank
    @Size(min = 2, message = "Country name must contains atleast 2 characters")
    private String country ;

    @ToString.Exclude // we only want to show the addresses not the users
    @ManyToMany(mappedBy = "addresses")
    private List<User> user = new ArrayList<>() ;
}
