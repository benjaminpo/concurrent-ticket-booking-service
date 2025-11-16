package com.geoplace.ticketbooking.exception;

public class InsufficientTicketsException extends RuntimeException {
    
    public InsufficientTicketsException(String message) {
        super(message);
    }
}

