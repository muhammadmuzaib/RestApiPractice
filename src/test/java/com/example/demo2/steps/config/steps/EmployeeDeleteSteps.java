package com.example.demo2.steps.config.steps;

import com.example.demo2.steps.config.steps.util.RestTemplateProvider;
import com.example.demo2.steps.config.steps.util.ScenarioContext;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

public class EmployeeDeleteSteps {

    private final ScenarioContext scenarioContext;
    private final String BASE_URL = "http://localhost:8080";

    @Autowired
    public EmployeeDeleteSteps(ScenarioContext scenarioContext) {
        this.scenarioContext = scenarioContext;
    }

    @When("the client sends a DELETE request to {string}")
    public void theClientSendsADELETERequestTo(String endpoint) {
        RestTemplate restTemplate = RestTemplateProvider.getRestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                BASE_URL + endpoint,
                HttpMethod.DELETE,
                requestEntity,
                String.class
        );
        scenarioContext.setLatestResponse(response);
    }
}