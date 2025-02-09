package com.example.demo2.steps.config.steps;

import com.example.demo2.steps.config.steps.util.ScenarioContext;

public class EmployeeUpdateSteps {

    private final ScenarioContext scenarioContext;
    private final String BASE_URL = "http://localhost:8080";

    public EmployeeUpdateSteps(ScenarioContext scenarioContext) {
        this.scenarioContext = scenarioContext;
    }
}