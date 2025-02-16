Feature: Employee Deletion API

  Scenario: Successfully delete an existing employee
    Given an employee exists with username "admin"
    When the client sends a DELETE request to "/api/employee/delete/admin"
    Then the response status code should be 200
    And the response body should contain "Employee deleted successfully"

  Scenario: Attempt to delete a non-existent employee
    Given no employee exists with username "nonexistent"
    When the client sends a DELETE request to "/api/employee/delete/nonexistent"
    Then the response status code should be 404
    And the response body should contain "Employee not found"
