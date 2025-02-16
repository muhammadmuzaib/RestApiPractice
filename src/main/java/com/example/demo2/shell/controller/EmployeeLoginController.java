package com.example.demo2.shell.controller;

import com.example.demo2.core.service.AuthenticationServiceImpl;
import com.example.demo2.core.service.DtoConversionServiceImpl;
import com.example.demo2.core.service.SchemaValidationServiceImpl;
import com.example.demo2.shell.dto.request.EmployeeLoginRequestDto;
import com.example.demo2.shell.dto.response.LoginSuccessResponse;
import com.example.demo2.core.service.JsonResponseServiceImpl;
import com.networknt.schema.JsonSchema;
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
import org.springframework.http.ResponseEntity;
import com.example.demo2.shell.dto.response.ErrorResponse;
import org.springframework.web.bind.annotation.*;

import static com.example.demo2.shell.constants.AppConstants.CORRELATION_ID_CONSTANT;


@RestController
@RequestMapping("/api/employee")
public class EmployeeLoginController {

    private static final Logger logger = LogManager.getLogger(EmployeeLoginController.class);
    private static final String SCHEMA_PATH = "/schemas/login-schema.json";

    private final SchemaValidationServiceImpl schemaValidator;
    private JsonResponseServiceImpl responseService;
    private final AuthenticationServiceImpl authenticationServiceImpl;
    private final DtoConversionServiceImpl dtoConversionServiceImpl;
    private final SchemaValidationServiceImpl schemaValidationServiceImpl;

    private JsonSchema loginSchema;

    @Autowired
    public EmployeeLoginController(JsonResponseServiceImpl responseService,
                                   SchemaValidationServiceImpl schemaValidator,
                                   AuthenticationServiceImpl authenticationServiceImpl,
                                   DtoConversionServiceImpl dtoConversionServiceImpl,
                                   SchemaValidationServiceImpl schemaValidationServiceImpl
                                   ) {
        this.responseService = responseService;
        this.schemaValidator = schemaValidator;
        this.authenticationServiceImpl = authenticationServiceImpl;
        this.dtoConversionServiceImpl = dtoConversionServiceImpl;
        this.schemaValidationServiceImpl = schemaValidationServiceImpl;
    }

    @PostConstruct
    public void init() throws Exception {
        logger.info("Initializing EmployeeLoginController. Loading schemas...");
        this.loginSchema = schemaValidator.loadSchema(SCHEMA_PATH);
        logger.info("Schemas loaded successfully: SCHEMA_PATH={}", SCHEMA_PATH);
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
    )
    @ApiResponse(
            responseCode = "400",
            description = "Invalid request data (JSON schema validation error or parse error)",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)
            )
    )
    @ApiResponse(
            responseCode = "401",
            description = "Invalid credentials",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)
            )
    )
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
        final String CORRELATION_ID = (String) httpRequest.getAttribute(CORRELATION_ID_CONSTANT);
        logger.info("Login request recieved. Correlation ID: {}", CORRELATION_ID);

        ResponseEntity<?> validationError = schemaValidationServiceImpl.validateRequest(rawJson, CORRELATION_ID, loginSchema);
        if (validationError != null) {
            return validationError;
        }

        EmployeeLoginRequestDto requestDto = dtoConversionServiceImpl.convertToDto(rawJson, CORRELATION_ID, EmployeeLoginRequestDto.class);
        if (requestDto == null) {
            return responseService.parseErrorResponse(CORRELATION_ID);
        }

        return authenticationServiceImpl.handleAuthentication(requestDto, CORRELATION_ID);
    }
}
