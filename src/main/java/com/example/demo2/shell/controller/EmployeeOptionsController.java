package com.example.demo2.shell.controller;

import com.example.demo2.core.service.EmployeeOptionsServiceImpl;
import com.example.demo2.shell.errors.EmployeeMethodsNotFound;
import com.example.demo2.shell.dto.response.HttpMethodInfo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/employee-methods")
public class EmployeeOptionsController {

    private static final Logger logger = LogManager.getLogger(EmployeeOptionsController.class);

    private final EmployeeOptionsServiceImpl employeeMethodService;

    @Autowired
    public EmployeeOptionsController(EmployeeOptionsServiceImpl employeeMethodService) {
        this.employeeMethodService = employeeMethodService;
        logger.info("EmployeeOptionsController initialized");
    }

    @Operation(
            summary = "List supported methods",
            description = "Returns available HTTP methods",
            parameters = {
                    @Parameter(
                            name = "Accept",
                            description = "Expected response media type",
                            example = "application/json",
                            in = ParameterIn.HEADER
                    )
            }
    )
    @ApiResponse(
            responseCode = "200",
            description = "List of supported methods",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = HttpMethodInfo.class)
            )
    )
    @RequestMapping(method = RequestMethod.OPTIONS)
    public ResponseEntity<List<HttpMethodInfo>> getSupportedMethods() {
        logger.info("Received request to list supported HTTP methods");
        ResponseEntity<List<HttpMethodInfo>> response = employeeMethodService.listSupportedMethods();
        if (response.getBody() != null) {
            logger.info("Returning {} supported methods", response.getBody().size());
        } else {
            logger.error("No supported methods found");
            throw new EmployeeMethodsNotFound("No supported methods found");
        }
        return response;
    }
}