// ---------------------------------------------------------
// Author : Jishwa
// Module : Comments
// Description : Step Definitions for Comments Module in GoRest API
// ---------------------------------------------------------

package stepDefinitions;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

import java.util.List;
import java.util.Map;

import base.BaseTest;
import endpoints.ICommentEndpoint;
import hooks.Hooks;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import utils.DataUtility;
import utils.ExcelUtility;

public class CommentStep extends BaseTest {

    private Response response;
    private String excelName;

    // =========================================================================
    // WHEN / GIVEN  —  Request Steps
    // =========================================================================

    // ── TC-001 : Create Comment with Valid Payload - Excel Utility ─────────────

    @When("I Send POST request to create a comment with data from excel")
    public void createCommentWithExcel() {
        ExcelUtility.loadExcel("CommentsTestData.xlsx", "POST_VALID");
        excelName          = ExcelUtility.getCellData(1, 0);
        String email       = ExcelUtility.getCellData(1, 1);
        String body        = ExcelUtility.getCellData(1, 2);
        int    postId      = Integer.parseInt(ExcelUtility.getCellData(1, 3));

        String requestBody = DataUtility.buildCommentJson(excelName, email, body, postId);

        response = RestAssured
                .given()
                    .spec(requestSpec)
                    .body(requestBody)
                .when()
                    .post(ICommentEndpoint.CREATE_COMMENT);
    }

    // ── TC-002 : Create Comment with Non-Existing Post ID - Excel Utility ──────

    @When("I Send POST request to create a comment with non-existing postId from excel")
    public void createCommentWithInvalidPostIdFromExcel() {
        ExcelUtility.loadExcel("CommentsTestData.xlsx", "POST_INVALID_ID");
        String name   = ExcelUtility.getCellData(1, 0);
        String email  = ExcelUtility.getCellData(1, 1);
        String body   = ExcelUtility.getCellData(1, 2);
        int    postId = Integer.parseInt(ExcelUtility.getCellData(1, 3));

        String requestBody = DataUtility.buildCommentJson(name, email, body, postId);

        response = RestAssured
                .given()
                    .spec(requestSpec)
                    .body(requestBody)
                .when()
                    .post(ICommentEndpoint.CREATE_COMMENT);
    }

    // ── TC-003 : Create Comment with Empty or Duplicate Payload - Excel Utility ─

    @When("I Send POST request to create a comment with empty or duplicate data from excel")
    public void createCommentWithEmptyOrDuplicateDataFromExcel() {
        ExcelUtility.loadExcel("CommentsTestData.xlsx", "POST_INVALID_PAYLOAD");
        String name   = ExcelUtility.getCellData(1, 0);
        String email  = ExcelUtility.getCellData(1, 1);
        String body   = ExcelUtility.getCellData(1, 2);
        int    postId = Integer.parseInt(ExcelUtility.getCellData(1, 3));

        String requestBody = DataUtility.buildCommentJson(name, email, body, postId);

        response = RestAssured
                .given()
                    .spec(requestSpec)
                    .body(requestBody)
                .when()
                    .post(ICommentEndpoint.CREATE_COMMENT);
    }

    // ── TC-004 and TC-005 : Get Comment by ID - Scenario Outline ──────────────

    @When("I Send GET comment request with comment id {string}")
    public void getCommentById(String commentId) {
        response = RestAssured
                .given()
                    .spec(requestSpec)
                    .pathParam("id", commentId)
                .when()
                    .get(ICommentEndpoint.GET_COMMENT_BY_ID);
    }
    // ── TC-005 : Get Comment by Non-Existing Comment ID ───────────────────────────

@When("I Send GET comment request with comment id \"99999\"")
public void getCommentByInvalidId() {
    response = RestAssured
            .given()
                .spec(requestSpec)
                .pathParam("id", "99999")
            .when()
                .get(ICommentEndpoint.GET_COMMENT_BY_ID);
}

