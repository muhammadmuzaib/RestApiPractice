package com.example.demo2.shell.dto.request;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(name = "EmployeeLoginRequestDto")
public class EmployeeLoginRequestDto {

    @Schema(required = true, example = "admin", description = "Employee username")
    @NotBlank(message = "Username required")
    private String username;

    @Schema(required = true, example = "password", description = "Employee password")
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
