package com.example.demo2.controller;

import com.example.demo2.dto.response.EmployeeInfoResponse;
import com.example.demo2.dto.response.ErrorResponse;
import com.example.demo2.model.Employee;
import com.example.demo2.service.EmployeeLoginService;
import com.example.demo2.service.JsonResponseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/employee")
public class EmployeeInfoController {

    private static final Logger logger = LogManager.getLogger(EmployeeInfoController.class);

    private final EmployeeLoginService employeeService;
    private final JsonResponseService responseService;

    @Autowired
    public EmployeeInfoController(EmployeeLoginService employeeService,
                                  JsonResponseService responseService) {
        this.employeeService = employeeService;
        this.responseService = responseService;
    }

    @Operation(
            summary = "Get employee details",
            description = "Retrieve employee information by username",
            parameters = {
                    @Parameter(
                            name = "username",
                            description = "Employee's username",
                            example = "user1",
                            in = ParameterIn.PATH
                    ),
                    @Parameter(
                            name = "Accept",
                            description = "Expected response media type",
                            example = "application/json",
                            in = ParameterIn.HEADER
                    )
            }
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Employee found",
                    content = @Content(schema = @Schema(implementation = EmployeeInfoResponse.class))),
            @ApiResponse(responseCode = "404", description = "Employee not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/get-info/{username}")
    public ResponseEntity<?> getEmployeeInfo(
            @PathVariable String username,
            HttpServletRequest request) {

        String correlationId = (String) request.getAttribute("correlationId");
        logger.info("Received request to get employee info for username: {}. CorrelationId: {}", username, correlationId);

        Employee employee = employeeService.getEmployeeByUsername(username);

        if (employee == null) {
            logger.error("Employee not found for username: {}. CorrelationId: {}", username, correlationId);
            return responseService.notFoundResponse(correlationId);
        }

        logger.info("Employee found: {} {}. CorrelationId: {}", employee.getFirstName(), employee.getLastName(), correlationId);

        EmployeeInfoResponse response = new EmployeeInfoResponse(
                employee.getFirstName(),
                employee.getLastName(),
                correlationId
        );

        return ResponseEntity.ok(response);
    }
}
