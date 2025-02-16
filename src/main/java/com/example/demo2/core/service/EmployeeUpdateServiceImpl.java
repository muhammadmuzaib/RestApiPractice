package com.example.demo2.core.service;

import com.example.demo2.core.model.Employee;
import com.example.demo2.shell.dto.request.EmployeeUpdateRequestDto;
import com.example.demo2.shell.dto.response.SuccessResponse;
import com.example.demo2.shell.service.EmployeeUpdateService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class EmployeeUpdateServiceImpl implements EmployeeUpdateService {

    private static final Logger logger = LogManager.getLogger(EmployeeUpdateServiceImpl.class);

    private final EmployeeServiceImpl employeeServiceImpl;
    private final JsonResponseServiceImpl responseService;

    public EmployeeUpdateServiceImpl(EmployeeServiceImpl employeeServiceImpl, JsonResponseServiceImpl responseService) {
        this.employeeServiceImpl = employeeServiceImpl;
        this.responseService = responseService;
    }

    @Override
    public ResponseEntity<?> handleEmployeeUpdate(String username,
                                                              EmployeeUpdateRequestDto request,
                                                              String correlationId) {
        logger.info("Handling employee update for username: {}. CorrelationId: {}", username, correlationId);

        Employee employee = employeeServiceImpl.getEmployeeByUsername(username);
        if (employee == null) {
            logger.error("Employee not found for update (username: {}, correlationId: {})", username, correlationId);
            return responseService.notFoundResponse(correlationId);
        }

        employeeServiceImpl.updateEmployee(username, request);
        logger.info("Employee updated successfully for username: {}. CorrelationId: {}", username, correlationId);

        return ResponseEntity.ok(new SuccessResponse(
                "success",
                "Employee updated successfully",
                correlationId
        ));
    }
}
