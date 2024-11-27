package com.ecommerce.project.controller;

import com.ecommerce.project.configurations.AppConstants;
import com.ecommerce.project.dto.CategoryDTO;
import com.ecommerce.project.dto.CategoryResponse;
import com.ecommerce.project.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value="/api/category")
public class  CategoryController {

    @Autowired
    private CategoryService categoryService;


//    @GetMapping("/echo")
//    public ResponseEntity<String> echoMessage(@RequestParam(name = "message" , defaultValue = "Hello") String message){
//        return new ResponseEntity<>("Echoed Messaged : " + message, HttpStatus.OK);
//    }
    @GetMapping("/public/categories")
    public ResponseEntity<CategoryResponse> getAllCategories(
            @RequestParam(name = "pageNumber" , defaultValue = AppConstants.PAGE_NUMBER , required = false) Integer pageNumber ,
            @RequestParam(name = "pageSize" , defaultValue = AppConstants.PAGE_SIZE , required = false) Integer pageSize ,
            @RequestParam(name = "sortBy" , defaultValue = AppConstants.SORT_CATEGORIES_BY , required = false) String sortBy ,
            @RequestParam(name = "sortOrder" , defaultValue = AppConstants.SORT_DIRECTION , required = false) String sortOrder
    ){
        CategoryResponse categoryResponse = categoryService.getAllCategories(pageNumber , pageSize , sortBy , sortOrder);
        return new ResponseEntity<>(categoryResponse, HttpStatus.OK);
    }

    @PostMapping("/admin/addCategory")
    public ResponseEntity<CategoryDTO> createCategory(@Valid @RequestBody CategoryDTO categoryDTO){
        CategoryDTO cat = categoryService.createCategory(categoryDTO);
        return new ResponseEntity<>(cat,HttpStatus.CREATED);
    }

    @DeleteMapping("/admin/deleteCategory/{categoryId}")
    public ResponseEntity<CategoryDTO> deleteCategory(@PathVariable("categoryId") Long categoryId){
        CategoryDTO deletedCategory = categoryService.deleteCategory(categoryId);
        return new ResponseEntity<>(deletedCategory, HttpStatus.OK) ;
    }

    @PutMapping("/admin/updateCategory")
    public ResponseEntity<CategoryDTO> updateCategory(@RequestBody @Valid CategoryDTO categoryDto){
        CategoryDTO updatedCategory = categoryService.updateCategory(categoryDto) ;
        return new ResponseEntity<>(updatedCategory, HttpStatus.OK) ;
    }
}
