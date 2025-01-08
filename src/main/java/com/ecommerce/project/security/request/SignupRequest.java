package com.ecommerce.project.security.request;


import com.ecommerce.project.model.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Data
public class SignUpRequest {

    @NotBlank(message = "Username should not be blank")
    @Size(min = 5, message = "Username should contain at least 5 characters")
    private String username ;

    @NotBlank(message = "First name should not be blank")
    private String firstName ;

    @NotBlank(message = "Last name should not be blank")
    private String lastName ;

    @Email
    @NotBlank(message = "Email should not be blank")
    private String email ;

    @Size(min = 8 , message = "Password must have at least 8 characters")
    private String password ;

    private Set<String> roles = new HashSet<>();
}
