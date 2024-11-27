package com.ecommerce.project.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity(name = "categories")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long categoryId ;

    @NotBlank
    @Size(min = 3 , message = "Category name must contains atleast 3 characters")
    private String categoryName ;

    @CreationTimestamp
    private LocalDateTime createDate ;

    @UpdateTimestamp
    private LocalDateTime updateDate ;

    @OneToMany(mappedBy = "category" , cascade = CascadeType.ALL)
    private List<Product> products ;

}
