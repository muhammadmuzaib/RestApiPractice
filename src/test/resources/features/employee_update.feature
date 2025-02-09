Feature: Employee Update API

  Scenario: Successfully update an existing employee
    Given an employee exists with username "user1"
    When the client sends a POST request to "/api/employee/update/user1" with JSON body:
      """
      {
        "password": "newPassword",
        "firstName": "UpdatedFirstName",
        "lastName": "UpdatedLastName"
      }
      """
    Then the response status code should be 200
    And the response body should contain "Employee updated successfully"

  Scenario: Update employee with invalid JSON schema
    Given an employee exists with username "user1"
    When the client sends a POST request to "/api/employee/update/user1" with JSON body:
      """
      {
        "password": "newPassword",
        "firstName": 12345,
        "lastName": "UpdatedLastName"
      }
      """
    Then the response status code should be 400
    And the response body should contain "validationErrors"

  Scenario: Attempt to update a non-existent employee
    Given no employee exists with username "nonexistent"
    When the client sends a POST request to "/api/employee/update/nonexistent" with JSON body:
      """
      {
        "password": "newPassword",
        "firstName": "FirstName",
        "lastName": "LastName"
      }
      """
    Then the response status code should be 404
    And the response body should contain "Employee not found"
