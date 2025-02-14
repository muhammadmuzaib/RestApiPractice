package com.example.demo2.shell.service;

import com.example.demo2.core.model.Employee;
import com.example.demo2.shell.dto.request.EmployeeUpdateRequestDto;

public interface EmployeeService {

    boolean isValidEmployee(String username, String password, String correlationId);

    Employee getEmployeeByUsername(String username);

    boolean employeeExists(String username);

    void createEmployee(String username, String password, String firstName, String lastName);

    void updateEmployee(String username, EmployeeUpdateRequestDto updateRequest);

    void deleteEmployee(String username);
}
