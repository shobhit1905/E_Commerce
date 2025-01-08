package com.ecommerce.project.service;

import com.ecommerce.project.dto.OrderDTO;
import com.ecommerce.project.dto.OrderItemDTO;
import com.ecommerce.project.exceptions.APIExceptions;
import com.ecommerce.project.exceptions.ResourceNotFoundException;
import com.ecommerce.project.model.*;
import com.ecommerce.project.repository.*;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CartService cartService;

    @Autowired
    private ModelMapper modelMapper ;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private ProductRepository productRepository;

    // placing order has a few steps
    // we are assuming the user has done the payment

    @Transactional
    public OrderDTO placeOrder(String emailId, Long addressId, String paymentMethod, String pgName,
                               String pgPaymentId, String pgStatus, String pgResponseMessage) {


        // 1) finding cart of the logged in user , cart has to exist for the order to process
        Cart cart = cartRepository.findCartByEmail(emailId);
        if(cart == null){
            throw new ResourceNotFoundException("Cart" , "email" , emailId) ;
        }

        // 2) finding the address of the user where the order is to be shipped
        Address address = addressRepository.findById(addressId).orElseThrow(
                () -> new ResourceNotFoundException("Address" , "addressId" , addressId)
        );

        // 3) creating the order object
        Order order = new Order() ;
        order.setEmail(emailId);
        order.setOrderDateAndTime(LocalDateTime.now());
        order.setTotalAmount(cart.getTotalPrice());
        // when the project will grow , the functionality will be added where the seller can update the order status , using their dashboard
        order.setOrderStatus("Order Accepted");
        order.setAddress(address);

        // 4) creating the payment information that needs to be set against an order
        Payment payment = new Payment(paymentMethod, pgName, pgPaymentId, pgResponseMessage , pgStatus);
        payment.setOrder(order); // helps to track which payment is for which order
        payment = paymentRepository.save(payment);
        order.setPayment(payment);

        Order savedOrder = orderRepository.save(order);

        // 5) adding items in the order from the cart items
        List<CartItem> cartItems = cart.getCartItems();

        if(cartItems == null)
            throw new APIExceptions("Cart is Empty") ;

        List<OrderItem> orderItems = new ArrayList<OrderItem>() ;

        for(CartItem cartItem : cartItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setDiscount(cartItem.getDiscount());
            orderItem.setOrderedProductPrice(cartItem.getProductPrice());
            orderItem.setOrder(savedOrder);
            orderItems.add(orderItem);
        }

        orderItems = orderItemRepository.saveAll(orderItems);

        // 6) update the stock of the products
        cart.getCartItems().forEach(item -> {
            int quantity = item.getQuantity();
            Product product = item.getProduct();

            // reduce the product quantity in the db
            product.setQuantity(product.getQuantity() - quantity);
            productRepository.save(product);

            // remove from the cart
            cartService.deleteProductFromCart(cart.getCartId() , item.getProduct().getProductId()) ;
        });

        OrderDTO orderDTO =  modelMapper.map(savedOrder, OrderDTO.class) ;
        orderItems.forEach(item -> orderDTO.getOrderItems().add(modelMapper.map(item, OrderItemDTO.class)));

        orderDTO.setAddressId(addressId);

        return orderDTO;
    }
}
