package com.example.demo2.controller;

import com.example.demo2.dto.response.ErrorResponse;
import com.example.demo2.dto.response.SuccessResponse;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/employee")
public class EmployeeDeleteController {

    private static final Logger logger = LogManager.getLogger(EmployeeDeleteController.class);

    private final EmployeeLoginService employeeService;
    private final JsonResponseService responseService;

    @Autowired
    public EmployeeDeleteController(EmployeeLoginService employeeService,
                                    JsonResponseService responseService) {
        this.employeeService = employeeService;
        this.responseService = responseService;
    }

    @Operation(
            summary = "Delete employee",
            description = "Permanently remove an employee record",
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
            @ApiResponse(responseCode = "200", description = "Employee deleted",
                    content = @Content(schema = @Schema(implementation = SuccessResponse.class))),
            @ApiResponse(responseCode = "404", description = "Employee not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/delete/{username}")
    public ResponseEntity<?> deleteEmployee(
            @PathVariable String username,
            HttpServletRequest request) {

        final String correlationId = (String) request.getAttribute("correlationId");
        logger.info("Received delete request for employee '{}'. CorrelationId: {}", username, correlationId);

        boolean exists = employeeService.employeeExists(username);
        if (!exists) {
            logger.error("Employee '{}' not found. CorrelationId: {}", username, correlationId);
            return responseService.notFoundResponse(correlationId);
        }

        employeeService.deleteEmployee(username);
        logger.info("Employee '{}' deleted successfully. CorrelationId: {}", username, correlationId);

        return ResponseEntity.ok(new SuccessResponse(
                "success",
                "Employee deleted successfully",
                correlationId
        ));
    }
}
