Feature: Employee Deletion API

  Scenario: Successfully delete an existing employee
    Given an employee exists with username "user1"
    When the client sends a DELETE request to "/api/employees/delete/user1"
    Then the response status code should be 200
    And the response body should contain "Employee deleted successfully"

  Scenario: Attempt to delete a non-existent employee
    Given no employee exists with username "nonexistent"
    When the client sends a DELETE request to "/api/employees/delete/nonexistent"
    Then the response status code should be 404
    And the response body should contain "Employee not found"
