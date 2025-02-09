package com.example.demo2.steps.config;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = "src/test/resources/features",
        glue = "com.example.demo2.steps",
        plugin = {"pretty", "html:target/cucumber-reports.html"}
)
public class CucumberIntegrationTest {
}
