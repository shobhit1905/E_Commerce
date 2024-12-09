package com.ecommerce.project.security.response;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class UserInfoResponse {

    private Long id;
    private String jwtToken ;

    private String userName ;

    private List<String> roles;

    public UserInfoResponse(Long id ,String userName , String jwtToken, List<String> roles) {
        this.jwtToken = jwtToken;
        this.userName = userName;
        this.roles = roles;
        this.id = id ;
    }
}
