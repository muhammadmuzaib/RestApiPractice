package com.example.demo2.shell.service;

import com.example.demo2.shell.dto.response.HttpMethodInfo;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface EmployeeOptionsService {

    /**
     * Builds and returns the response containing the list of supported HTTP methods.
     *
     * @return a ResponseEntity with the list of HTTP methods and an Allow header.
     */
    ResponseEntity<List<HttpMethodInfo>> listSupportedMethods();
}
