package com.shop.smartshop.exception;


import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BadRequestException extends RuntimeException{

    private final HttpStatus status;

    public BadRequestException(String message)
    {
        this(message, HttpStatus.BAD_REQUEST);
    }

    public BadRequestException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }
}
