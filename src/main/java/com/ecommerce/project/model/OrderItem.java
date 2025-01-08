package com.ecommerce.project.model;


import jakarta.persistence.*;
import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Getter
@Setter
public class OrderItem {

    // this entity refers to the item that the customer has ordered and that are in the order of a customer
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderItemId ;

    @ManyToOne
    @JoinColumn(name = "productId")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "orderId")
    private Order order;

    private Integer quantity ;

    private Double discount ;

    private Double orderedProductPrice ;
}
