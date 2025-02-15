package com.example.demo2.core.service;

import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

@Service
public class SchemaValidationService {

    private static final Logger logger = LogManager.getLogger(SchemaValidationService.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final JsonResponseService responseService;


    @Autowired
    public SchemaValidationService(JsonResponseService responseService) throws IOException {
        this.responseService = responseService;
    }

    public ResponseEntity<?> validateRequest(String rawJson, String correlationId, JsonSchema schema) {
        Set<ValidationMessage> errors = validate(schema, rawJson);
        if (!errors.isEmpty()) {
            logger.error("Validation errors for correlationId {}: {}", correlationId, errors);
            return responseService.validationErrorResponse(errors, correlationId);
        }
        return null;
    }

    public JsonSchema loadSchema(String schemaPath) throws IOException {
        logger.info("Loading schema from path: {}", schemaPath);
        try (InputStream schemaStream = getClass().getResourceAsStream(schemaPath)) {
            if (schemaStream == null) {
                logger.error("Schema not found at path: {}", schemaPath);
                throw new IllegalStateException("Missing schema: " + schemaPath);
            }
            JsonNode schemaNode = objectMapper.readTree(schemaStream);
            logger.info("Schema loaded successfully from path: {}", schemaPath);
            return JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V201909).getSchema(schemaNode);
        }
    }

    public Set<ValidationMessage> validate(JsonSchema schema, String rawJson) {
        logger.debug("Validating JSON payload: {}", rawJson);
        try {
            Set<ValidationMessage> messages = schema.validate(objectMapper.readTree(rawJson));
            if (!messages.isEmpty()) {
                logger.error("Validation errors found: {}", messages);
            } else {
                logger.debug("JSON validated successfully.");
            }
            return messages;
        } catch (Exception e) {
            logger.error("JSON validation failed.", e);
            throw new RuntimeException("JSON validation failed", e);
        }
    }
}
