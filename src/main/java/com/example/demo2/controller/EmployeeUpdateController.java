package com.example.demo2.controller;

import com.example.demo2.dto.request.EmployeeUpdateRequestDto;
import com.example.demo2.dto.response.ErrorResponse;
import com.example.demo2.dto.response.SuccessResponse;
import com.example.demo2.model.Employee;
import com.example.demo2.service.EmployeeLoginService;
import com.example.demo2.service.JsonResponseService;
import com.example.demo2.service.SchemaValidationService;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.ValidationMessage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Set;

public class EmployeeUpdateController {

    private static final Logger logger = LogManager.getLogger(EmployeeUpdateController.class);

    private static final String UPDATE_SCHEMA = "/schemas/employee-update-schema.json";

    private final SchemaValidationService schemaValidator;
    private final JsonResponseService responseService;
    private final EmployeeLoginService employeeService;

    private JsonSchema updateSchema;

    @Autowired
    public EmployeeUpdateController(SchemaValidationService schemaValidator,
                                    JsonResponseService responseService,
                                    EmployeeLoginService employeeService) {
        this.schemaValidator = schemaValidator;
        this.responseService = responseService;
        this.employeeService = employeeService;
    }

    @PostConstruct
    public void init() throws Exception {
        logger.info("Initializing EmployeeUpdateController. Loading schemas...");
        this.updateSchema = schemaValidator.loadSchema(UPDATE_SCHEMA);
        logger.info("Schemas loaded successfully:UPDATE_SCHEMA={}",UPDATE_SCHEMA);
    }

    @Operation(summary = "Update employee details", description = "Partial update of employee record")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Employee updated",
                    content = @Content(schema = @Schema(implementation = SuccessResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Employee not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/{username}/update")
    public ResponseEntity<?> updateEmployee(
            @PathVariable String username,
            @RequestBody String rawJson,
            HttpServletRequest request) {

        final String correlationId = (String) request.getAttribute("correlationId");
        logger.info("Received update employee request for username: {}. CorrelationId: {}", username, correlationId);


        logger.debug("Validating update employee schema for username: {}", username);
        Set<ValidationMessage> errors = schemaValidator.validate(updateSchema, rawJson);
        if (!errors.isEmpty()) {
            logger.error("Schema validation errors for update employee (username: {}, correlationId: {}): {}",
                    username, correlationId, errors);
            return responseService.validationErrorResponse(errors, correlationId);
        }

        // Parse DTO
        EmployeeUpdateRequestDto requestDto;
        try {
            logger.debug("Parsing JSON to EmployeeUpdateRequestDto for username: {}", username);
            requestDto = responseService.parseJsonToDto(rawJson, EmployeeUpdateRequestDto.class);
            logger.debug("Parsed EmployeeUpdateRequestDto: {}", requestDto);
        } catch (Exception e) {
            logger.error("Error parsing JSON for update employee (username: {}, correlationId: {}): {}",
                    username, correlationId, e.getMessage(), e);
            return responseService.parseErrorResponse(correlationId);
        }

        logger.info("Proceeding to handle employee update for username: {}. CorrelationId: {}", username, correlationId);
        return handleEmployeeUpdate(username, requestDto, correlationId);
    }


    private ResponseEntity<?> handleEmployeeUpdate(String username,
                                                   EmployeeUpdateRequestDto request,
                                                   String correlationId) {
        logger.info("Handling employee update for username: {}. CorrelationId: {}", username, correlationId);
        Employee employee = employeeService.getEmployeeByUsername(username);
        if (employee == null) {
            logger.error("Employee not found for update (username: {}, correlationId: {})", username, correlationId);
            return responseService.notFoundResponse(correlationId);
        }

        employeeService.updateEmployee(username, request);
        logger.info("Employee updated successfully for username: {}. CorrelationId: {}", username, correlationId);
        return ResponseEntity.ok(new SuccessResponse(
                "success",
                "Employee updated successfully",
                correlationId
        ));
    }
}
