package com.ecommerce.project.security.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Data
@Setter
@Getter
public class SignupRequest {

    @NotBlank
    @Size(min = 3, max = 20)
    private String userName ;

    @NotBlank
    @Size(min = 6, max = 50)
    private String password ;


    private Set<String> roles ;

    @NotBlank
    @Email
    @Size(max = 50)
    private String email ;
}
