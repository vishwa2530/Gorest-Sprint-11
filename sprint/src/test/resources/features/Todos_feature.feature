# ---------------------------------------------------------
# Author : Kamesh
# Module : Todos
# Description : Testing Todos Module in GoRest API
# ---------------------------------------------------------

Feature: Todos Module Testing

  Background:
    Given the GoRest Todos API is accessible with a valid bearer token

  # ── POST /todos ─────────────────────────────────────────

  Scenario Outline: TC-001 - Create Todo with Valid Payload
    When I send POST request to create a todo "<title>" "<status>" "<userId>"
    Then Response status code should be 201
    And Response status line contains "201 Created"
    And Response time less than 3000 ms
    And Response body contains field "title" with value "<title>"
    And Response body contains field "status" with value "<status>"
    And Response body contains generated todo "id"

    Examples:
      | title            | status    | userId  |
      | Learn API        | pending   | 8445087 |
      | Write Tests      | completed | 8445087 |
      | Automation Task  | pending   | 8445087 |

  Scenario: TC-002 - Create Todo with Invalid User ID
    When I send POST request to create a todo with invalid userId "99999"
    Then Response status code should be 422
    And Response status line contains "Unprocessable Entity"
    And Response time less than 3000 ms
    And Response body contains an appropriate error message

  Scenario: TC-003 - Create Todo with Empty Payload
    When I send POST request to create a todo with empty payload
    Then Response status code should be 422
    And Response status line contains "Unprocessable Entity"
    And Response time less than 3000 ms
    And Response body indicates validation error

  # ── GET /todos ─────────────────────────────────────────

  Scenario: TC-004 - Get Todo by Valid ID
    When I send GET todo request with id "1"
    Then Response status code should be 200
    And Response status line contains "200 OK"
    And Response time less than 3000 ms
    And Response body contains field "id" with value "1"

  Scenario: TC-005 - Get Todo by Invalid ID
    When I send GET todo request with id "99999"
    Then Response status code should be 404
    And Response status line contains "Not Found"
    And Response time less than 3000 ms
    And Response body contains an appropriate error message

  Scenario: TC-006 - Get All Todos
    When I send GET request to fetch all todos
    Then Response status code should be 200
    And Response status line contains "200 OK"
    And Response time less than 3000 ms
    And Response body should be a JSON array

  # ── PUT /todos/{id} ───────────────────────────────────

  Scenario: TC-007 - Update Todo with Valid Data
    When I send PUT request to update todo "1" with valid data
    Then Response status code should be 200
    And Response status line contains "200 OK"
    And Response time less than 3000 ms
    And Response body contains updated todo details

  Scenario: TC-008 - Update Todo with Invalid ID
    When I send PUT request to update todo "99999"
    Then Response status code should be 404
    And Response status line contains "Not Found"
    And Response time less than 3000 ms
    And Response body contains an appropriate error message

  # ── DELETE /todos/{id} ─────────────────────────────────

  Scenario: TC-009 - Delete Todo with Valid ID
    When I send DELETE request for todo "1"
    Then Response status code should be 204
    And Response time less than 3000 ms

  Scenario: TC-010 - Get Deleted Todo
    Given todo "1" has already been deleted
    When I send GET todo request with id "1"
    Then Response status code should be 404
    And Response status line contains "Not Found"
    And Response time less than 3000 ms

  # ── AUTHENTICATION ───────────────────────────────────

  Scenario: TC-011 - POST Todo with Invalid Token
    When I send POST request to create a todo with invalid token
    Then Response status code should be 401
    And Response status line contains "Unauthorized"
    And Response time less than 3000 ms

  Scenario: TC-012 - GET Todos with Invalid Token
    When I send GET request to fetch todos with invalid token
    Then Response status code should be 401
    And Response status line contains "Unauthorized"
    And Response time less than 3000 ms