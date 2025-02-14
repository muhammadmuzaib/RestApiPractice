package com.example.demo2.shell.controller;

import com.example.demo2.core.service.EmployeeServiceImpl;
import com.example.demo2.shell.dto.request.EmployeeUpdateRequestDto;
import com.example.demo2.shell.dto.response.ErrorResponse;
import com.example.demo2.shell.dto.response.SuccessResponse;
import com.example.demo2.core.model.Employee;
import com.example.demo2.core.service.JsonResponseService;
import com.example.demo2.core.service.SchemaValidationService;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.ValidationMessage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/employee")
public class EmployeeUpdateController {

    private static final Logger logger = LogManager.getLogger(EmployeeUpdateController.class);

    private static final String UPDATE_SCHEMA = "/schemas/employee-update-schema.json";

    private final SchemaValidationService schemaValidator;
    private final JsonResponseService responseService;
    private final EmployeeServiceImpl employeeServiceImpl;

    private JsonSchema updateSchema;

    @Autowired
    public EmployeeUpdateController(SchemaValidationService schemaValidator,
                                    JsonResponseService responseService,
                                    EmployeeServiceImpl employeeServiceImpl) {
        this.schemaValidator = schemaValidator;
        this.responseService = responseService;
        this.employeeServiceImpl = employeeServiceImpl;
    }

    @PostConstruct
    public void init() throws Exception {
        logger.info("Initializing EmployeeUpdateController. Loading schemas...");
        this.updateSchema = schemaValidator.loadSchema(UPDATE_SCHEMA);
        logger.info("Schemas loaded successfully:UPDATE_SCHEMA={}",UPDATE_SCHEMA);
    }

    @Operation(
            summary = "Update employee details",
            description = "Partial update of employee record",
            parameters = {
                    @Parameter(
                            name = "Accept",
                            description = "Expected response media type",
                            example = "application/json",
                            in = ParameterIn.HEADER
                    ),
                    @Parameter(
                            name = "Content-Type",
                            description = "Request content type",
                            example = "application/json",
                            in = ParameterIn.HEADER
                    ),
                    @Parameter(
                            name = "username",
                            description = "Username of the employee to update",
                            example = "johndoe",
                            in = ParameterIn.PATH
                    )
            }
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Employee updated",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SuccessResponse.class)
                    )
            ),
            @ApiResponse(responseCode = "400", description = "Invalid request data",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(responseCode = "404", description = "Employee not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @PostMapping("/update/{username}")
    public ResponseEntity<?> updateEmployee(
            @PathVariable String username,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Payload for updating employee details. Must follow the JSON schema defined at /schemas/employee-update-schema.json.",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = EmployeeUpdateRequestDto.class),
                            examples = @ExampleObject(
                                    name = "Employee Update Example",
                                    summary = "A sample payload for updating employee details",
                                    value = "{\n  \"firstName\": \"Jane\",\n  \"lastName\": \"Doe\",\n  \"password\": \"newPassword123\"\n}"
                            )
                    )
            )
            @RequestBody String rawJson,
            HttpServletRequest request) {

        final String correlationId = (String) request.getAttribute("correlationId");
        logger.info("Received update employee request for username: {}. CorrelationId: {}", username, correlationId);


        logger.info("Validating update employee schema for username: {}", username);
        Set<ValidationMessage> errors = schemaValidator.validate(updateSchema, rawJson);
        if (!errors.isEmpty()) {
            logger.error("Schema validation errors for update employee (username: {}, correlationId: {}): {}",
                    username, correlationId, errors);
            return responseService.validationErrorResponse(errors, correlationId);
        }

        // Parse DTO
        EmployeeUpdateRequestDto requestDto;
        try {
            logger.info("Parsing JSON to EmployeeUpdateRequestDto for username: {}", username);
            requestDto = responseService.parseJsonToDto(rawJson, EmployeeUpdateRequestDto.class);
            logger.info("Parsed EmployeeUpdateRequestDto: {}", requestDto);
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
