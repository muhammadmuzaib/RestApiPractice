package com.example.demo2.core.service;

import com.example.demo2.shell.dto.response.ErrorResponse;
import com.example.demo2.shell.service.JsonResponseService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.ValidationMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class JsonResponseServiceImpl implements JsonResponseService {
    private static final Logger logger = LogManager.getLogger(JsonResponseServiceImpl.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    private ResponseEntity<ErrorResponse> buildErrorResponse(HttpStatus status, String message, String correlationId, List<String> validationErrors) {
        logger.error("Error response generated with message: '{}' for correlationId: {}", message, correlationId);
        return ResponseEntity.status(status)
                .body(new ErrorResponse("error", message, correlationId, validationErrors));
    }

    @Override
    public <T> T parseJsonToDto(String rawJson, Class<T> dtoClass) throws JsonProcessingException {
        logger.debug("Attempting to parse JSON to DTO of type: {}", dtoClass.getSimpleName());
        T dto = objectMapper.readValue(rawJson, dtoClass);
        logger.debug("Successfully parsed JSON to DTO of type: {}", dtoClass.getSimpleName());
        return dto;
    }

    @Override
    public ResponseEntity<ErrorResponse> validationErrorResponse(Set<ValidationMessage> errors, String correlationId) {
        List<String> messages = errors.stream()
                .map(e -> e.getMessage().replace("\"", "'"))
                .toList();
        logger.error("Validation failed. Errors: {}. CorrelationId: {}", messages, correlationId);
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Validation failed", correlationId, messages);
    }

    @Override
    public ResponseEntity<ErrorResponse> parseErrorResponse(String correlationId) {
        logger.error("Invalid JSON format encountered. CorrelationId: {}", correlationId);
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Invalid JSON format", correlationId, null);
    }

    @Override
    public ResponseEntity<ErrorResponse> notFoundResponse(String correlationId) {
        logger.error("Resource not found. CorrelationId: {}", correlationId);
        return buildErrorResponse(HttpStatus.NOT_FOUND, "Employee not found", correlationId, null);
    }

    @Override
    public ResponseEntity<ErrorResponse> conflictErrorResponse(String message, String correlationId) {
        logger.error("Conflict error: {}. CorrelationId: {}", message, correlationId);
        return buildErrorResponse(HttpStatus.CONFLICT, message, correlationId, null);
    }
}
