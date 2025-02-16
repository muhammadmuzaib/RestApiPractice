package com.example.demo2.shell.service;

import com.example.demo2.shell.dto.response.ErrorResponse;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.ValidationMessage;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.Set;

public interface SchemaValidationService {

    /**
     * Validates the raw JSON payload using the given JSON Schema.
     * Returns a ResponseEntity containing a ValidationErrorResponse if there are errors;
     * otherwise (if valid) the method can return null (or you can adjust it to return a success response).
     */
    ResponseEntity<ErrorResponse> validateRequest(String rawJson, String correlationId, JsonSchema schema);

    /**
     * Loads and returns a JSON Schema from the given path.
     */
    JsonSchema loadSchema(String schemaPath) throws IOException;

    /**
     * Validates the raw JSON against the provided schema and returns a set of validation messages.
     */
    Set<ValidationMessage> validate(JsonSchema schema, String rawJson);
}
