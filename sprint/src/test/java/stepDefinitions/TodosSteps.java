// ---------------------------------------------------------
// Author : Kamesh
// Module : Todos
// Description : Step Definitions for Todos Module in GoRest API
// ---------------------------------------------------------

package stepDefinitions;

import base.BaseTest;
import endpoints.GoRestEndpoints;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import utils.DataUtility;

public class TodosSteps extends BaseTest {

    Response response;

    // ── TC-001 : Scenario Outline - Create Todo ─────────────────────────

    @When("I send POST request to create a todo {string} {string} {string}")
    public void createTodo(String title, String status, String userId) {

        String requestBody = DataUtility.buildTodoJson(title, status, Integer.parseInt(userId));

        response = RestAssured
                .given()
                .spec(requestSpec)
                .body(requestBody)
                .when()
                .post(GoRestEndpoints.CREATE_TODO);

        Assertions.setResponse(response);
    }

    // ── TC-002 : Invalid User ID ───────────────────────────────────────

    @When("I send POST request to create a todo with invalid userId {string}")
    public void createTodoInvalidUser(String userId) {

        String requestBody = DataUtility.buildTodoJson("Test Todo", "pending", Integer.parseInt(userId));

        response = RestAssured
                .given()
                .spec(requestSpec)
                .body(requestBody)
                .when()
                .post(GoRestEndpoints.CREATE_TODO);

        Assertions.setResponse(response);
    }

    // ── TC-003 : Empty Payload ─────────────────────────────────────────

    @When("I send POST request to create a todo with empty payload")
    public void createTodoEmpty() {

        String requestBody = "{ }";

        response = RestAssured
                .given()
                .spec(requestSpec)
                .body(requestBody)
                .when()
                .post(GoRestEndpoints.CREATE_TODO);

        Assertions.setResponse(response);
    }

    // ── TC-004 & TC-005 : GET Todo by ID ──────────────────────────────

    @When("I send GET todo request with id {string}")
    public void getTodo(String id) {

        response = RestAssured
                .given()
                .spec(requestSpec)
                .pathParam("id", id)
                .when()
                .get(GoRestEndpoints.GET_TODO_BY_ID);

        Assertions.setResponse(response);
    }

    // ── TC-006 : Get All Todos ───────────────────────────────────────

    @When("I send GET request to fetch all todos")
    public void getAllTodos() {

        response = RestAssured
                .given()
                .spec(requestSpec)
                .when()
                .get(GoRestEndpoints.GET_ALL_TODOS);

        Assertions.setResponse(response);
    }

    // ── TC-007 : PUT Update Todo ─────────────────────────────────────

    @When("I send PUT request to update todo {string} with valid data")
    public void updateTodo(String id) {

        String requestBody = DataUtility.buildTodoJson("Updated Todo", "completed", 8445087);

        response = RestAssured
                .given()
                .spec(requestSpec)
                .pathParam("id", id)
                .body(requestBody)
                .when()
                .put(GoRestEndpoints.UPDATE_TODO);

        Assertions.setResponse(response);
    }

    // ── TC-008 : PUT Invalid ID ──────────────────────────────────────

    @When("I send PUT request to update todo {string}")
    public void updateTodoInvalid(String id) {

        String requestBody = DataUtility.buildTodoJson("Test", "pending", 8445087);

        response = RestAssured
                .given()
                .spec(requestSpec)
                .pathParam("id", id)
                .body(requestBody)
                .when()
                .put(GoRestEndpoints.UPDATE_TODO);

        Assertions.setResponse(response);
    }

    // ── TC-009 : DELETE Todo ─────────────────────────────────────────

    @When("I send DELETE request for todo {string}")
    public void deleteTodo(String id) {

        response = RestAssured
                .given()
                .spec(requestSpec)
                .pathParam("id", id)
                .when()
                .delete(GoRestEndpoints.DELETE_TODO);

        Assertions.setResponse(response);
    }

    // ── TC-010 : Given Deleted Todo ─────────────────────────────────

    @Given("todo {string} has already been deleted")
    public void deletedTodo(String id) {

        RestAssured
                .given()
                .spec(requestSpec)
                .pathParam("id", id)
                .when()
                .delete(GoRestEndpoints.DELETE_TODO);
    }

    // ── TC-011 : Invalid Token POST ─────────────────────────────────

    @When("I send POST request to create a todo with invalid token")
    public void invalidTokenPost() {

        String requestBody = DataUtility.buildTodoJson("Test", "pending", 1);

        response = RestAssured
                .given()
                .spec(requestSpec)
                .header("Authorization", "Bearer invalid_token")
                .body(requestBody)
                .when()
                .post(GoRestEndpoints.CREATE_TODO);

        Assertions.setResponse(response);
    }

    // ── TC-012 : Invalid Token GET ──────────────────────────────────

    @When("I send GET request to fetch todos with invalid token")
    public void invalidTokenGet() {

        response = RestAssured
                .given()
                .spec(requestSpec)
                .header("Authorization", "Bearer invalid_token")
                .when()
                .get(GoRestEndpoints.GET_ALL_TODOS);

        Assertions.setResponse(response);
    }
}