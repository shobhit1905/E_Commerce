package com.ecommerce.project.security.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LoginRequest {
    @NotBlank
    private String userName ;

    @NotBlank
    private String password ;
}
