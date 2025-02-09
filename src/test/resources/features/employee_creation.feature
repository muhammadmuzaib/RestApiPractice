Feature: Employee Creation API

  Scenario: Successfully create a new employee
    Given no employee exists with username "newUser"
    When the client sends a PUT request to "/api/employee/create/newUser" with JSON body:
      """
      {
        "password": "newPassword",
        "firstName": "New",
        "lastName": "User"
      }
      """
    Then the response status code should be 200
    And the response body should contain "Employee created"

  Scenario: Conflict when creating an already existing employee
    Given an employee exists with username "existingUser"
    When the client sends a PUT request to "/api/employee/create/existingUser" with JSON body:
      """
      {
        "password": "newPassword",
        "firstName": "Existing",
        "lastName": "User"
      }
      """
    Then the response status code should be 409
    And the response body should contain "Employee already exists"

  Scenario: Create employee with invalid JSON schema
    Given no employee exists with username "badUser"
    When the client sends a PUT request to "/api/employee/create/badUser" with JSON body:
      """
      {
        "password": "newPassword",
        "firstName": 12345,
        "lastName": "User"
      }
      """
    Then the response status code should be 400
    And the response body should contain "validationErrors"
