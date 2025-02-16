package com.example.demo2.shell.controller;

import com.example.demo2.core.service.*;
import com.example.demo2.shell.dto.request.EmployeeCreateRequestDto;
import com.example.demo2.shell.dto.response.ErrorResponse;
import com.example.demo2.shell.dto.response.SuccessResponse;
import com.example.demo2.shell.errors.SchemaLoadException;
import com.networknt.schema.JsonSchema;
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

import java.io.IOException;

import static com.example.demo2.shell.constants.AppConstants.CORRELATION_ID_CONSTANT;


@RestController
@RequestMapping("/api/employee")
public class EmployeeCreationController {

    private static final Logger logger = LogManager.getLogger(EmployeeCreationController.class);

    private static final String CREATE_SCHEMA = "/schemas/employee-create-schema.json";

    private final SchemaValidationServiceImpl schemaValidator;
    private final JsonResponseServiceImpl responseService;
    private final EmployeeCreationServiceImpl employeeCreationServiceImpl;
    private final DtoConversionServiceImpl dtoConversionServiceImpl;

    private JsonSchema createSchema;

    @Autowired
    public EmployeeCreationController(SchemaValidationServiceImpl schemaValidator,
                                      JsonResponseServiceImpl responseService,
                                      EmployeeCreationServiceImpl employeeCreationServiceImpl,
                                      DtoConversionServiceImpl dtoConversionServiceImpl) {
        this.schemaValidator = schemaValidator;
        this.responseService = responseService;
        this.employeeCreationServiceImpl = employeeCreationServiceImpl;
        this.dtoConversionServiceImpl = dtoConversionServiceImpl;
    }

    @PostConstruct
    public void init() throws SchemaLoadException {
        logger.info("Initializing EmployeeCreationController. Loading schemas...");
        this.createSchema = schemaValidator.loadSchema(CREATE_SCHEMA);
        logger.info("Schemas loaded successfully: CREATE_SCHEMA={}", CREATE_SCHEMA);
    }

    @Operation(
            summary = "Create employee",
            description = "Idempotent employee creation with schema validation. " +
                    "The JSON request body must match to the JSON Schema in " + CREATE_SCHEMA + ". " +
                    "Include the header 'X-Correlation-ID' to help track requests. (Optional)",
            parameters = {
                    @Parameter(name = "username", description = "Employee's username", example = "user1", in = ParameterIn.PATH),
                    @Parameter(name = "Accept", description = "Expected response media type", example = "application/json", in = ParameterIn.HEADER),
                    @Parameter(name = "Content-Type", description = "Content type of the request body", example = "application/json", in = ParameterIn.HEADER)
            }
    )
    @ApiResponse(responseCode = "200", description = "Employee created",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = SuccessResponse.class)
            )
    )
    @ApiResponse(responseCode = "400", description = "Invalid request data",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)
            )
    )
    @ApiResponse(responseCode = "409", description = "Employee already exists",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)
            )
    )
    @PutMapping("/create/{username}")
    public ResponseEntity<?> createEmployee(
            @PathVariable String username,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Employee creation payload. The JSON structure must follow the schema defined at " + CREATE_SCHEMA + ".",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = EmployeeCreateRequestDto.class),
                            examples = @ExampleObject(
                                    name = "Employee Creation Example",
                                    summary = "A sample payload for creating an employee",
                                    value = "{\n  \"firstName\": \"John\",\n  \"lastName\": \"Doe\",\n  \"password\": \"examplePassword123\"\n}"
                            )
                    )
            )
            @RequestBody String rawJson,
            HttpServletRequest request) {

        final String CORRELATION_ID = (String) request.getAttribute(CORRELATION_ID_CONSTANT);
        logger.info("Received create employee request for username: {}. CorrelationId: {}", username, CORRELATION_ID);

        ResponseEntity<?> validationError = schemaValidator.validateRequest(rawJson, CORRELATION_ID, createSchema);
        if (validationError != null) {
            return validationError;
        }

        EmployeeCreateRequestDto requestDto = dtoConversionServiceImpl.convertToDto(rawJson, CORRELATION_ID, EmployeeCreateRequestDto.class);
        if (requestDto == null) {
            return responseService.parseErrorResponse(CORRELATION_ID);
        }

        return employeeCreationServiceImpl.handleEmployeeCreation(username, requestDto, CORRELATION_ID);
    }
}
