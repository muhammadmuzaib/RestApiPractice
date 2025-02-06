package com.example.demo2.controller;

import com.example.demo2.dto.response.HttpMethodInfo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
public class EmployeeOptionsController {

    private static final Logger logger = LogManager.getLogger(EmployeeOptionsController.class);

    @Operation(summary = "List supported methods", description = "Returns available HTTP methods")
    @ApiResponse(responseCode = "200", description = "List of supported methods",
            content = @Content(schema = @Schema(implementation = HttpMethodInfo.class)))
    @RequestMapping(method = RequestMethod.OPTIONS)
    public ResponseEntity<List<HttpMethodInfo>> getSupportedMethods() {
        List<HttpMethodInfo> methods = List.of(
                new HttpMethodInfo("GET", "Retrieve employee details by username"),
                new HttpMethodInfo("POST", "Update partial employee information"),
                new HttpMethodInfo("PUT", "Create or replace employee record"),
                new HttpMethodInfo("DELETE", "Remove employee record"),
                new HttpMethodInfo("OPTIONS", "List supported HTTP methods")
        );
        logger.debug("Constructed supported methods list: {}", methods);

        ResponseEntity<List<HttpMethodInfo>> response = ResponseEntity.ok()
                .header("Allow", "GET, POST, PUT, DELETE, OPTIONS")
                .body(methods);

        logger.info("Returning response with header Allow: GET, POST, PUT, DELETE, OPTIONS.");
        return response;
    }
}