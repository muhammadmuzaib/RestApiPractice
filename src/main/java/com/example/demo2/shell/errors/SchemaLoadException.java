package com.example.demo2.shell.errors;

public class SchemaLoadException extends RuntimeException {
    public SchemaLoadException(String message) {
        super(message);
    }

    public SchemaLoadException(String message, Throwable cause) {
        super(message, cause);
    }
}
