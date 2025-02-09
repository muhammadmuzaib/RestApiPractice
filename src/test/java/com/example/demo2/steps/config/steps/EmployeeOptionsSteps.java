package com.example.demo2.steps.config.steps;

import com.example.demo2.steps.config.steps.util.RestTemplateProvider;
import com.example.demo2.steps.config.steps.util.ScenarioContext;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;


public class EmployeeOptionsSteps {

    private final ScenarioContext scenarioContext;
    private final String BASE_URL = "http://localhost:8080";

    public EmployeeOptionsSteps(ScenarioContext scenarioContext) {
        this.scenarioContext = scenarioContext;
    }

    @When("the client sends an OPTIONS request to {string}")
    public void theClientSendsAnOPTIONSRequestTo(String endpoint) {
        RestTemplate restTemplate = RestTemplateProvider.getRestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                BASE_URL + endpoint,
                HttpMethod.OPTIONS,
                requestEntity,
                String.class
        );
        scenarioContext.setLatestResponse(response);
    }

    @Then("the response header {string} should be {string}")
    public void theResponseHeaderShouldBe(String headerName, String expectedValue) {
        String headerValue = scenarioContext.getLatestResponse().getHeaders().getFirst(headerName);
        assertThat(
                "Expected header " + headerName + " to be " + expectedValue + " but was " + headerValue,
                headerValue,
                is(expectedValue)
        );
    }
}