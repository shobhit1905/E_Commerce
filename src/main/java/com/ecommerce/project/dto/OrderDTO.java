package com.ecommerce.project.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {

    // these are the information that the customer will see while placing the order
    private Long orderId ;
    private String email ;
    private List<OrderItemDTO> orderItems ;
    private LocalDateTime orderDateAndTime ;
    private PaymentDTO payment ;
    private Double totalAmount ;
    private String orderStatus ;
    private Long addressId ;
}
