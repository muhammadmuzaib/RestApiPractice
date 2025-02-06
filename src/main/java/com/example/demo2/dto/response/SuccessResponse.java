package com.example.demo2.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "SuccessResponse")
public class SuccessResponse {
    @Schema(example = "success", description = "Response status")
    private String status;

    @Schema(example = "Employee created", description = "Success message")
    private String message;

    @Schema(example = "correlation-id", description = "Request correlation ID")
    private String correlationId;

    public SuccessResponse(String success, String employeeCreated, String correlationId) {
        this.status = success;
        this.message = employeeCreated;
        this.correlationId = correlationId;
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

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }
}