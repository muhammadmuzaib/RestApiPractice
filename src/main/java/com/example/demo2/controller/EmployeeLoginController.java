package com.example.demo2.controller;

import com.example.demo2.dto.request.EmployeeLoginRequestDto;
import com.example.demo2.dto.response.LoginSuccessResponse;
import com.example.demo2.service.EmployeeLoginService;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.example.demo2.dto.response.ErrorResponse;
import org.springframework.web.bind.annotation.*;


import java.util.Set;


@RestController
@RequestMapping("/api/employee")
public class EmployeeLoginController {

    private static final Logger logger = LogManager.getLogger(EmployeeLoginController.class);

    private static final String SCHEMA_PATH = "/schemas/login-schema.json";
    private JsonSchema schema;


    private SchemaValidationService schemaValidator;
    private JsonResponseService responseService;
    private final EmployeeLoginService employeeLoginService;

    @Autowired
    public EmployeeLoginController(EmployeeLoginService employeeLoginService,
                                   SchemaValidationService schemaValidator,
                                   JsonResponseService responseService) {
        this.employeeLoginService = employeeLoginService;
        this.responseService = responseService;
        this.schemaValidator = schemaValidator;
    }

    @PostConstruct
    public void init() throws Exception {
        this.schema = schemaValidator.loadSchema(SCHEMA_PATH);
        logger.info("Json schema loaded from path: {}", SCHEMA_PATH);
    }

    @Operation(
            summary = "Employee Login",
            description = "Validates and authenticates the employee credentials using JSON schema validation",
            parameters = {
                    @Parameter(
                            name = "Accept",
                            description = "Expected response media type",
                            example = "application/json",
                            in = ParameterIn.HEADER
                    ),
                    @Parameter(
                            name = "Content-Type",
                            description = "Request media type",
                            example = "application/json",
                            in = ParameterIn.HEADER
                    )
            }
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "User authenticated successfully",
                    content = @Content(schema = @Schema(implementation = LoginSuccessResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request data (JSON schema validation error or parse error)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Invalid credentials",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Payload for employee login. Must follow the JSON schema defined at " + SCHEMA_PATH,
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = EmployeeLoginRequestDto.class),
                            examples = @ExampleObject(
                                    name = "Employee Login Example",
                                    summary = "A sample payload for employee login",
                                    value = "{\n  \"username\": \"johndoe\",\n  \"password\": \"examplePassword123\"\n}"
                            )
                    )
            )
            @RequestBody String rawJson,
            HttpServletRequest httpRequest) {
        final String correlationId = (String) httpRequest.getAttribute("correlationId");
        logger.info("Login request recieved. Correlation ID: {}", correlationId);

        // Validation
        Set<ValidationMessage> errors = schemaValidator.validate(schema, rawJson);
        if (!errors.isEmpty()) {
            logger.error("Validation errors for correlationId {}: {}", correlationId, errors);
            return responseService.validationErrorResponse(errors, correlationId);
        }

        // DTO Conversion
        EmployeeLoginRequestDto requestDto;
        try {
            requestDto = responseService.parseJsonToDto(rawJson, EmployeeLoginRequestDto.class);
            logger.info("Parsed EmployeeLoginRequestDto: {}", requestDto);
        } catch (Exception e) {
            logger.error("Error parsing JSON for correlationId {}: {}", correlationId, e.getMessage());
            return responseService.parseErrorResponse(correlationId);
        }

        return handleAuthentication(requestDto, correlationId);
    }

    private ResponseEntity<?> handleAuthentication(EmployeeLoginRequestDto request, String correlationId) {
        logger.info("Attempting authentication for user: {}, Correlation ID: {}", request.getUsername(), correlationId);
        boolean isAuthenticated = authenticateUser(
                request.getUsername(),
                request.getPassword(),
                correlationId
        );

        if (isAuthenticated) {
            logger.info("User {} authenticated successfully. Correlation ID: {}", request.getUsername(), correlationId);
            return ResponseEntity.ok(new LoginSuccessResponse(
                    "success",
                    "User authenticated",
                    correlationId
            ));
        } else {
            logger.error("Authentication failed for user {}. Correlation ID: {}", request.getUsername(), correlationId);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("error", "Invalid credentials", correlationId, null));
        }
    }


    private boolean authenticateUser(String username, String password, String correlationId) {
        logger.info("Authenticating user: {}. Correlation ID: {}", username, correlationId);
        return employeeLoginService.isValidEmployee(username, password, correlationId);
    }
}
