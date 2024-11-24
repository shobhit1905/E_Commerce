package com.ecommerce.project.exceptions;

import com.ecommerce.project.dto.APIResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice

public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String,String>> myMethodArgumentNotValidException(MethodArgumentNotValidException e){
        Map<String,String> response = new HashMap<>() ;
        e.getBindingResult().getAllErrors().forEach(err -> {
            String fieldName = ((FieldError)err).getField() ;
            String fieldMessage = err.getDefaultMessage() ;
            response.put(fieldName,fieldMessage);
        }) ;

        return new ResponseEntity<>(response , HttpStatus.BAD_REQUEST) ;
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<APIResponse> myResourceNotFoundException(ResourceNotFoundException ex){
        String message = ex.getMessage() ;
        APIResponse response = new APIResponse(message , false) ;
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND) ;
    }

    @ExceptionHandler(APIExceptions.class)
    public ResponseEntity<APIResponse> myAPIException(APIExceptions e){
        String message = e.getMessage() ;
        APIResponse response = new APIResponse(message , false) ;
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST) ;
    }
}
