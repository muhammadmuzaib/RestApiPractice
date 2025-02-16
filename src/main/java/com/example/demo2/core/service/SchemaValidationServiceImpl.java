package com.example.demo2.core.service;

import com.example.demo2.shell.errors.JsonValidationException;
import com.example.demo2.shell.errors.SchemaLoadException;
import com.example.demo2.shell.dto.response.ErrorResponse;
import com.example.demo2.shell.service.SchemaValidationService;
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
public class SchemaValidationServiceImpl implements SchemaValidationService {

    private static final Logger logger = LogManager.getLogger(SchemaValidationServiceImpl.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final JsonResponseServiceImpl responseService;


    @Autowired
    public SchemaValidationServiceImpl(JsonResponseServiceImpl responseService) throws IOException {
        this.responseService = responseService;
    }

    @Override
    public ResponseEntity<ErrorResponse> validateRequest(String rawJson, String correlationId, JsonSchema schema) {
        Set<ValidationMessage> errors = validate(schema, rawJson);
        if (!errors.isEmpty()) {
            logger.error("Validation errors for correlationId {}: {}", correlationId, errors);
            return responseService.validationErrorResponse(errors, correlationId);
        }
        return null;
    }

    @Override
    public JsonSchema loadSchema(String schemaPath) throws SchemaLoadException {
        logger.info("Loading schema from path: {}", schemaPath);
        try (InputStream schemaStream = getClass().getResourceAsStream(schemaPath)) {
            if (schemaStream == null) {
                logger.error("Schema not found at path: {}", schemaPath);
                throw new IllegalStateException("Missing schema: " + schemaPath);
            }
            JsonNode schemaNode = objectMapper.readTree(schemaStream);
            logger.info("Schema loaded successfully from path: {}", schemaPath);
            return JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V201909).getSchema(schemaNode);
        } catch (IOException e) {
            logger.error("IOException occurred while loading schema from path: {}", schemaPath, e);
            throw new SchemaLoadException("Error loading schema from: " + schemaPath, e);
        }
    }

    @Override
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
            throw new JsonValidationException("JSON validation failed", e);
        }
    }
}
