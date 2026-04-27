# ---------------------------------------------------------
# Author : Rajmohan
# Module : Posts
# Description : Testing Posts Module in GoRest API
# ---------------------------------------------------------
Feature: Posts Module Testing

  Background:
    Given the GoRest Posts API is accessible with a valid bearer token
  # ── POST /posts ───────────────────────────────────────────────────────────────

  Scenario Outline: TC-001 - Create Post with Valid Payload
    When I Send POST request to create a post "<title>" "<body>" "<userId>"
    Then Response status code should be 201
    And Response status line contains "201 Created"
    And Response time less than 3000 ms
    And Response body contains field "title" with value "<title>"
    And Response body contains field "body" with value "<body>"
    And Response body contains generated post "id"

    Examples:
      | title            | body                       | userId  |
      | My First Post    | This is the body of post 1 | 8445087 |
      | Another Post     | This is the body of post 2 | 8445087 |
      | Third Post Entry | This is the body of post 3 | 8445087 |

  Scenario: TC-002 - Create Post with Non-Existing User ID returns 422
    When I Send POST request to create a post with non-existing userId "99999"
    Then Response status code should be 422
    And Response status line contains "Unprocessable Entity"
    And Response time less than 3000 ms
    And Response body contains an appropriate error message

  Scenario: TC-003 - Create Post with Empty or Duplicate Payload returns 422
    When I Send POST request to create a post with empty title and body for userId "8445087"
    Then Response status code should be 422
    And Response status line contains "Unprocessable Entity"
    And Response time less than 3000 ms
    And Response body indicates duplicate or incorrect data issue
  # ── GET /posts and GET /posts/{id} ────────────────────────────────────────────

  Scenario: TC-004 - Get Post by Valid Post ID returns 200 OK
    When I Send GET post request with post id "278134"
    Then Response status code should be 200
    And Response status line contains "200 OK"
    And Response time less than 3000 ms
    And Validate "Post" schema
    And Response body contains field "id" with value "278134"

  Scenario: TC-005 - Get Post by Non-Existing Post ID returns 404 Not Found
    When I Send GET post request with post id "99999"
    Then Response status code should be 404
    And Response status line contains "Not Found"
    And Response time less than 3000 ms
    And Response body contains an appropriate error message

  Scenario: TC-006 - Get All Posts returns 200 OK with JSON array
    When I Send GET request to fetch all posts
    Then Response status code should be 200
    And Response status line contains "200 OK"
    And Response time less than 3000 ms
    And Response body should be a JSON array
    And Validate "PostList" schema
  # ── PUT /posts/{id} ───────────────────────────────────────────────────────────

  Scenario: TC-007 - Full Update Post with Valid Payload from Excel returns 200 OK
    When I Send PUT request to update post "278134" with data from excel
    Then Response status code should be 200
    And Response status line contains "200 OK"
    And Response time less than 3000 ms
    And Response body contains field "title" with value from excel

  Scenario: TC-008 - Full Update Post with Non-Existing Post ID returns 404
    When I Send PUT request to update post "99999" with the following details:
      | title         | body         | userId  |
      | Updated Title | Updated body | 8445087 |
    Then Response status code should be 404
    And Response status line contains "Not Found"
    And Response time less than 3000 ms
    And Response body contains an appropriate error message

  Scenario: TC-009 - Full Update Post with Invalid Data Types returns 422
    When I Send PUT request to update post "278134" with invalid data types
    Then Response status code should be 422
    And Response status line contains "Unprocessable Entity"
    And Response time less than 3000 ms
    And Response body contains an appropriate error message
  # ── DELETE /posts/{id} ───────────────────────────────────────────────────────

  Scenario: TC-010 - Delete Post with Valid Post ID returns 204
    When I Send DELETE request for post "278134"
    Then Response status code should be 204
    And Response time less than 3000 ms
    And Response body confirms successful deletion

  Scenario: TC-011 - Delete Post with Non-Existing Post ID returns 404
    When I Send DELETE request for post "99999"
    Then Response status code should be 404
    And Response status line contains "Not Found"
    And Response time less than 3000 ms
    And Response body contains an appropriate error message

  Scenario: TC-012 - Get Deleted Post returns 404 Not Found
    Given post "278134" has already been deleted
    When I Send GET post request with post id "278134"
    Then Response status code should be 404
    And Response status line contains "Not Found"
    And Response time less than 3000 ms
    And Response body indicates the post no longer exists
  # ── AUTHENTICATION ────────────────────────────────────────────────────────────

  Scenario: TC-013 - POST /posts with Invalid or Expired Token returns 401
    When I Send POST request to create a post with invalid or expired token
    Then Response status code should be 401
    And Response status line contains "Unauthorized"
    And Response time less than 3000 ms
    And Response body indicates authentication or authorization failure

  Scenario: TC-014 - DELETE /posts/{id} with Invalid or Expired Token returns 401
    When I Send DELETE request for post "1" with invalid or expired token
    Then Response status code should be 401
    And Response status line contains "Unauthorized"
    And Response time less than 3000 ms
    And Response body indicates authentication or authorization failure

  Scenario: TC-015 - GET /posts with No Auth returns 401
    When I Send GET request to fetch all posts with no auth
    Then Response status code should be 401
    And Response status line contains "Unauthorized"
    And Response time less than 3000 ms
    And Response body indicates authentication or authorization failure
