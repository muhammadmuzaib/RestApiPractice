package com.example.demo2.core.service;

import com.example.demo2.shell.errors.EmployeeNotFoundException;
import com.example.demo2.shell.dto.request.EmployeeUpdateRequestDto;
import com.example.demo2.core.model.Employee;
import com.example.demo2.shell.service.EmployeeService;
import org.springframework.stereotype.Service;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private static final Logger logger = LogManager.getLogger(EmployeeServiceImpl.class);

    private final List<Employee> employees = new ArrayList<>();
    private final Map<String, String> requestCorrelationMap = new HashMap<>();

    public EmployeeServiceImpl() {
        logger.info("Initializing EmployeeServiceImpl with default employees");
        employees.add(new Employee("admin", "password", "Admin", "User"));
        employees.add(new Employee("user1", "1234", "john", "doe"));
        logger.debug("Default employees added: {}", employees);
    }

    @Override
    public boolean isValidEmployee(String username, String password, String correlationId) {
        logger.info("Validating credentials for username: {}. CorrelationId: {}", username, correlationId);
        boolean isValid = employees.stream()
                .anyMatch(e -> e.getUsername().equals(username) && e.getPassword().equals(password));

        if (isValid) {
            requestCorrelationMap.put(correlationId, username);
            logger.debug("Valid credentials. CorrelationId {} mapped to username {}", correlationId, username);
        } else {
            logger.error("Invalid credentials for username: {}. CorrelationId: {}", username, correlationId);
        }
        return isValid;
    }

    @Override
    public Employee getEmployeeByUsername(String username) {
        logger.info("Retrieving employee by username: {}", username);
        Employee employee = employees.stream()
                .filter(e -> e.getUsername().equalsIgnoreCase(username))
                .findFirst()
                .orElse(null);

        if (employee == null) {
            logger.error("Employee not found for username: {}", username);
        } else {
            logger.debug("Employee found: {}", employee);
        }
        return employee;
    }

    @Override
    public boolean employeeExists(String username) {
        logger.info("Checking existence of employee with username: {}", username);
        boolean exists = employees.stream()
                .anyMatch(e -> e.getUsername().equalsIgnoreCase(username));
        logger.info("Employee exists: {} for username: {}", exists, username);
        return exists;
    }

    @Override
    public void createEmployee(Employee employee) {
        logger.info("Creating employee with username: {}", employee.getUsername());
        employees.add(new Employee(employee.getUsername(),
                employee.getPassword(),
                employee.getFirstName(),
                employee.getLastName()));
        logger.info("Employee created: {}", getEmployeeByUsername(employee.getUsername()));
    }


    @Override
    public void updateEmployee(String username, EmployeeUpdateRequestDto updateRequest) {
        logger.info("Updating employee details for username: {}", username);
        Employee employee = getEmployeeByUsername(username);
        if (employee == null) {
            String errMsg = "Update failed. Employee not found for username: " + username;
            logger.error(errMsg);
            throw new EmployeeNotFoundException(errMsg);
        }

        if (updateRequest.getPassword() != null) {
            logger.info("Updating password for employee: {}", username);
            employee.setPassword(updateRequest.getPassword());
        }
        if (updateRequest.getFirstName() != null) {
            logger.info("Updating first name for employee: {}", username);
            employee.setFirstName(updateRequest.getFirstName());
        }
        if (updateRequest.getLastName() != null) {
            logger.info("Updating last name for employee: {}", username);
            employee.setLastName(updateRequest.getLastName());
        }
        logger.info("Employee updated successfully for username: {}", username);
    }

    @Override
    public void deleteEmployee(String username) {
        logger.info("Deleting employee with username: {}", username);
        boolean removed = employees.removeIf(e -> e.getUsername().equalsIgnoreCase(username));
        if (removed) {
            logger.info("Employee deleted successfully for username: {}", username);
        } else {
            String errMsg = "Deletion failed. Employee not found for username: " + username;
            logger.error(errMsg);
            throw new EmployeeNotFoundException(errMsg);
        }
    }
}
