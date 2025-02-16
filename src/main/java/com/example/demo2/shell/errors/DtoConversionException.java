package com.example.demo2.shell.errors;

public class DtoConversionException extends RuntimeException {
    public DtoConversionException(String message, Throwable cause) {
        super(message, cause);
    }
}
