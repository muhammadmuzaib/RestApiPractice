package com.example.demo2.shell.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "HttpMethodInfo")
public class HttpMethodInfo {
    @Schema(example = "GET", description = "HTTP method name")
    private String method;

    @Schema(example = "Retrieve employee details", description = "Method functionality")
    private String description;

    public HttpMethodInfo(String method, String description) {
        this.method = method;
        this.description = description;
    }

    public String getMethod() {
        return method;
    }

    public String getDescription() {
        return description;
    }
}