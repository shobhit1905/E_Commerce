package com.ecommerce.project.controller;

import com.ecommerce.project.dto.CartDTO;
import com.ecommerce.project.model.Cart;
import com.ecommerce.project.repository.CartRepository;
import com.ecommerce.project.service.CartService;
import com.ecommerce.project.util.AuthUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CartController {

    @Autowired
    private CartService cartService ;

    @Autowired
    private AuthUtil authUtil ;
    @Autowired
    private CartRepository cartRepository;

    //adding a product to the cart , if cart not exists create a new cart and then add a product
    @PostMapping("/cart/product/{productId}/quantity/{quantity}")
    public ResponseEntity<CartDTO> addProductToCart(@PathVariable("productId") Long productId ,
                                                    @PathVariable("quantity") Integer quantity) {
        CartDTO cartDTO = cartService.addProductToCart(productId , quantity) ;
        return new ResponseEntity<>(cartDTO, HttpStatus.CREATED);
    }

    // get details of all the carts in the application
    @GetMapping("/carts")
    public ResponseEntity<List<CartDTO>> getCarts() {
        List<CartDTO> cartDTOs = cartService.getAllCarts() ;
        return new ResponseEntity<>(cartDTOs, HttpStatus.FOUND);
    }


    // Get the details of the cart of the logged in user
    @GetMapping("/carts/user/cart")
    public ResponseEntity<CartDTO> getCartById(){
        // email , because we need to make sure that the user which is logged in is accessing the right cart
        // cartId , because we want our code to be scalable in future

        String email = authUtil.loggedInEmail() ;
        Cart cart = cartRepository.findCartByEmail(email) ;
        Long cartId = cart.getCartId() ;
        CartDTO cartDTO = cartService.getCart(email , cartId) ;
        return new ResponseEntity<>(cartDTO, HttpStatus.FOUND) ;
    }

    // Updating the quantity of a product on the cart
    @PutMapping("/cart/products/{productId}/quantity/{operation}")
    public ResponseEntity<CartDTO> updateCartProduct(@PathVariable("productId") Long productId ,
                                                     @PathVariable("operation") String operation) {
        CartDTO cartDTO = cartService.updateProductQuantityInCart(productId ,
                operation.equalsIgnoreCase("delete") ? -1 : 1) ;

        return new ResponseEntity<>(cartDTO, HttpStatus.OK) ;
    }


    // Deleting a product from the cart
    @DeleteMapping("/carts/{cartId}/product/{productId}")
    public ResponseEntity<String> deleteProductFromCart(@PathVariable("cartId") Long cartId ,
                                                     @PathVariable("productId") Long productId) {
        String status = cartService.deleteProductFromCart(cartId , productId) ;

        return new ResponseEntity<>(status, HttpStatus.OK) ;
    }
}
