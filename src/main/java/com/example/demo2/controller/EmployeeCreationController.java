package com.example.demo2.controller;

import com.example.demo2.dto.request.EmployeeCreateRequestDto;
import com.example.demo2.dto.response.ErrorResponse;
import com.example.demo2.dto.response.SuccessResponse;
import com.example.demo2.service.EmployeeService;
import com.example.demo2.service.JsonResponseService;
import com.example.demo2.service.SchemaValidationService;
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
public class EmployeeCreationController {

    private static final Logger logger = LogManager.getLogger(EmployeeCreationController.class);

    private static final String CREATE_SCHEMA = "/schemas/employee-create-schema.json";

    private final SchemaValidationService schemaValidator;
    private final JsonResponseService responseService;
    private final EmployeeService employeeService;

    private JsonSchema createSchema;

    @Autowired
    public EmployeeCreationController(SchemaValidationService schemaValidator,
                                      JsonResponseService responseService,
                                      EmployeeService employeeService) {
        this.schemaValidator = schemaValidator;
        this.responseService = responseService;
        this.employeeService = employeeService;
    }

    @PostConstruct
    public void init() throws Exception {
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
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Employee created",
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
            @ApiResponse(responseCode = "409", description = "Employee already exists",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
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

        final String correlationId = (String) request.getAttribute("correlationId");
        logger.info("Received create employee request for username: {}. CorrelationId: {}", username, correlationId);

        // 1. Schema Validation
        logger.debug("Validating create employee schema for username: {}", username);
        Set<ValidationMessage> errors = schemaValidator.validate(createSchema, rawJson);
        if (!errors.isEmpty()) {
            logger.error("Schema validation errors for create employee (username: {}, correlationId: {}): {}",
                    username, correlationId, errors);
            return responseService.validationErrorResponse(errors, correlationId);
        }

        // 2. Parse DTO
        EmployeeCreateRequestDto requestDto;
        try {
            logger.info("Parsing JSON to EmployeeCreateRequestDto for username: {}", username);
            requestDto = responseService.parseJsonToDto(rawJson, EmployeeCreateRequestDto.class);
            logger.info("Parsed EmployeeCreateRequestDto: {}", requestDto);
        } catch (Exception e) {
            logger.error("Error parsing JSON for create employee (username: {}, correlationId: {}): {}",
                    username, correlationId, e.getMessage(), e);
            return responseService.parseErrorResponse(correlationId);
        }

        // TODO: might remove later
        if (employeeService.employeeExists(username)) {
            logger.error("Employee already exists for username: {}. CorrelationId: {}", username, correlationId);
            return responseService.conflictErrorResponse("Employee already exists", correlationId);
        }

        logger.info("Proceeding to handle employee creation for username: {}. CorrelationId: {}", username, correlationId);
        return handleEmployeeCreation(username, requestDto, correlationId);
    }



    private ResponseEntity<?> handleEmployeeCreation(String username,
                                                     EmployeeCreateRequestDto request,
                                                     String correlationId) {
        logger.info("Handling employee creation for username: {}. CorrelationId: {}", username, correlationId);
        boolean exists = employeeService.employeeExists(username);

        if (exists) {
            logger.error("Conflict: Employee already exists for username: {}. CorrelationId: {}", username, correlationId);
            return responseService.conflictErrorResponse("Employee already exists", correlationId);
        }

        employeeService.createEmployee(
                username,
                request.getPassword(),
                request.getFirstName(),
                request.getLastName()
        );
        logger.info("Employee created successfully for username: {}. CorrelationId: {}", username, correlationId);
        return ResponseEntity.ok(new SuccessResponse(
                "success",
                "Employee created",
                correlationId
        ));
    }
}
