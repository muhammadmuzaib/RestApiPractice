package com.example.demo2.core.factory;

import com.example.demo2.core.model.Employee;
import com.example.demo2.shell.dto.request.EmployeeCreateRequestDto;
import com.example.demo2.shell.factory.EmployeeFactory;
import org.springframework.stereotype.Component;

@Component
public class EmployeeFactoryImpl implements EmployeeFactory {

    @Override
    public Employee createEmployee(String username, EmployeeCreateRequestDto dto) {
        Employee employee = new Employee();
        employee.setUsername(username);
        employee.setPassword(dto.getPassword());
        employee.setFirstName(dto.getFirstName());
        employee.setLastName(dto.getLastName());
        return employee;
    }
}
