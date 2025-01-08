package com.ecommerce.project.security.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoResponse {

    private Long id ;

    private String username ;

    private String firstName ;

    private String lastName ;

    private String token ;

    private List<String> roles ;

    public UserInfoResponse(Long id, String username, List<String> roles) {
        this.id = id;
        this.username = username;
        this.roles = roles;
    }

    public UserInfoResponse(Long id, String username, List<String> roles, String firstName, String lastName) {
        this.id = id;
        this.username = username;
        this.roles = roles;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public UserInfoResponse(Long id, String username, String firstName, String lastName, List<String> roles) {
        this.id = id;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.roles = roles;
    }

}
