package com.example.demo2.shell.service;

public interface DtoConversionService {

    /**
     * Converts a raw JSON string to a DTO of the specified type.
     *
     * @param rawJson       the JSON string to convert
     * @param correlationId the correlation ID for logging or tracking
     * @param dtoClass      the target DTO class
     * @param <T>           the type of the DTO
     * @return the converted DTO, or {@code null} if the conversion fails
     */
    <T> T convertToDto(String rawJson, String correlationId, Class<T> dtoClass);
}
