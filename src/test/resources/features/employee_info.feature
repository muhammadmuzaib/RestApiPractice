Feature: Employee Information API

  Scenario: Retrieve employee details successfully
    Given an employee exists with username "user1"
    When the client sends a GET request to "/api/employee/get-info/user1"
    Then the response status code should be 200
    And the response should contain employee first name "john"
    And the response should contain employee last name "doe"

  Scenario: Employee not found
    Given no employee exists with username "unknown_user"
    When the client sends a GET request to "/api/employee/get-info/unknown_user"
    Then the response status code should be 404