    // ── TC-006 : Get All Comments - Scenario Outline ───────────────────────────

    @When("I Send GET request to fetch all comments")
    public void getAllComments() {
        response = RestAssured
                .given()
                    .spec(requestSpec)
                .when()
                    .get(ICommentEndpoint.GET_ALL_COMMENTS);
    }

    // ── TC-007 : Full Update Comment with Valid Payload - DataTable ────────────

    @When("I Send PUT request to update comment {string} with the following details:")
    public void updateCommentWithDataTable(String commentId, DataTable dataTable) {
        Map<String, String> row = dataTable.asMaps().get(0);
        String requestBody = DataUtility.buildCommentJson(row);

        response = RestAssured
                .given()
                    .spec(requestSpec)
                    .pathParam("id", commentId)
                    .body(requestBody)
                .when()
                    .put(ICommentEndpoint.UPDATE_COMMENT);
    }

    // ── TC-009b : Partial Update Comment - DataTable ───────────────────────────

    @When("I Send PATCH request to update comment {string} with the following details:")
    public void patchCommentWithDataTable(String commentId, DataTable dataTable) {
        Map<String, String> row = dataTable.asMaps().get(0);
        String requestBody = DataUtility.buildCommentJson(row);

        response = RestAssured
                .given()
                    .spec(requestSpec)
                    .pathParam("id", commentId)
                    .body(requestBody)
                .when()
                    .patch(ICommentEndpoint.UPDATE_COMMENT);
    }

    // ── TC-010 and TC-011 : Delete Comment by ID - Scenario Outline ───────────

    @When("I Send DELETE request for comment {string}")
    public void deleteComment(String commentId) {
        response = RestAssured
                .given()
                    .spec(requestSpec)
                    .pathParam("id", commentId)
                .when()
                    .delete(ICommentEndpoint.DELETE_COMMENT);
    }

    // ── TC-012 : Given Pre-Condition - Delete Comment Before GET ──────────────

    @Given("comment {string} has already been deleted")
    public void commentHasAlreadyBeenDeleted(String commentId) {
        RestAssured
                .given()
                    .spec(requestSpec)
                    .pathParam("id", commentId)
                .when()
                    .delete(ICommentEndpoint.DELETE_COMMENT);
    }

    // ── TC-013 : POST with Invalid or Expired Token ───────────────────────────

    @When("I Send POST request to create a comment with invalid or expired token {string}")
    public void createCommentWithInvalidToken(String token) {
        String requestBody = DataUtility.buildCommentJson("Test", "test@test.com", "Body", 1);

        response = RestAssured
                .given()
                    .spec(requestSpec)
                    .header("Authorization", "Bearer " + token)
                    .body(requestBody)
                .when()
                    .post(ICommentEndpoint.CREATE_COMMENT);
    }

    // ── TC-014 : DELETE with Invalid or Expired Token ─────────────────────────

    @When("I Send DELETE request for comment {string} with invalid or expired token {string}")
    public void deleteCommentWithInvalidToken(String commentId, String token) {
        response = RestAssured
                .given()
                    .spec(requestSpec)
                    .header("Authorization", "Bearer " + token)
                    .pathParam("id", commentId)
                .when()
                    .delete(ICommentEndpoint.DELETE_COMMENT);
    }

    // ── TC-015 : GET All Comments with Invalid or Expired Token ───────────────

    @When("I Send GET request to fetch all comments with invalid or expired token {string}")
    public void getAllCommentsWithInvalidToken(String token) {
        response = RestAssured
                .given()
                    .spec(requestSpec)
                    .header("Authorization", "Bearer " + token)
                .when()
                    .get(ICommentEndpoint.GET_ALL_COMMENTS);
    }

    // =========================================================================
    // THEN  —  Assertion Steps
    // =========================================================================

    // ── Core Validations ──────────────────────────────────────────────────────

