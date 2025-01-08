package com.ecommerce.project.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Payment{

    // this entity represents the payment facility for the ecommerce application

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentId ;

    @OneToOne(mappedBy = "payment" , cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Order order ;

    private String paymentMethod ;

    private String pgPaymentId ;

    private String pgStatus ;


    private String pgResponseMessage ;

    private String pgName ;

    public Payment(String paymentMethod, String pgName, String pgPaymentId, String pgResponseMessage, String pgStatus) {
        this.paymentMethod = paymentMethod;
        this.pgPaymentId = pgPaymentId;
        this.pgStatus = pgStatus;
        this.pgResponseMessage = pgResponseMessage;
        this.pgName = pgName;
    }
}
