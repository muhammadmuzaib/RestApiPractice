package com.example.demo2.core.service;

import com.example.demo2.core.model.Employee;
import com.example.demo2.shell.dto.request.EmployeeCreateRequestDto;
import com.example.demo2.shell.dto.response.SuccessResponse;
import com.example.demo2.shell.factory.EmployeeFactory;
import com.example.demo2.shell.service.EmployeeCreationService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class EmployeeCreationServiceImpl implements EmployeeCreationService {

    private static final Logger logger = LogManager.getLogger(EmployeeCreationServiceImpl.class);

    private final EmployeeServiceImpl employeeServiceImpl;
    private final JsonResponseServiceImpl responseService;
    private final EmployeeFactory employeeFactory;

    @Autowired
    public EmployeeCreationServiceImpl(EmployeeServiceImpl employeeServiceImpl,
                                       JsonResponseServiceImpl responseService,
                                       EmployeeFactory employeeFactory) {
        this.employeeServiceImpl = employeeServiceImpl;
        this.responseService = responseService;
        this.employeeFactory = employeeFactory;
    }

    @Override
    public ResponseEntity<?> handleEmployeeCreation(String username, EmployeeCreateRequestDto requestDto, String correlationId) {
        logger.info("Handling employee creation for username: {}. CorrelationId: {}", username, correlationId);

        if (employeeServiceImpl.employeeExists(username)) {
            logger.error("Conflict: Employee already exists for username: {}. CorrelationId: {}", username, correlationId);
            return responseService.conflictErrorResponse("Employee already exists", correlationId);
        }

        Employee employee = employeeFactory.createEmployee(username, requestDto);
        employeeServiceImpl.createEmployee(employee);

        logger.info("Employee created successfully for username: {}. CorrelationId: {}", username, correlationId);
        return ResponseEntity.ok(new SuccessResponse("success", "Employee created", correlationId));
    }
}
