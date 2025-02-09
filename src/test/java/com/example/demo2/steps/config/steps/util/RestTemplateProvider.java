package com.example.demo2.steps.config.steps.util;

import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;

public class RestTemplateProvider {

    public static RestTemplate getRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();

        // Disable the default error handling for 4xx/5xx
        restTemplate.setErrorHandler(new ResponseErrorHandler() {

            @Override
            public boolean hasError(ClientHttpResponse response) throws IOException {
                return false;
            }

            @Override
            public void handleError(URI url, HttpMethod method, ClientHttpResponse response) throws IOException {
                ResponseErrorHandler.super.handleError(url, method, response);
            }
        });
        return restTemplate;
    }
}
