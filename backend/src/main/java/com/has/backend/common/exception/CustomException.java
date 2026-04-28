package com.has.backend.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class CustomException extends RuntimeException {

    private HttpStatus httpStatus;

    public CustomException(HttpStatus httpStatus, String message) {
        super(message); // RuntimeException(message) 세팅
        this.httpStatus = httpStatus;
    }
}
