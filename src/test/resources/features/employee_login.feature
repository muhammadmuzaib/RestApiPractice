Feature: Employee Login API

  Scenario: Successful Login
    Given the employee with username "admin" and password "password" is a valid
    When the client sends a POST request to "/api/employee/login" with JSON body:
      """
      {
        "username": "admin",
        "password": "password"
      }
      """
    Then the response status code should be 200
    And the response body should contain "User authenticated"

  Scenario: Invalid Credentials
    Given the employee with username "wrong_user" and password "wrong_password" is invalid
    When the client sends a POST request to "/api/employee/login" with JSON body:
      """
      {
        "username": "wrong_user",
        "password": "wrong_password"
      }
      """
    Then the response status code should be 401
    And  the response body should contain "Invalid credentials"

