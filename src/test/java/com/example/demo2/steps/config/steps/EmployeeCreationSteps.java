package com.example.demo2.steps.config.steps;

import com.example.demo2.steps.config.steps.util.RestTemplateProvider;
import com.example.demo2.steps.config.steps.util.ScenarioContext;
import io.cucumber.java.en.When;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;


public class EmployeeCreationSteps {

    private final ScenarioContext scenarioContext;
    private final String BASE_URL = "http://localhost:8080";

    public EmployeeCreationSteps(ScenarioContext scenarioContext) {
        this.scenarioContext = scenarioContext;
    }

    @When("the client sends a PUT request to {string} with JSON body:")
    public void theClientSendsAPutRequestToWithJSONBody(String endpoint, String body) {
        RestTemplate restTemplate = RestTemplateProvider.getRestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        HttpEntity<String> requestEntity = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                BASE_URL + endpoint,
                HttpMethod.PUT,
                requestEntity,
                String.class
        );
        scenarioContext.setLatestResponse(response);
    }
}