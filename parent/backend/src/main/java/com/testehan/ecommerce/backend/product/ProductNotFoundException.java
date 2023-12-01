package com.testehan.ecommerce.backend.product;

public class ProductNotFoundException extends Exception {

    public ProductNotFoundException(String message){
        super(message);
    }
}
