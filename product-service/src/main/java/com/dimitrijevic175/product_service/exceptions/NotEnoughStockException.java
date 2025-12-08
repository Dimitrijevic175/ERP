package com.dimitrijevic175.product_service.exceptions;

public class NotEnoughStockException extends RuntimeException{
    public NotEnoughStockException() {
        super("Not enough stock to decrease.");
    }
}
