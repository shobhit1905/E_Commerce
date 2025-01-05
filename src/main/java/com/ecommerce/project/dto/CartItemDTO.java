package com.ecommerce.project.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CartItemDTO {

    private Long cartItemId ;
    private CartDTO cart ;
    private ProductDTO product ;
    private Integer quantity ;
    private Double discount ;
    private Double productPrice ;
}
