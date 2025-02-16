package com.example.demo2.shell.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Schema(name = "EmployeeCreateRequest")
public class EmployeeCreateRequestDto {

    @Schema(requiredMode = REQUIRED, example = "password", description = "Employee password")
    private String password;

    @Schema(requiredMode = REQUIRED, example = "John", description = "First name")
    private String firstName;

    @Schema(requiredMode = REQUIRED, example = "Doe", description = "Last name")
    private String lastName;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}