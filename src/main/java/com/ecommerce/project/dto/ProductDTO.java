package com.ecommerce.project.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDTO {

    private Long productId ;
    private String productName ;
    private String image ;
    private Integer quantity ;
    private Double price ;
    private Double specialPrice ;
    private Double discount;
    private String description ;
}
