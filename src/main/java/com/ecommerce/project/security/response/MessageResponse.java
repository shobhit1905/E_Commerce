package com.ecommerce.project.security.response;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Getter
@Setter
@NoArgsConstructor
public class MessageResponse {

    public MessageResponse(String message) {
        this.message = message;
    }

    private String message ;
}
