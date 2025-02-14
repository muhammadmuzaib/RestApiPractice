package com.example.demo2.shell.controller;

import com.example.demo2.core.service.AuthenticationService;
import com.example.demo2.shell.dto.request.EmployeeLoginRequestDto;
import com.example.demo2.shell.dto.response.LoginSuccessResponse;
import com.example.demo2.core.service.EmployeeService;
import com.example.demo2.core.service.JsonResponseService;
import com.example.demo2.core.service.SchemaValidationService;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.ValidationMessage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.headers.Header;
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
import com.example.demo2.shell.dto.response.ErrorResponse;
import org.springframework.web.bind.annotation.*;


import java.util.Set;


@RestController
@RequestMapping("/api/employee")
public class EmployeeLoginController {

    private static final Logger logger = LogManager.getLogger(EmployeeLoginController.class);
    private static final String SCHEMA_PATH = "/schemas/login-schema.json";

    private JsonResponseService responseService;
    private final AuthenticationService authenticationService;

    @Autowired
    public EmployeeLoginController(JsonResponseService responseService, AuthenticationService authenticationService) {
        this.responseService = responseService;
        this.authenticationService = authenticationService;
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
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = LoginSuccessResponse.class)
                    ),
                    headers = {
                            @Header(
                                    name = "Content-Type",
                                    description = "Request media type",
                                    schema = @Schema(
                                            type = "string",
                                            example = "application/json"
                                    )
                            )
                    }
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request data (JSON schema validation error or parse error)",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Invalid credentials",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
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
        ResponseEntity<?> validationError = authenticationService.validateRequest(rawJson, correlationId);
        if (validationError != null) {
            return validationError;
        }

        // DTO Conversion
        EmployeeLoginRequestDto requestDto = authenticationService.convertToDto(rawJson, correlationId);
        if (requestDto == null) {
            return responseService.parseErrorResponse(correlationId);
        }

        return authenticationService.handleAuthentication(requestDto, correlationId);
    }
}
