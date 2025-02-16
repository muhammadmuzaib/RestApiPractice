package com.example.demo2.shell.errors;

public class EmployeeMethodsNotFound extends RuntimeException {
    public EmployeeMethodsNotFound(String message, Throwable cause) {
        super(message, cause);
    }

    public EmployeeMethodsNotFound(String message) {
        super(message);
    }
}
