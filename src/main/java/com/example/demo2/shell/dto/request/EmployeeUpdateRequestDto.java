package com.example.demo2.shell.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "EmployeeUpdateRequest")
public class EmployeeUpdateRequestDto {
    @Schema(example = "newPassword", description = "New password")
    private String password;

    @Schema(example = "John", description = "Updated first name")
    private String firstName;

    @Schema(example = "Doe", description = "Updated last name")
    private String lastName;

    public String getPassword() {
        return password;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }
}