    @Then("Response status code should be {int}")
    public void statusCodeValidation(int expected) {
        int actual = response.getStatusCode();
        if (actual != expected) {
            String msg = "Status Code Failed: expected=" + expected + ", actual=" + actual;
            Hooks.scenario.log(msg);
            Hooks.softAssert.fail(msg);
        } else {
            Hooks.scenario.log("Status Code Passed: " + actual);
        }
    }

    @Then("Response status line contains {string}")
    public void statusLineValidation(String expectedText) {
        String actual = response.getStatusLine();
        if (!actual.contains(expectedText)) {
            String msg = "Status Line Failed: expected to contain '" + expectedText + "', actual='" + actual + "'";
            Hooks.scenario.log(msg);
            Hooks.softAssert.fail(msg);
        } else {
            Hooks.scenario.log("Status Line Passed: " + actual);
        }
    }

    @Then("Response time less than {int} ms")
    public void responseTimeLessThan(int expectedTime) {
        long actualTime = response.getTime();
        if (actualTime >= expectedTime) {
            String msg = "Response Time Failed: expected < " + expectedTime + " ms, actual=" + actualTime + " ms";
            Hooks.scenario.log(msg);
            Hooks.softAssert.fail(msg);
        } else {
            Hooks.scenario.log("Response Time Passed: " + actualTime + " ms");
        }
    }

    @Then("Validate {string} schema")
    public void validateSchema(String key) {
        try {
            response.then()
                    .assertThat()
                    .body(matchesJsonSchemaInClasspath("schema/" + key + "Schema.json"));
            Hooks.scenario.log("Schema Validation Passed: " + key);
        } catch (AssertionError e) {
            String msg = "Schema Validation Failed: " + e.getMessage();
            Hooks.scenario.log(msg);
            Hooks.softAssert.fail(msg);
        }
    }

    // ── Comment Field Validations ─────────────────────────────────────────────

    @Then("Response body contains field {string} with value {string}")
    public void responseBodyContainsFieldWithValue(String field, String expectedValue) {
        String actual = response.jsonPath().getString(field);
        if (actual == null || !actual.equals(expectedValue)) {
            String msg = "Field Validation Failed: field='" + field + "', expected='" + expectedValue + "', actual='" + actual + "'";
            Hooks.scenario.log(msg);
            Hooks.softAssert.fail(msg);
        } else {
            Hooks.scenario.log("Field Validation Passed: " + field + "=" + actual);
        }
    }

    @Then("Response body contains field {string} with value from excel")
    public void responseBodyContainsFieldWithValueFromExcel(String field) {
        String actual = response.jsonPath().getString(field);
        if (actual == null || !actual.equals(excelName)) {
            String msg = "Excel Field Validation Failed: field='" + field + "', expected from excel='" + excelName + "', actual='" + actual + "'";
            Hooks.scenario.log(msg);
            Hooks.softAssert.fail(msg);
        } else {
            Hooks.scenario.log("Excel Field Validation Passed: " + field + "=" + actual);
        }
    }

    @Then("Response body contains generated comment {string}")
    public void responseBodyContainsGeneratedField(String field) {
        Object value = response.jsonPath().get(field);
        if (value == null) {
            String msg = "Generated Field Validation Failed: '" + field + "' is missing in response body";
            Hooks.scenario.log(msg);
            Hooks.softAssert.fail(msg);
        } else {
            Hooks.scenario.log("Generated Field Validation Passed: " + field + "=" + value);
        }
    }

    @Then("Response body should be a JSON array")
    public void responseBodyShouldBeJsonArray() {
        try {
            List<?> list = response.jsonPath().getList("$");
            if (list == null || list.isEmpty()) {
                String msg = "JSON Array Validation Failed: response body is empty or not a JSON array";
                Hooks.scenario.log(msg);
                Hooks.softAssert.fail(msg);
            } else {
                Hooks.scenario.log("JSON Array Validation Passed: array size=" + list.size());
            }
        } catch (Exception e) {
            String msg = "JSON Array Validation Failed: " + e.getMessage();
            Hooks.scenario.log(msg);
            Hooks.softAssert.fail(msg);
        }
    }

