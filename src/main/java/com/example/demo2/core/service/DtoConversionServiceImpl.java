package com.example.demo2.core.service;

import com.example.demo2.shell.errors.DtoConversionException;
import com.example.demo2.shell.service.DtoConversionService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

@Service
public class DtoConversionServiceImpl implements DtoConversionService {

    private static final Logger logger = LogManager.getLogger(DtoConversionServiceImpl.class);
    private final JsonResponseServiceImpl responseService;

    public DtoConversionServiceImpl(JsonResponseServiceImpl responseService) {
        this.responseService = responseService;
    }

    @Override
    public <T> T convertToDto(String rawJson, String correlationId, Class<T> employeeRequestDto) {
        try {
            T dto = responseService.parseJsonToDto(rawJson, employeeRequestDto);
            logger.info("Parsed DTO: {}", dto);
            return dto;
        } catch (Exception e) {
            logger.error("Error parsing JSON for correlationId {}: {}", correlationId, e.getMessage());
            throw new DtoConversionException("Error parsing JSON for correlationId", e);
        }
    }

}
