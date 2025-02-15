package com.example.demo2.core.service;

import com.example.demo2.shell.dto.request.EmployeeLoginRequestDto;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

@Service
public class DtoConversionService {

    private static final Logger logger = LogManager.getLogger(DtoConversionService.class);
    private final JsonResponseService responseService;

    public DtoConversionService(JsonResponseService responseService) {
        this.responseService = responseService;
    }

    public <T> T convertToDto(String rawJson, String correlationId, Class<T> EmployeeRequestDto) {
        try {
            T dto = responseService.parseJsonToDto(rawJson, EmployeeRequestDto);
            logger.info("Parsed DTO: {}", dto);
            return dto;
        } catch (Exception e) {
            logger.error("Error parsing JSON for correlationId {}: {}", correlationId, e.getMessage());
            return null;
        }
    }

}
