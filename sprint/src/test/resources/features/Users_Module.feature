# ---------------------------------------------------------
# Author : Vishwa
# Module : Users
# Description : Testing Users Module in GoRest API
# ---------------------------------------------------------

Feature: Users Module Testing

  Background:
    Given the GoRest Users API is accessible with a valid bearer token

  # ── POST /users ─────────────────────────────────────────

  # Scenario Outline (Static Multiple Data)
  Scenario Outline: TC-001 - Create User with Multiple Valid Payloads
    When I Send POST request to create a user "<name>" "<email>" "<gender>" "<status>"
    Then Response status code should be 201
    And Response status line contains "201 Created"
    And Response body contains field "name" with value "<name>"
    And Response body contains field "email" with value "<email>"
    And Response body contains generated user "id"

    Examples:
      | name        | email               | gender | status |
      | Arun Kumar  | arun101@test.com   | male   | active |
      | Meena Devi  | meena102@test.com  | female | active |

  # Excel Driven
  Scenario: TC-002 - Create User with Valid Payload from Excel
    When I fetch user data from excel
    And I Send POST request to create user with excel data
    Then Response status code should be 201
    And Response status line contains "201 Created"
    And Response body contains values from excel

  Scenario: TC-003 - Create User with Duplicate Email from Excel
    When I fetch duplicate user data from excel
    And I Send POST request to create user
    Then Response status code should be 422
    And Response status line contains "Unprocessable Entity"
    And Response body contains duplicate error

  Scenario: TC-004 - Create User with Invalid Payload from Excel
    When I fetch invalid user data from excel
    And I Send POST request to create user
    Then Response status code should be 422
    And Response status line contains "Unprocessable Entity"
    And Response body contains validation error

  # Data Table
  Scenario: TC-005 - Create User using Data Table
    When I Send POST request with the following user details:
      | name       | email          | gender | status |
      | Ravi Kumar | ravi@test.com | male   | active |
    Then Response status code should be 201
    And Response body contains field "name" with value "Ravi Kumar"

  # ── GET /users ─────────────────────────────────────────

  Scenario: TC-006 - Get User by Valid ID from Excel
    When I fetch user id from excel
    And I Send GET request for that user
    Then Response status code should be 200
    And Response status line contains "200 OK"
    And Response body contains user details

  Scenario: TC-007 - Get User by Invalid ID
    When I Send GET request for user "999999"
    Then Response status code should be 404
    And Response status line contains "Not Found"

  Scenario: TC-008 - Get All Users
    When I Send GET request to fetch all users
    Then Response status code should be 401
    And Response status line contains "401 Unauthorized"
    And Response body should be a JSON array

  # ── PUT /users/{id} ─────────────────────────────────────

  Scenario: TC-009 - Update User with Excel Data
    When I fetch update user data from excel
    And I Send PUT request to update user
    Then Response status code should be 200
    And Response status line contains "200 OK"
    And Response body reflects updated values

  Scenario: TC-010 - Update User with Invalid ID
    When I Send PUT request for user "999999"
    Then Response status code should be 404
    And Response status line contains "Not Found"

  Scenario: TC-011 - Update User using Data Table
    When I Send PUT request with:
      | name        | email             | gender | status |
      | Updated Raj | updated@test.com | male   | active |
    Then Response status code should be 200
    And Response body reflects updated values

  # ── DELETE /users/{id} ──────────────────────────────────

  Scenario: TC-012 - Delete User using Excel Data
    When I fetch user id from excel
    And I Send DELETE request
    Then Response status code should be 204
    And Response status line contains "No Content"

  Scenario: TC-013 - Delete User with Invalid ID
    When I Send DELETE request for user "999999"
    Then Response status code should be 404
    And Response status line contains "Not Found"

  Scenario: TC-014 - Get Deleted User
    Given user from excel has already been deleted
    When I Send GET request for same user
    Then Response status code should be 404
    And Response status line contains "Not Found"

  # ── AUTHENTICATION ─────────────────────────────────────

  Scenario: TC-015 - Create User with Invalid Token
    When I Send POST request with invalid token
    Then Response status code should be 401
    And Response status line contains "Unauthorized"

  Scenario: TC-016 - Get Users with Invalid Token
    When I Send GET request with invalid token
    Then Response status code should be 401
    And Response status line contains "Unauthorized"

  Scenario: TC-017 - Delete User with Invalid Token
    When I Send DELETE request with invalid token
    Then Response status code should be 401
    And Response status line contains "Unauthorized"
    
    
    