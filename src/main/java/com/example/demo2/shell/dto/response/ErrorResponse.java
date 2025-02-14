package com.example.demo2.shell.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(name = "ErrorResponse")
public class ErrorResponse {
    @Schema(example = "error", description = "Response status")
    private String status;

    @Schema(example = "Validation errors", description = "Error description")
    private String message;

    @Schema(example = "User correlation Id", description = "User correlation")
    private String correlationId;

    @Schema(example = "[\"$.username: is missing but it is required\"]",
            description = "Detailed validation errors")
    private List<String> validationErrors;


    public ErrorResponse() {}

    public ErrorResponse(String status, String message, String correlationId, List<String> validationErrors) {
        this.status = status;
        this.message = message;
        this.correlationId = correlationId;
        this.validationErrors = validationErrors;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public List<String> getValidationErrors() {
        return validationErrors;
    }

    public void setValidationErrors(List<String> validationErrors) {
        this.validationErrors = validationErrors;
    }
}