package com.ecommerce.project.service;

import com.ecommerce.project.dto.CartDTO;
import com.ecommerce.project.dto.ProductDTO;
import com.ecommerce.project.dto.ProductResponse;
import com.ecommerce.project.exceptions.APIExceptions;
import com.ecommerce.project.exceptions.ResourceNotFoundException;
import com.ecommerce.project.model.Cart;
import com.ecommerce.project.model.Category;
import com.ecommerce.project.model.Product;
import com.ecommerce.project.repository.CartRepository;
import com.ecommerce.project.repository.CategoryRepository;
import com.ecommerce.project.repository.ProductRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;



@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private FileService fileService;

    @Value("${project.image}")
    private String path ;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartService cartService;

    public ProductDTO addNewProduct(ProductDTO productDto , Long categoryId){
        Category category = categoryRepository.findById(categoryId).orElseThrow(
                () -> new ResourceNotFoundException("Category", "categoryId", categoryId)
        );

        boolean present = false ;
        List<Product> products = category.getProducts() ;
        for(int i=0 ; i<products.size() ; i++){
            if(products.get(i).getProductName().equals(productDto.getProductName())){
                present = true ;
                break ;
            }
        }

        Product product = modelMapper.map(productDto , Product.class) ;

        if(present)
            throw new APIExceptions("Product with name : " + product.getProductName() + " , already present") ;
        product.setImage("default.png");
        product.setCategory(category);
        double price = product.getPrice();
        double discount = product.getDiscount();
        double specialPrice = price - (price * (discount/100)) ;
        product.setSpecialPrice(specialPrice);

        Product savedProduct = productRepository.save(product);

        return modelMapper.map(savedProduct, ProductDTO.class);
    }

    public ProductResponse getAllProducts(Integer pageNumber , Integer pageSize , String sortBy , String sortOrder){

        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending() ;
        Pageable pageDetails = PageRequest.of(pageNumber , pageSize , sortByAndOrder);
        Page<Product> productPage = productRepository.findAll(pageDetails) ;
        List<Product> products = productPage.getContent() ;

        if(products.isEmpty())
            throw new APIExceptions("No products found") ;

        List<ProductDTO> productDtos = products.stream()
                .map(product -> modelMapper.map(product , ProductDTO.class))
                .toList() ;

        ProductResponse productResponse = new ProductResponse();
        productResponse.setProducts(productDtos);

        productResponse.setPageNumber(productPage.getNumber());
        productResponse.setPageSize(productPage.getSize());
        productResponse.setTotalPages(productPage.getTotalPages());
        productResponse.setTotalElements(productPage.getTotalElements());
        productResponse.setLastPage(productPage.isLast());
        return productResponse;
    }

    public ProductResponse getProductsByCategory(Long categoryId , Integer pageNumber , Integer pageSize , String sortBy , String sortOrder){

        Category category = categoryRepository.findById(categoryId).orElseThrow(
                () -> new ResourceNotFoundException("Category", "categoryId", categoryId)
        ) ;

        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending() ;
        Pageable pageDetails = PageRequest.of(pageNumber , pageSize , sortByAndOrder);
        Page<Product> productPage = productRepository.findByCategoryOrderByPriceAsc(category , pageDetails) ;

        List<Product> products = productPage.getContent() ;

        if(products.isEmpty())
            throw new APIExceptions("No products found for category : " + category.getCategoryName()) ;
        List<ProductDTO> productDtos = products.stream()
                .map(product -> modelMapper.map(product , ProductDTO.class))
                .toList() ;

        ProductResponse productResponse = new ProductResponse();
        productResponse.setProducts(productDtos);
        productResponse.setPageNumber(productPage.getNumber());
        productResponse.setPageSize(productPage.getSize());
        productResponse.setTotalPages(productPage.getTotalPages());
        productResponse.setTotalElements(productPage.getTotalElements());
        productResponse.setLastPage(productPage.isLast());
        return productResponse;
    }

    public ProductResponse getProductsByKeyword(String keyword , Integer pageNumber , Integer pageSize , String sortBy , String sortOrder){
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending() ;
        Pageable pageDetails = PageRequest.of(pageNumber , pageSize , sortByAndOrder);
        Page<Product> productPage = productRepository.findByProductNameLikeIgnoreCase("%" + keyword + "%", pageDetails) ;

        List<Product> products = productPage.getContent() ;

        if(products.isEmpty())
            throw new APIExceptions("No products found for keyword : " + keyword) ;

        List<ProductDTO> productDtos = products.stream()
                .map(product -> modelMapper.map(product , ProductDTO.class))
                .toList() ;

        ProductResponse productResponse = new ProductResponse();
        productResponse.setProducts(productDtos);
        productResponse.setPageNumber(productPage.getNumber());
        productResponse.setPageSize(productPage.getSize());
        productResponse.setTotalPages(productPage.getTotalPages());
        productResponse.setTotalElements(productPage.getTotalElements());
        productResponse.setLastPage(productPage.isLast());
        return productResponse;
    }

    public ProductDTO updateProduct(ProductDTO productDto , Long productId){
        //get the existing product from the db
        Product existingProduct = productRepository.findById(productId).orElseThrow(
                () -> new ResourceNotFoundException("Product" , "productId" , productId)
        ) ;

        //update the product with given info
        existingProduct.setProductName(productDto.getProductName());
        existingProduct.setPrice(productDto.getPrice());
        existingProduct.setDiscount(productDto.getDiscount());
        existingProduct.setQuantity(productDto.getQuantity());
        existingProduct.setDescription(productDto.getDescription());

        double specialPrice = productDto.getPrice() - (productDto.getDiscount()/100 * productDto.getPrice()) ;
        existingProduct.setSpecialPrice(specialPrice);

        // save to db
        Product savedProduct = productRepository.save(existingProduct);

        // if any changes are made to the product it should also be reflected in the cart
        List<Cart> carts = cartRepository.findCartsByProductId(productId) ;

        List<CartDTO> cartDTOs = carts.stream().map(cart -> {
            CartDTO cartDTO = modelMapper.map(cart , CartDTO.class) ;

            List<ProductDTO> products = cart.getCartItems().stream()
                    .map(p -> modelMapper.map(p.getProduct() , ProductDTO.class)).toList()  ;

            cartDTO.setProducts(products);

            return cartDTO ;

        }).toList() ;

        cartDTOs.forEach(cart -> cartService.updateProductInCarts(cart.getCartId() , productId));

        return modelMapper.map(savedProduct , ProductDTO.class) ;

    }

    public ProductDTO deleteProduct(Long productId){
        Product existingProduct = productRepository.findById(productId).orElseThrow(
                () -> new ResourceNotFoundException("Product" , "productId" , productId)
        ) ;

        // we also need to delete the product from the carts as well
        List<Cart> carts = cartRepository.findCartsByProductId(productId) ;
        carts.forEach(cart -> cartService.deleteProductFromCart(cart.getCartId() , productId));

        productRepository.delete(existingProduct);
        return modelMapper.map(existingProduct , ProductDTO.class) ;
    }

    public ProductDTO updateProductImage(Long productId, MultipartFile image) throws IOException {
        //get the product from db
        Product productFromDB= productRepository.findById(productId).orElseThrow(
                () -> new ResourceNotFoundException("Product" , "productId" , productId)
        ) ;

        //upload image to server
        //get the filename of the uploaded image
        //String path = "images/" ;
        String fileName = fileService.uploadFile(path , image) ;

        //updating the new file name to the product
        productFromDB.setImage(fileName);

        //save the updated product
        Product savedProduct = productRepository.save(productFromDB);

        //return dto after mapping product to dto
        return modelMapper.map(savedProduct , ProductDTO.class) ;
    }

    public void uploadCSV(String fileContent) {

        List<String> productData = List.of(fileContent.split("\n"));
        for(int i=1 ; i<productData.size() ; i++) {
            String[] row = productData.get(i).split(",");
            ProductDTO p = ProductDTO.builder()
                            .productName(row[0])
                            .description(row[1])
                            .price(Double.parseDouble(row[2]))
                            .discount(Double.parseDouble(row[3]))
                    .quantity(Integer.parseInt(row[4]))
                    .build() ;
            Long categoryId = Long.parseLong(row[5]) ;
            addNewProduct(p, categoryId) ;
        }

    }
}
