package com.example.demo2.shell.controller;

import com.example.demo2.core.service.*;
import com.example.demo2.shell.dto.request.EmployeeCreateRequestDto;
import com.example.demo2.shell.dto.request.EmployeeUpdateRequestDto;
import com.example.demo2.shell.dto.response.ErrorResponse;
import com.example.demo2.shell.dto.response.SuccessResponse;
import com.example.demo2.core.model.Employee;
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


@RestController
@RequestMapping("/api/employee")
public class EmployeeUpdateController {

    private static final Logger logger = LogManager.getLogger(EmployeeUpdateController.class);

    private static final String UPDATE_SCHEMA = "/schemas/employee-update-schema.json";

    private final SchemaValidationService schemaValidator;
    private final JsonResponseService responseService;
    private final DtoConversionService dtoConversionService;
    private final EmployeeUpdateService employeeUpdateService;

    private JsonSchema updateSchema;

    @Autowired
    public EmployeeUpdateController(SchemaValidationService schemaValidator,
                                    JsonResponseService responseService,
                                    DtoConversionService dtoConversionService,
                                    EmployeeUpdateService employeeUpdateService) {
        this.schemaValidator = schemaValidator;
        this.responseService = responseService;
        this.dtoConversionService = dtoConversionService;
        this.employeeUpdateService = employeeUpdateService;
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


        // 1. Schema Validation
        ResponseEntity<?> validationError = schemaValidator.validateRequest(rawJson, correlationId, updateSchema);
        if (validationError != null) {
            return validationError;
        }

        // 2. DTO Conversion
        EmployeeUpdateRequestDto requestDto = dtoConversionService.convertToDto(rawJson, correlationId, EmployeeUpdateRequestDto.class);
        if (requestDto == null) {
            return responseService.parseErrorResponse(correlationId);
        }

        // 3. Business Logic: Handle the update
        return employeeUpdateService.handleEmployeeUpdate(username, requestDto, correlationId);
    }
}
