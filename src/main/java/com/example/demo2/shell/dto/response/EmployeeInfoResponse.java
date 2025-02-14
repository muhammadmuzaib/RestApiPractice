package com.example.demo2.shell.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "EmployeeInfoResponse")
public class EmployeeInfoResponse {
    @Schema(example = "John", description = "Employee's first name")
    private String firstName;

    @Schema(example = "Doe", description = "Employee's last name")
    private String lastName;

    @Schema(example = "correlation-id", description = "Request correlation ID")
    private String correlationId;

    public EmployeeInfoResponse() {}

    public EmployeeInfoResponse(String firstName, String lastName, String correlationId) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.correlationId = correlationId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getCorrelationId() {
        return correlationId;
    }
}