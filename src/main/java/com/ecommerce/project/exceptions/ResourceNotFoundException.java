package com.ecommerce.project.exceptions;



public class ResourceNotFoundException extends RuntimeException{

    String resourceName;
    String field ;
    String fieldName ;
    Long fieldId ;

    public ResourceNotFoundException(String resourceName, String field, String fieldName){
        super(String.format("%s not found with %s : %s",resourceName,field,fieldName));
        this.resourceName = resourceName;
        this.field = field;
        this.fieldName = fieldName;
    }

    public ResourceNotFoundException(String field , String fieldName , Long fieldId){
        super(String.format("%s not found with %s : %s",field,fieldName,fieldId));
        this.field = field;
        this.fieldName = fieldName;
        this.fieldId = fieldId;
    }

    //public ResourceNotFoundException(String )

    public ResourceNotFoundException(){}

}
