package com.example.demo2.core.service;

import com.example.demo2.shell.dto.response.ErrorResponse;
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
public class JsonResponseService {
    private static final Logger logger = LogManager.getLogger(JsonResponseService.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    public <T> T parseJsonToDto(String rawJson, Class<T> dtoClass) throws Exception {
        logger.debug("Attempting to parse JSON to DTO of type: {}", dtoClass.getSimpleName());
        T dto = objectMapper.readValue(rawJson, dtoClass);
        logger.debug("Successfully parsed JSON to DTO of type: {}", dtoClass.getSimpleName());
        return dto;
    }

    public ResponseEntity<ErrorResponse> validationErrorResponse(Set<ValidationMessage> errors, String correlationId) {
        List<String> messages = errors.stream()
                .map(e -> e.getMessage().replace("\"", "'"))
                .collect(Collectors.toList());
        logger.error("Validation failed. Errors: {}. CorrelationId: {}", messages, correlationId);
        return ResponseEntity.badRequest()
                .body(new ErrorResponse("error", "Validation failed", correlationId, messages));
    }

    public ResponseEntity<ErrorResponse> parseErrorResponse(String correlationId) {
        logger.error("Invalid JSON format encountered. CorrelationId: {}", correlationId);
        return ResponseEntity.badRequest()
                .body(new ErrorResponse("error", "Invalid JSON format", correlationId, null));
    }

    public ResponseEntity<ErrorResponse> notFoundResponse(String correlationId) {
        logger.error("Resource not found. CorrelationId: {}", correlationId);
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse("error", "Employee not found", correlationId, null));
    }

    public ResponseEntity<ErrorResponse> conflictErrorResponse(String message, String correlationId) {
        logger.error("Conflict error: {}. CorrelationId: {}", message, correlationId);
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponse("error", message, correlationId, null));
    }
}
