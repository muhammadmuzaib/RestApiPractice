package com.example.demo2.shell.errors;

public class JsonValidationException extends RuntimeException {
    public JsonValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}