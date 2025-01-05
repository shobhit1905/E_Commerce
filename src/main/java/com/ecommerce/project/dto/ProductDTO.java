package com.ecommerce.project.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
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
