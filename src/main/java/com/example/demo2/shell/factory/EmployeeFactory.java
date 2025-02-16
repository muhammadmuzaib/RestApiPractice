package com.example.demo2.shell.factory;

import com.example.demo2.core.model.Employee;
import com.example.demo2.shell.dto.request.EmployeeCreateRequestDto;

public interface EmployeeFactory {

    /**
     * Creates a new Employee instance from the provided username and DTO.
     *
     * @param username the employee's username
     * @param dto the employee creation request data
     * @return a newly created Employee object
     */
    Employee createEmployee(String username, EmployeeCreateRequestDto dto);
}
