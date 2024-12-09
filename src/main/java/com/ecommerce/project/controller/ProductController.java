package com.ecommerce.project.controller;

import com.ecommerce.project.configurations.AppConstants;
import com.ecommerce.project.dto.ProductDTO;
import com.ecommerce.project.dto.ProductResponse;
import com.ecommerce.project.service.ProductService;
import jakarta.validation.Valid;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api")
public class ProductController {

    @Autowired
    private ProductService productService;

    @PostMapping("/admin/categories/{categoryId}/product")
    public ResponseEntity<ProductDTO> addProduct(@RequestBody @Valid ProductDTO productDto ,
                                                 @PathVariable(name = "categoryId") Long categoryId){
        ProductDTO productDTO = productService.addNewProduct(productDto,categoryId) ;
        return new ResponseEntity<>(productDTO, HttpStatus.CREATED);
    }

    @GetMapping("/public/products")
    public ResponseEntity<ProductResponse> getAllProducts(
            @RequestParam(name = "pageNumber" , defaultValue = AppConstants.PAGE_NUMBER , required = false)  Integer pageNumber ,
            @RequestParam(name = "pageSize" , defaultValue = AppConstants.PAGE_SIZE , required = false) Integer pageSize ,
            @RequestParam(name = "sortBy" , defaultValue = AppConstants.SORT_PRODUCTS_BY , required = false) String sortBy ,
            @RequestParam(name = "sortOrder" , defaultValue = AppConstants.SORT_DIRECTION , required = false) String sortOrder
    ){
        ProductResponse productResponse = productService.getAllProducts(pageNumber , pageSize , sortBy , sortOrder) ;
        return new ResponseEntity<>(productResponse, HttpStatus.OK);
    }

    // useful for frontend , if a user wants to see products based on some categories
    @GetMapping("/public/categories/{categoryId}/products")
    public ResponseEntity<ProductResponse> getProductsByCategory(@PathVariable("categoryId") Long categoryId ,
                                                                 @RequestParam(name = "pageNumber" , defaultValue = AppConstants.PAGE_NUMBER , required = false) Integer pageNumber ,
                                                                 @RequestParam(name = "pageSize" , defaultValue = AppConstants.PAGE_SIZE , required = false) Integer pageSize ,
                                                                 @RequestParam(name = "sortBy" , defaultValue = AppConstants.SORT_PRODUCTS_BY , required = false) String sortBy ,
                                                                 @RequestParam(name = "sortOrder" , defaultValue = AppConstants.SORT_DIRECTION , required = false) String sortOrder
    ){
        ProductResponse productResponse = productService.getProductsByCategory(categoryId , pageNumber , pageSize , sortBy , sortOrder) ;
        return new ResponseEntity<>(productResponse, HttpStatus.OK);
    }

    @GetMapping("/public/products/keyword/{keyword}")
    public ResponseEntity<ProductResponse> getProductsByKeyword(@PathVariable("keyword") String keyword ,
                                                                @RequestParam(name = "pageNumber" , defaultValue = AppConstants.PAGE_NUMBER , required = false) Integer pageNumber ,
                                                                @RequestParam(name = "pageSize" , defaultValue = AppConstants.PAGE_SIZE , required = false) Integer pageSize ,
                                                                @RequestParam(name = "sortBy" , defaultValue = AppConstants.SORT_PRODUCTS_BY , required = false) String sortBy ,
                                                                @RequestParam(name = "sortOrder" , defaultValue = AppConstants.SORT_DIRECTION , required = false) String sortOrder
    ){
        ProductResponse productResponse = productService.getProductsByKeyword(keyword , pageNumber , pageSize , sortBy , sortOrder) ;
        return new ResponseEntity<>(productResponse, HttpStatus.FOUND);
    }

    @PutMapping("/admin/update/product/{productId}")
    public ResponseEntity<ProductDTO> updateProduct(@RequestBody @Valid ProductDTO productDto , @PathVariable("productId") Long productId){
        ProductDTO updatedProductDto = productService.updateProduct(productDto,productId) ;
        return new ResponseEntity<>(updatedProductDto, HttpStatus.OK);
    }

    @DeleteMapping("/admin/delete/{productId}/product")
    public ResponseEntity<ProductDTO> deleteProduct(@PathVariable("productId") Long productId){
        ProductDTO deletedProductDto = productService.deleteProduct(productId) ;
        return new ResponseEntity<>(deletedProductDto, HttpStatus.OK);
    }

    @PutMapping("/admin/products/{productId}/image")
    public ResponseEntity<ProductDTO> updateProductImage(@PathVariable Long productId,
                                                         @RequestParam("image") MultipartFile image) throws IOException {
        ProductDTO updatedProduct = productService.updateProductImage(productId , image) ;
        return new ResponseEntity<>(updatedProduct, HttpStatus.OK);
    }
}
