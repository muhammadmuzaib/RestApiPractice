package com.example.demo2.steps.config.steps.util;

import com.example.demo2.core.service.EmployeeService;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;


public class CommonSteps {

    private final ScenarioContext scenarioContext;
    private final EmployeeService employeeService;
    private final String BASE_URL = "http://localhost:8080";

    @Autowired
    public CommonSteps(ScenarioContext scenarioContext, EmployeeService employeeService) {
        this.scenarioContext = scenarioContext;
        this.employeeService = employeeService;
    }

    @Given("an employee exists with username {string}")
    public void anEmployeeExistsWithUsername(String username) {
        if (!employeeService.employeeExists(username)) {
            employeeService.createEmployee(username, "defaultPassword", "DefaultFirstName", "DefaultLastName");
        }
    }

    @Given("no employee exists with username {string}")
    public void noEmployeeExistsWithUsername(String username) {
        if (employeeService.employeeExists(username)) {
            employeeService.deleteEmployee(username);
        }
    }

    @When("the client sends a POST request to {string} with JSON body:")
    public void theClientSendsAPostRequestToWithJSONBody(String endpoint, String body) {
        RestTemplate restTemplate = RestTemplateProvider.getRestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        HttpEntity<String> requestEntity = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                BASE_URL + endpoint,
                HttpMethod.POST,
                requestEntity,
                String.class
        );
        System.out.println("DEBUG status code: " + response.getStatusCode());
        System.out.println("DEBUG body: " + response.getBody());
        scenarioContext.setLatestResponse(response);
    }

    @Then("the response status code should be {int}")
    public void theResponseStatusCodeShouldBe(int expectedStatusCode) {
        ResponseEntity<String> latestResponse = scenarioContext.getLatestResponse();
        assertThat(
                "Expected HTTP status code " + expectedStatusCode + " but got " + latestResponse.getStatusCode().value(),
                latestResponse.getStatusCode().value(),
                is(expectedStatusCode)
        );
    }

    @Then("the response body should contain {string}")
    public void theResponseBodyShouldContain(String expectedText) {
        ResponseEntity<String> latestResponse = scenarioContext.getLatestResponse();
        assertThat(
                "Expected response body to contain '" + expectedText + "', but was:\n" + latestResponse.getBody(),
                latestResponse.getBody(),
                containsString(expectedText)
        );
    }
}