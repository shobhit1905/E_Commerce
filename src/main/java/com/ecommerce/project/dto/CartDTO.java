package com.ecommerce.project.dto;

import lombok.*;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CartDTO {

    private Long cartId ;
    private Double totalPrice ;
    private List<ProductDTO> products = new ArrayList<>() ;
}
