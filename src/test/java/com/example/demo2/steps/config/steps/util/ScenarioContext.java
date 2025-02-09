package com.example.demo2.steps.config.steps.util;

import io.cucumber.spring.ScenarioScope;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

/**
 * Stores shared data for a single Scenario.
 */
@Component
@ScenarioScope
public class ScenarioContext {

    private ResponseEntity<String> latestResponse;

    public ResponseEntity<String> getLatestResponse() {
        return latestResponse;
    }

    public void setLatestResponse(ResponseEntity<String> latestResponse) {
        this.latestResponse = latestResponse;
    }

    // You can also store other scenario-level data here, if needed
    // e.g., user credentials, tokens, etc.
}