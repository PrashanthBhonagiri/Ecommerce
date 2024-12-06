package com.example.Ecommerce.exception;

public class InvalidDiscountCodeException extends RuntimeException{
    public InvalidDiscountCodeException(String message) {
        super(message);
    }
}
