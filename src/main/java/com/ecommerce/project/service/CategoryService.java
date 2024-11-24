package com.ecommerce.project.service;

import com.ecommerce.project.dto.CategoryDTO;
import com.ecommerce.project.dto.CategoryResponse;
import com.ecommerce.project.exceptions.APIExceptions;
import com.ecommerce.project.exceptions.ResourceNotFoundException;
import com.ecommerce.project.model.Category;
import com.ecommerce.project.repository.CategoryRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CategoryService{
    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ModelMapper modelMapper ;


    public CategoryResponse getAllCategories(Integer pageNumber , Integer pageSize , String sortBy , String sortOrder){
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        //Pageable pageDetails = PageRequest.of(pageNumber, pageSize);
        Pageable pageDetails = PageRequest.of(pageNumber , pageSize , sortByAndOrder);
        Page<Category> categoryPage = categoryRepository.findAll(pageDetails);
        List<Category> categories = categoryPage.getContent() ;
        if(categories.isEmpty())
            throw new APIExceptions("No category present") ;

        List<CategoryDTO> categoryDTOS = categories.stream()
                .map(category -> modelMapper.map(category , CategoryDTO.class))
                .toList() ;

        CategoryResponse categoryResponse = new CategoryResponse() ;
        categoryResponse.setCategories(categoryDTOS);
        categoryResponse.setPageNumber(categoryPage.getNumber());
        categoryResponse.setPageSize(categoryPage.getSize());
        categoryResponse.setTotalElements(categoryPage.getTotalElements());
        categoryResponse.setTotalPages(categoryPage.getTotalPages());
        categoryResponse.setLastPage(categoryPage.isLast());
        return categoryResponse ;
    }


    public CategoryDTO createCategory(CategoryDTO categoryDTO){
        Category category = modelMapper.map(categoryDTO , Category.class) ;
        Category savedCategory = categoryRepository.findByCategoryName(category.getCategoryName()) ;
        if(savedCategory != null){
            throw new APIExceptions("Category with the name : " + category.getCategoryName() + " , already exists") ;
        }
        return modelMapper.map(categoryRepository.save(category) , CategoryDTO.class);
    }


    public CategoryDTO deleteCategory(Long categoryId){
        Category c = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId", categoryId)) ;

        CategoryDTO deletedCategory = modelMapper.map(c , CategoryDTO.class);
        categoryRepository.delete(c) ;
        return deletedCategory;
    }


    public CategoryDTO updateCategory(CategoryDTO categoryDto){
        Category category = modelMapper.map(categoryDto , Category.class) ;
        Category c = categoryRepository.findById(category.getCategoryId())
                .orElseThrow(() ->new ResourceNotFoundException("Category" , "categoryId" , category.getCategoryId())) ;
        Category savedCategory = categoryRepository.findByCategoryName(category.getCategoryName()) ;
        if(savedCategory != null)
            throw new APIExceptions("Category with the name : " + category.getCategoryName() + " , already exists") ;

        c.setCategoryName(category.getCategoryName());
        return modelMapper.map(categoryRepository.save(c) , CategoryDTO.class);
    }
}
