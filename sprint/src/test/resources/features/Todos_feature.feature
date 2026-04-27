
Feature: Todos Module Testing

  Background:
    Given the GoRest Todos API is accessible with a valid bearer token


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
      | Learn API        | pending   | 8448441 |
      | Write Tests      | completed | 8448441 |
      | Automation Task  | pending   | 8448441 |

  Scenario: TC-002 - Create Todo using Excel Data
    When I send POST request to create a todo with data from excel
    Then Response status code should be 201
    And Response status line contains "201 Created"
    And Response time less than 3000 ms
    And Response body contains field "title" with value from excel

  Scenario: TC-003 - Create Todo using DataTable
    When I send POST request to create a todo with the following details:
      | title        | status    | userId  |
      | DataTableTodo| completed | 8448441 |
    Then Response status code should be 201
    And Response status line contains "201 Created"
    And Response time less than 3000 ms
    And Response body contains field "title" with value "DataTableTodo"

  Scenario: TC-004 - Create Todo with Invalid User ID
    When I send POST request to create a todo with invalid userId "99999"
    Then Response status code should be 422
    And Response status line contains "Unprocessable Entity"
    And Response time less than 3000 ms
    And Response body contains an appropriate error message


  Scenario: TC-005 - Get Todo by Valid ID
    When I send GET todo request with id "102871"
    Then Response status code should be 200
    And Response status line contains "200 OK"
    And Response time less than 3000 ms
    And Validate "Todo" schema
    And Response body contains field "id" with value "102871"
    And Response body contains field "status" with value "pending"

  Scenario: TC-006 - Get Todo by Invalid ID
    When I send GET todo request with id "99999"
    Then Response status code should be 404
    And Response status line contains "Not Found"
    And Response time less than 3000 ms
    And Response body contains an appropriate error message

  Scenario: TC-007 - Get All Todos
    When I send GET request to fetch all todos
    Then Response status code should be 200
    And Response status line contains "200 OK"
    And Response time less than 3000 ms
    And Response body should be a JSON array
    And Validate "TodoList" schema


  Scenario: TC-008 - Update Todo using Excel Data
    When I send PUT request to update todo "102871" with data from excel
    Then Response status code should be 200
    And Response status line contains "200 OK"
    And Response time less than 3000 ms
    And Response body contains field "title" with value from excel

  Scenario: TC-009 - Update Todo using DataTable
    When I send PUT request to update todo "102871" with the following details:
      | title       | status    | userId  |
      | Updated Todo| completed | 8448441 |
    Then Response status code should be 200
    And Response status line contains "200 OK"
    And Response time less than 3000 ms
    And Response body contains field "title" with value "Updated Todo"

  Scenario: TC-010 - Update Todo with Invalid ID
    When I send PUT request to update todo "99999"
    Then Response status code should be 404
    And Response status line contains "Not Found"
    And Response time less than 3000 ms
    And Response body contains an appropriate error message


  Scenario: TC-011 - Delete Todo
    When I send DELETE request for todo "102871"
    Then Response status code should be 204
    And Response time less than 3000 ms


  Scenario: TC-012 - POST Todo with Invalid Token
    When I send POST request to create a todo with invalid token
    Then Response status code should be 401
    And Response status line contains "Unauthorized"
    And Response time less than 3000 ms
    And Response body indicates authentication or authorization failure

  Scenario: TC-013 - GET /todos with No Auth returns 401
    When I send GET request to fetch all todos with no auth
    Then Response status code should be 401
    And Response status line contains "Unauthorized"
    And Response time less than 3000 ms
    And Response body indicates authentication or authorization failure
