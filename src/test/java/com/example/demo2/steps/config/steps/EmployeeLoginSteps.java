package com.example.demo2.steps.config.steps;

import com.example.demo2.core.service.EmployeeServiceImpl;
import io.cucumber.java.en.Given;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;


public class EmployeeLoginSteps {

    private final EmployeeServiceImpl employeeServiceImpl;

    @Autowired
    public EmployeeLoginSteps(EmployeeServiceImpl employeeServiceImpl) {
        this.employeeServiceImpl = employeeServiceImpl;
    }

    @Given("the employee with username {string} and password {string} is a valid")
    public void theEmployeeWithUsernameAndPasswordIsAValid(String username, String password) {
        String correlationId = UUID.randomUUID().toString();

        boolean isValid = employeeServiceImpl.isValidEmployee(username, password, correlationId);

        if (!isValid) {
            throw new IllegalArgumentException("Employee credentials are invalid for username: " + username);
        }
    }

    @Given("the employee with username {string} and password {string} is invalid")
    public void the_employee_with_username_and_password_is_invalid(String username, String password) {
        if (employeeServiceImpl.employeeExists(username)) {
            employeeServiceImpl.deleteEmployee(username);
        }
    }
}
