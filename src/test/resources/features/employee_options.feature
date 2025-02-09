Feature: Employee Options API
  As a client, I want to know which HTTP methods are supported by the Employee API

  Scenario: List supported HTTP methods
    When the client sends an OPTIONS request to "/api/employee-methods"
    Then the response status code should be 200
    And the response header "Allow" should be "GET, POST, PUT, DELETE, OPTIONS"
    And the response body should contain "GET"
    And the response body should contain "POST"
    And the response body should contain "PUT"
    And the response body should contain "DELETE"
    And the response body should contain "OPTIONS"
