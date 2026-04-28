# ---------------------------------------------------------
# Author : Jishwa
# Module : Comments
# Description : Testing Comments Module in GoRest API
# ---------------------------------------------------------

Feature: Comments Module Testing

  Background:
    Given the GoRest Comments API is accessible with a valid bearer token

  # ── POST /comments ──────────────────────────────────────────────────────────

  Scenario: TC-001 - Create Comment with Valid Payload
    When I Send POST request to create a comment with data from excel
    Then Response status code should be 201
    And Response status line contains "201 Created"
    And Response time less than 5000 ms
    And Response body contains field "name" with value from excel
    And Response body contains field "body" with value from excel
    And Response body contains generated comment "id"

  Scenario: TC-002 - Create Comment with Non-Existing Post ID returns 422
    When I Send POST request to create a comment with non-existing postId from excel
    Then Response status code should be 422
    And Response status line contains "Unprocessable Entity"
    And Response time less than 5000 ms
    And Response body contains an appropriate error message

  Scenario: TC-003 - Create Comment with Empty or Duplicate Payload returns 422
    When I Send POST request to create a comment with empty or duplicate data from excel
    Then Response status code should be 422
    And Response status line contains "Unprocessable Entity"
    And Response time less than 5000 ms
    And Response body indicates duplicate or incorrect data issue

  # ── GET /comments and GET /comments/{id} ────────────────────────────────────

  Scenario Outline: TC-004 - Get Comment by Valid Comment ID returns 200 OK
    When I Send GET comment request with comment id "<commentId>"
    Then Response status code should be 200
    And Response status line contains "200 OK"
    And Response time less than 5000 ms
    And Validate "Comment" schema
    And Response body contains field "id" with value "<commentId>"

    Examples:
      | commentId |
      | 278134    |

  Scenario: TC-005 - Get Comment by Non-Existing Comment ID returns 404 Not Found
    When I Send GET comment request with comment id "99999"
    Then Response status code should be 404
    And Response status line contains "Not Found"
    And Response time less than 5000 ms
    And Response body contains an appropriate error message

  Scenario: TC-006 - Get All Comments returns 200 OK with JSON array
    When I Send GET request to fetch all comments
    Then Response status code should be 200
    And Response status line contains "200 OK"
    And Response time less than 5000 ms
    And Response body should be a JSON array
    And Validate "CommentList" schema

  # ── PUT /comments/{id} ──────────────────────────────────────────────────────

  Scenario: TC-007 - Full Update Comment with Valid Payload from Excel returns 200 OK
    When I Send PUT request to update comment "278134" with data from excel
    Then Response status code should be 200
    And Response status line contains "200 OK"
    And Response time less than 5000 ms
    And Response body contains field "name" with value from excel

  Scenario: TC-008 - Full Update Comment with Non-Existing Comment ID returns 404
    When I Send PUT request to update comment "99999" with the following details:
      | postId | name         | email               | body                 |
      | 278134 | Updated Name | updated@example.com | Updated comment body |
    Then Response status code should be 404
    And Response status line contains "Not Found"
    And Response time less than 5000 ms
    And Response body contains an appropriate error message

  Scenario: TC-009 - Full Update Comment with Invalid Data Types returns 422
    When I Send PUT request to update comment "278134" with the following details:
      | postId       | name  | email        | body |
      | not-a-number | 12345 | not-an-email | null |
    Then Response status code should be 422
    And Response status line contains "Unprocessable Entity"
    And Response time less than 5000 ms
    And Response body indicates invalid data error

  # ── DELETE /comments/{id} ───────────────────────────────────────────────────

  Scenario Outline: TC-010 - Delete Comment with Valid Comment ID returns 204
    When I Send DELETE request for comment "<commentId>"
    Then Response status code should be 204
    And Response time less than 5000 ms
    And Response body confirms successful deletion

    Examples:
      | commentId |
      | 278134    |

  Scenario: TC-011 - Delete Comment with Non-Existing Comment ID returns 404
    When I Send DELETE request for comment "99999"
    Then Response status code should be 404
    And Response status line contains "Not Found"
    And Response time less than 5000 ms
    And Response body contains an appropriate error message

  Scenario: TC-012 - Get Deleted Comment returns 404 Not Found
    Given comment "278134" has already been deleted
    When I Send GET comment request with comment id "278134"
    Then Response status code should be 404
    And Response status line contains "Not Found"
    And Response time less than 5000 ms
    And Response body indicates the comment no longer exists

  # ── AUTHENTICATION ───────────────────────────────────────────────────────────

  Scenario: TC-013 - POST /comments with Invalid or Expired Token returns 401
    When I Send POST request to create a comment with invalid or expired token "invalid_token"
    Then Response status code should be 401
    And Response status line contains "Unauthorized"
    And Response time less than 5000 ms
    And Response body indicates authentication or authorization failure

  Scenario: TC-014 - DELETE /comments/{id} with Invalid or Expired Token returns 401
    When I Send DELETE request for comment "278134" with invalid or expired token "invalid_token"
    Then Response status code should be 401
    And Response status line contains "Unauthorized"
    And Response time less than 5000 ms
    And Response body indicates authentication or authorization failure

  Scenario: TC-015 - GET /comments with Invalid or Expired Token returns 401
    When I Send GET request to fetch all comments with invalid or expired token "invalid_token"
    Then Response status code should be 401
    And Response status line contains "Unauthorized"
    And Response time less than 5000 ms
    And Response body indicates authentication or authorization failure