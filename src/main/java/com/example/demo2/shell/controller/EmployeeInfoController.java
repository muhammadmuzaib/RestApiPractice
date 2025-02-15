package com.example.demo2.shell.controller;

import com.example.demo2.core.service.EmployeeInfoService;
import com.example.demo2.shell.dto.response.EmployeeInfoResponse;
import com.example.demo2.shell.dto.response.ErrorResponse;
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

    private final EmployeeInfoService employeeInfoService;

    @Autowired
    public EmployeeInfoController(EmployeeInfoService employeeInfoService) {
        this.employeeInfoService = employeeInfoService;
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
            @ApiResponse(
                    responseCode = "200",
                    description = "Employee found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = EmployeeInfoResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Employee not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @GetMapping("/get-info/{username}")
    public ResponseEntity<?> getEmployeeInfo(
            @PathVariable String username,
            HttpServletRequest request) {

        final String CORRELATION_ID = (String) request.getAttribute("correlationId");
        logger.info("Received request to get employee info for username: {}. CorrelationId: {}", username, CORRELATION_ID);

        return employeeInfoService.retrieveEmployeeInfo(username, CORRELATION_ID);
    }
}
