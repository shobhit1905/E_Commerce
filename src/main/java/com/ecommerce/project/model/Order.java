package com.ecommerce.project.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Order {

    // This entity refers to the overall order of the customer , in which we have multiple order items
    // Here order items refers to the items/products that the customer has ordered

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId ;

    @Email
    @Column(nullable = false)
    private String email ;

    @CreationTimestamp
    private LocalDateTime orderDateAndTime ;

    @OneToMany(mappedBy = "order" , cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<OrderItem> orderItems = new ArrayList<>() ;

    private Double totalAmount ;

    private String orderStatus ;

    @ManyToOne
    @JoinColumn(name = "addressId")
    private Address address ;

    @OneToOne
    @JoinColumn(name = "paymentId")
    private Payment payment ;

}
