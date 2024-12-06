package com.example.Ecommerce.exception;

public class DiscountCodeAlreadyUsedException extends RuntimeException{
    public DiscountCodeAlreadyUsedException(String message) {
        super(message);
    }
}
