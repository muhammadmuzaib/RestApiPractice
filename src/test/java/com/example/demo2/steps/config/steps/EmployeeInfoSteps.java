package com.example.demo2.steps.config.steps;

import com.example.demo2.core.service.EmployeeServiceImpl;
import com.example.demo2.steps.config.steps.util.RestTemplateProvider;
import com.example.demo2.steps.config.steps.util.ScenarioContext;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class EmployeeInfoSteps {
    private final ScenarioContext scenarioContext;
    private final EmployeeServiceImpl employeeServiceImpl;
    private final String BASE_URL = "http://localhost:8080";

    @Autowired
    public EmployeeInfoSteps(ScenarioContext scenarioContext, EmployeeServiceImpl employeeServiceImpl) {
        this.scenarioContext = scenarioContext;
        this.employeeServiceImpl = employeeServiceImpl;
    }

    @When("the client sends a GET request to {string}")
    public void theClientSendsAGetRequestTo(String endpoint) {
        RestTemplate restTemplate = RestTemplateProvider.getRestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                BASE_URL + endpoint,
                HttpMethod.GET,
                requestEntity,
                String.class
        );
        scenarioContext.setLatestResponse(response);
    }

    @Then("the response should contain employee first name {string}")
    public void theResponseShouldContainEmployeeFirstName(String expectedFirstName) {
        ResponseEntity<String> latestResponse = scenarioContext.getLatestResponse();
        assertThat(latestResponse.getBody(), containsString(expectedFirstName));
    }

    @Then("the response should contain employee last name {string}")
    public void theResponseShouldContainEmployeeLastName(String expectedLastName) {
        ResponseEntity<String> latestResponse = scenarioContext.getLatestResponse();
        assertThat(latestResponse.getBody(), containsString(expectedLastName));
    }
}
