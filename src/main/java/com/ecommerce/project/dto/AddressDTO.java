package com.ecommerce.project.dto;


import com.ecommerce.project.model.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddressDTO {


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
}