    // ── Delete / Error / Auth Validations ─────────────────────────────────────

    @Then("Response body confirms successful deletion")
    public void responseBodyConfirmsDeletion() {
        int statusCode = response.getStatusCode();
        if (statusCode == 200 || statusCode == 204) {
            Hooks.scenario.log("Deletion Confirmation Passed: status=" + statusCode);
        } else {
            String msg = "Deletion Confirmation Failed: expected 200 or 204, actual=" + statusCode;
            Hooks.scenario.log(msg);
            Hooks.softAssert.fail(msg);
        }
    }

    @Then("Response body contains an appropriate error message")
    public void responseBodyContainsErrorMessage() {
        String body = response.getBody().asString();
        if (body == null || body.trim().isEmpty() || body.equals("{}")) {
            String msg = "Error Message Validation Failed: response body is empty or missing error details";
            Hooks.scenario.log(msg);
            Hooks.softAssert.fail(msg);
        } else {
            Hooks.scenario.log("Error Message Validation Passed: body=" + body);
        }
    }

    @Then("Response body indicates duplicate or incorrect data issue")
    public void responseBodyIndicatesDuplicateOrIncorrectData() {
        int statusCode = response.getStatusCode();
        String body = response.getBody().asString();
        if (statusCode == 400 || statusCode == 409) {
            Hooks.scenario.log("Duplicate/Incorrect Data Validation Passed: status=" + statusCode + ", body=" + body);
        } else {
            String msg = "Duplicate/Incorrect Data Validation Failed: expected 400 or 409, actual=" + statusCode;
            Hooks.scenario.log(msg);
            Hooks.softAssert.fail(msg);
        }
    }

    @Then("Response body indicates invalid data error")
    public void responseBodyIndicatesInvalidDataError() {
        int statusCode = response.getStatusCode();
        String body = response.getBody().asString();
        if (statusCode == 400 || statusCode == 422) {
            Hooks.scenario.log("Invalid Data Validation Passed: status=" + statusCode + ", body=" + body);
        } else {
            String msg = "Invalid Data Validation Failed: expected 400 or 422, actual=" + statusCode;
            Hooks.scenario.log(msg);
            Hooks.softAssert.fail(msg);
        }
    }

    @Then("Response body indicates the comment no longer exists")
    public void responseBodyIndicatesCommentNoLongerExists() {
        int statusCode = response.getStatusCode();
        if (statusCode == 404) {
            Hooks.scenario.log("Comment Deletion Verification Passed: comment correctly returns 404 after deletion");
        } else {
            String msg = "Comment Deletion Verification Failed: expected 404, actual=" + statusCode;
            Hooks.scenario.log(msg);
            Hooks.softAssert.fail(msg);
        }
    }

    @Then("Response body reflects the partially updated comment data")
    public void responseBodyReflectsPartiallyUpdatedComment() {
        String body = response.getBody().asString();
        if (body == null || body.trim().isEmpty()) {
            String msg = "Partial Update Validation Failed: response body is empty";
            Hooks.scenario.log(msg);
            Hooks.softAssert.fail(msg);
        } else {
            Hooks.scenario.log("Partial Update Validation Passed: body=" + body);
        }
    }

    @Then("Response body indicates authentication or authorization failure")
    public void responseBodyIndicatesAuthFailure() {
        int statusCode = response.getStatusCode();
        String body = response.getBody().asString();
        if (statusCode == 401 || statusCode == 403) {
            Hooks.scenario.log("Auth Failure Validation Passed: status=" + statusCode + ", body=" + body);
        } else {
            String msg = "Auth Failure Validation Failed: expected 401 or 403, actual=" + statusCode;
            Hooks.scenario.log(msg);
            Hooks.softAssert.fail(msg);
        }
    }
}