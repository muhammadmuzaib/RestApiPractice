package com.example.demo2.shell.service;

import com.example.demo2.shell.dto.response.ErrorResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.networknt.schema.ValidationMessage;
import org.springframework.http.ResponseEntity;

import java.util.Set;

public interface JsonResponseService {

    /**
     * Parses a raw JSON string into a DTO of the specified type.
     *
     * @param rawJson the raw JSON string
     * @param dtoClass the target DTO class
     * @param <T> the type of the DTO
     * @return the parsed DTO instance
     * @throws Exception if the JSON cannot be parsed into the DTO
     */
    <T> T parseJsonToDto(String rawJson, Class<T> dtoClass) throws JsonProcessingException;

    /**
     * Creates a response entity for validation errors.
     *
     * @param errors the set of validation messages
     * @param correlationId the correlation id for the request
     * @return a response entity containing the error details
     */
    ResponseEntity<ErrorResponse> validationErrorResponse(Set<ValidationMessage> errors, String correlationId);

    /**
     * Creates a response entity for JSON parsing errors.
     *
     * @param correlationId the correlation id for the request
     * @return a response entity containing the error details
     */
    ResponseEntity<ErrorResponse> parseErrorResponse(String correlationId);

    /**
     * Creates a response entity for resource not found errors.
     *
     * @param correlationId the correlation id for the request
     * @return a response entity containing the error details
     */
    ResponseEntity<ErrorResponse> notFoundResponse(String correlationId);

    /**
     * Creates a response entity for conflict errors.
     *
     * @param message the conflict error message
     * @param correlationId the correlation id for the request
     * @return a response entity containing the error details
     */
    ResponseEntity<ErrorResponse> conflictErrorResponse(String message, String correlationId);

}

