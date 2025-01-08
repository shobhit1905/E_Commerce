package com.ecommerce.project.service;

import com.ecommerce.project.dto.CartDTO;
import com.ecommerce.project.dto.ProductDTO;
import com.ecommerce.project.exceptions.APIExceptions;
import com.ecommerce.project.exceptions.ResourceNotFoundException;
import com.ecommerce.project.model.Cart;
import com.ecommerce.project.model.CartItem;
import com.ecommerce.project.model.Product;
import com.ecommerce.project.repository.CartItemRepository;
import com.ecommerce.project.repository.CartRepository;
import com.ecommerce.project.repository.ProductRepository;
import com.ecommerce.project.util.AuthUtil;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

@Service
public class CartService {


    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private AuthUtil authUtil ;

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CartItemRepository cartItemRepository;
    @Autowired
    private ModelMapper modelMapper;

    public CartDTO addProductToCart(Long productId, Integer quantity) {
        // Create the cart or find existing cart
        Cart cart = createCart() ;

        // Retrieve the product details
        Product product = productRepository.findById(productId).orElseThrow(
                () -> new ResourceNotFoundException("Product" , "productId" , productId)
        ) ;

        CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(cart.getCartId() , productId);

        // Perform validations
        if(cartItem != null)
            throw new APIExceptions("Product : " + product.getProductName() + " , already exists in the cart") ;

        if(product.getQuantity() == 0)
            throw new APIExceptions("Product : " + product.getProductName() + " , is out of stock") ;

        if(product.getQuantity() < quantity)
            throw new APIExceptions("Product : " + product.getProductName() + " , not available in the desired quantity") ;

        // Create Cart Item
        CartItem newCartItem = new CartItem();

        newCartItem.setProduct(product);
        newCartItem.setQuantity(quantity);
        newCartItem.setCart(cart);
        newCartItem.setDiscount(product.getDiscount());
        newCartItem.setProductPrice(product.getSpecialPrice());

        // Save Cart Item
        cartItemRepository.save(newCartItem);

        cart.setTotalPrice(cart.getTotalPrice() + (product.getSpecialPrice()*quantity));

        cartRepository.save(cart);

        // return updated cart
        CartDTO cartDTO = modelMapper.map(cart , CartDTO.class);
        List<CartItem> cartItems = cart.getCartItems() ;

        Stream<ProductDTO> productStream = cartItems.stream()
                .map(items -> {
                    ProductDTO map = modelMapper.map(items.getProduct(), ProductDTO.class);
                    map.setQuantity(items.getQuantity());
                    return map;
                }) ;

        cartDTO.setProducts(productStream.toList());

        return cartDTO;
    }

    private Cart createCart() {
        Cart userCart = cartRepository.findCartByEmail(authUtil.loggedInEmail()) ;
        if(userCart != null) {
            return userCart;
        }

        Cart cart = new Cart();
        cart.setTotalPrice(0.0);
        cart.setUser(authUtil.loggedInUser());

        return cartRepository.save(cart);
    }

    public List<CartDTO> getAllCarts() {
        List<Cart> carts = cartRepository.findAll() ;

        if(carts.isEmpty())
            throw new APIExceptions("No cart exists") ;

        List<CartDTO> cartDTOs = carts.stream().map(cart -> {
            CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);

            List<ProductDTO> products = cart.getCartItems().stream().map(cartItem -> {
                ProductDTO productDTO = modelMapper.map(cartItem.getProduct(), ProductDTO.class);
                productDTO.setQuantity(cartItem.getQuantity()); // Set the quantity from CartItem
                return productDTO;
            }).toList();

            return cartDTO;

        }).toList() ;

