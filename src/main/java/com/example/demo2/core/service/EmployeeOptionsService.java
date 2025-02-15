package com.example.demo2.core.service;

import com.example.demo2.shell.dto.response.HttpMethodInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeOptionsService {

    private static final Logger logger = LogManager.getLogger(EmployeeOptionsService.class);

    /**
     * Builds the response containing the list of supported HTTP methods.
     *
     * @return a ResponseEntity containing the list of HTTP methods and an Allow header
     */
    public ResponseEntity<List<HttpMethodInfo>> ListSupportedMethods() {
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
