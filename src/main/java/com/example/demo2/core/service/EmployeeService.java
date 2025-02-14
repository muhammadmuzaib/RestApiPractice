package com.example.demo2.core.service;

import com.example.demo2.shell.dto.request.EmployeeUpdateRequestDto;
import com.example.demo2.core.model.Employee;
import org.springframework.stereotype.Service;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EmployeeService {

    private static final Logger logger = LogManager.getLogger(EmployeeService.class);

    private final List<Employee> employees = new ArrayList<>();
    private final Map<String, String> requestCorrelationMap = new HashMap<>();

    public EmployeeService() {
        logger.info("Initializing EmployeeService with default employees");
        employees.add(new Employee("admin", "password", "Admin", "User"));
        employees.add(new Employee("user1", "1234", "john", "doe"));
        logger.debug("Default employees added: {}", employees);
    }

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

    public boolean employeeExists(String username) {
        logger.info("Checking existence of employee with username: {}", username);
        boolean exists = employees.stream()
                .anyMatch(e -> e.getUsername().equalsIgnoreCase(username));
        logger.debug("Employee exists: {} for username: {}", exists, username);
        return exists;
    }

    public void createEmployee(String username, String password, String firstName, String lastName) {
        logger.info("Creating employee with username: {}", username);
        employees.add(new Employee(username, password, firstName, lastName));
        logger.debug("Employee created: {}", getEmployeeByUsername(username));
    }

    public void updateEmployee(String username, EmployeeUpdateRequestDto updateRequest) {
        logger.info("Updating employee details for username: {}", username);
        Employee employee = getEmployeeByUsername(username);
        if (employee == null) {
            logger.warn("Update failed. Employee not found for username: {}", username);
            return;
        }

        if (updateRequest.getPassword() != null) {
            logger.debug("Updating password for employee: {}", username);
            employee.setPassword(updateRequest.getPassword());
        }
        if (updateRequest.getFirstName() != null) {
            logger.debug("Updating first name for employee: {}", username);
            employee.setFirstName(updateRequest.getFirstName());
        }
        if (updateRequest.getLastName() != null) {
            logger.debug("Updating last name for employee: {}", username);
            employee.setLastName(updateRequest.getLastName());
        }
        logger.info("Employee updated successfully for username: {}", username);
    }

    public void deleteEmployee(String username) {
        logger.info("Deleting employee with username: {}", username);
        boolean removed = employees.removeIf(e -> e.getUsername().equalsIgnoreCase(username));
        if (removed) {
            logger.info("Employee deleted successfully for username: {}", username);
        } else {
            logger.error("Deletion failed. Employee not found for username: {}", username);
        }
    }
}
