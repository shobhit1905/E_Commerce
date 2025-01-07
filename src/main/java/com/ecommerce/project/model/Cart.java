package com.ecommerce.project.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "carts")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cartId ;

    @OneToOne
    @JoinColumn(name = "userId")
    private User user ;

    @OneToMany(mappedBy = "cart" , cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
    private List<CartItem> cartItems = new ArrayList<CartItem>();

    private Double totalPrice ;

    @CreationTimestamp
    private LocalDateTime creationDate ;

    @UpdateTimestamp
    private LocalDateTime modificationDate ;

}
