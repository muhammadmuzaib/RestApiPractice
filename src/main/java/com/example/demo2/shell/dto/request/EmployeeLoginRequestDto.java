package com.example.demo2.shell.dto.request;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Schema(name = "EmployeeLoginRequestDto")
public class EmployeeLoginRequestDto {

    @Schema(requiredMode = REQUIRED, example = "admin", description = "Employee username")
    @NotBlank(message = "Username required")
    private String username;

    @Schema(requiredMode = REQUIRED, example = "password", description = "Employee password")
    @NotBlank(message = "Password required")
    private String password;

    public EmployeeLoginRequestDto() {
    }

    public EmployeeLoginRequestDto(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

}
