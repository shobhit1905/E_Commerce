package com.ecommerce.project.dto;


// request object

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CategoryDTO {

    private Long categoryId ;
    private String categoryName ;
}
