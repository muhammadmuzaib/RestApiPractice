package com.example.demo2.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "LoginSuccessResponse")
public class LoginSuccessResponse {
    @Schema(example = "success", description = "Response status")
    private String status;

    @Schema(example = "User authenticated", description = "Success message")
    private String message;

    @Schema(example = "User correlation Id", description = "USer correlation")
    private String correlationId;

    public LoginSuccessResponse() {}

    public LoginSuccessResponse(String status, String message, String correlationId) {
        this.status = status;
        this.message = message;
        this.correlationId = correlationId;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}