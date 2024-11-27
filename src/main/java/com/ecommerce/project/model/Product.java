package com.ecommerce.project.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId ;

    @NotBlank(message = "Product name should not be blank")
    @Size(min = 3 , message = "Product name must contains atleast 3 characters")
    private String productName ;

    @Size(min = 10 , message = "Product description must contains atleast 10 characters")
    private String description ;

    @Min(value = 0 , message = "Invalid quantity")
    private Integer quantity ;

    private Double price ;

    private Double specialPrice ;

    private Double discount ;

    private String image ;

    @CreationTimestamp
    private LocalDateTime createDate ;

    @UpdateTimestamp
    private LocalDateTime updateDate ;

    @ManyToOne
    @JoinColumn(name = "categoryId")
    private Category category ;
}