        return cartDTOs ;
    }

    public CartDTO getCart(String email, Long cartId) {
        Cart cart = cartRepository.findCartByEmailAndCartId(email,cartId) ;
        if(cart == null)
            throw new ResourceNotFoundException("Cart" , "cartId" , cartId) ;

        cart.getCartItems().forEach(c -> {
            c.getProduct().setQuantity(c.getQuantity());
        });
        CartDTO cartDTO = modelMapper.map(cart , CartDTO.class) ;
        List<ProductDTO> products = cart.getCartItems().stream()
                .map(product -> modelMapper.map(product.getProduct() , ProductDTO.class))
                .toList() ;
        cartDTO.setProducts(products);
        return cartDTO;
    }

    @Transactional // this annotation is used to make sure that if in the middle of a transaction the application fails ,
    // the whole transaction is rolled back even the updations that we have made
    public CartDTO updateProductQuantityInCart(Long productId, Integer quantity) {
        String email = authUtil.loggedInEmail() ;
        Cart userCart = cartRepository.findCartByEmail(email) ;
        Long cartId = userCart.getCartId() ;

        Cart cart = cartRepository.findById(cartId).orElseThrow(
                () -> new ResourceNotFoundException("Cart" , "cartId" , cartId)
        ) ;

        Product product = productRepository.findById(productId).orElseThrow(
                () -> new ResourceNotFoundException("Product" , "productId" , productId)
        ) ;

        if(product.getQuantity() == 0){
            throw new APIExceptions("Product : " + product.getProductName() + " , is out of stock") ;
        }

        if(product.getQuantity() < quantity){
            throw new APIExceptions("Product : " + product.getProductName() + " , not available in the desired quantity") ;
        }

        CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(cartId , productId);

        if(cartItem == null){
            throw new APIExceptions("Product : " + product.getProductName() + " , not exists in the cart") ;
        }

        // checking if the quantity of the product becomes < 0

        int newQuantity = cartItem.getQuantity() + quantity ;

        if(newQuantity < 0)
            throw new APIExceptions("Quantity cannot be less than 0") ;

        if(newQuantity == 0)
            deleteProductFromCart(cartId , productId) ;
        else{
            cartItem.setProductPrice(product.getSpecialPrice());
            cartItem.setQuantity(quantity + cartItem.getQuantity());
            cartItem.setDiscount(product.getDiscount());
            cart.setTotalPrice(cart.getTotalPrice() + (cartItem.getProductPrice()*quantity));
            cartRepository.save(cart);
        }

        CartItem updatedItem = cartItemRepository.save(cartItem);

        if(updatedItem.getQuantity() == 0){
            cartItemRepository.deleteById(updatedItem.getCartItemId());
        }

        CartDTO cartDTO = modelMapper.map(cart , CartDTO.class) ;

        List<CartItem> cartItems = cart.getCartItems() ;

        Stream<ProductDTO> productStream = cartItems.stream().map(
                item -> {
                    ProductDTO prod = modelMapper.map(item.getProduct() , ProductDTO.class) ;
                    prod.setQuantity(item.getQuantity());
                    return prod ;
                }
        ) ;

        cartDTO.setProducts(productStream.toList());

        return cartDTO;
    }

    @Transactional
    public String deleteProductFromCart(Long cartId, Long productId) {

        Cart cart = cartRepository.findById(cartId).orElseThrow(
                () -> new ResourceNotFoundException("Cart" , "cartId" , cartId)
        ) ;

        CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(cartId , productId) ;

        if(cartItem == null){
            throw new ResourceNotFoundException("Product" , "productId" , productId) ;
        }


        cart.setTotalPrice(cart.getTotalPrice() - (cartItem.getProductPrice() * cartItem.getQuantity()));

        Product product = cartItem.getProduct() ;

        //product.setQuantity(product.getQuantity() + cartItem.getQuantity());

        cartItemRepository.deleteCartItemByCartIdAndProductId(cartId , productId) ;

        return "Product : " + cartItem.getProduct().getProductName() + ", removed from the cart" ;
    }

    public void updateProductInCarts(Long cartId , Long productId) {

        Cart cart = cartRepository.findById(cartId).orElseThrow(
                () -> new ResourceNotFoundException("Cart" , "cartId" , cartId)
        ) ;

        Product product = productRepository.findById(productId).orElseThrow(
                () -> new ResourceNotFoundException("Product" , "productId" , productId)
        ) ;

        CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(cartId , productId) ;

        if(cartItem == null)
            throw new APIExceptions("Product : " + product.getProductName() + " , not exists in the cart") ;

        double cartPrice = cart.getTotalPrice() - (cartItem.getProductPrice() * cartItem.getQuantity());

        cartItem.setProductPrice(product.getSpecialPrice());

        cart.setTotalPrice(cartPrice + (cartItem.getProductPrice()*cartItem.getQuantity()));

        cartItemRepository.save(cartItem);
    }
}
