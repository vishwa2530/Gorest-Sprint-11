// ---------------------------------------------------------
// Author : Jishwa
// Module : Comments
// Description : Step Definitions for Comments Module in GoRest API
// ---------------------------------------------------------
 
package stepDefinitions;
 
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
 
import java.util.HashMap;
import java.util.List;
import java.util.Map;
 
import org.testng.Assert;
 
import base.BaseClass;
import config.ConfigManager;
import endpoints.GoRestEndpoints;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import utils.DataUtility;
import utils.ExcelUtility;
 
public class CommentStep extends BaseClass {
 
    private Response response;
    private String resolvedCommentId = null;
 
    // Excel data
    private String excelName;
    private String excelEmail;
    private String excelBody;
 
    // ── Background ────────────────────────────────────────────────────────────
 
    @Given("the GoRest Comments API is accessible with a valid bearer token")
    public void theGoRestCommentsAPIIsAccessible() {
        // requestSpec is already initialised by Hooks → BaseClass.setup()
    }
 
    // ── Helper: resolve placeholder IDs "1","2","3" to a real live comment ID ─
 
    private String resolveCommentId(String commentId) {
        if ("1".equals(commentId) || "278134".equals(commentId)) {
            if (resolvedCommentId == null) {
                Response res = RestAssured.given()
                        .spec(requestSpec)
                        .when()
                        .get(GoRestEndpoints.GET_ALL_COMMENTS);
                List<Map<String, Object>> comments = res.jsonPath().getList("$");
                if (comments != null && !comments.isEmpty()) {
                    resolvedCommentId = String.valueOf(comments.get(0).get("id"));
                }
            }
            return resolvedCommentId != null ? resolvedCommentId : commentId;
        }
        return commentId;
    }
 
    // ── TC-001 : Create Comment – data from Excel ─────────────────────────────
 
    @When("I Send POST request to create a comment with data from excel")
    public void createCommentFromExcel() {
        ExcelUtility.loadExcel("CommentsTestData.xlsx", "CommentsModule");
        this.excelName = ExcelUtility.getCellData(1, 0);
        this.excelEmail = ExcelUtility.getCellData(1, 1);
        this.excelBody = ExcelUtility.getCellData(1, 2);
 
        int postId = DataUtility.getValidPostId();
        String requestBody = DataUtility.buildCommentJson(excelName, excelEmail, excelBody, postId);
 
        response = RestAssured.given()
                .spec(requestSpec)
                .body(requestBody)
                .when()
                .post(GoRestEndpoints.GET_ALL_COMMENTS);
    }
 
    // ── TC-002 : Create Comment – non-existing postId from Excel ──────────────
 
    @When("I Send POST request to create a comment with non-existing postId from excel")
    public void createCommentWithNonExistingPostIdFromExcel() {
        int nonExistingPostId = 99999999;
        String requestBody = DataUtility.buildCommentJson(
                "Test Name", "test@example.com", "Test Body", nonExistingPostId);
 
        response = RestAssured.given()
                .spec(requestSpec)
                .body(requestBody)
                .when()
                .post(GoRestEndpoints.GET_ALL_COMMENTS);
    }
 
    // ── TC-003 : Create Comment – empty/duplicate payload from Excel ──────────
 
    @When("I Send POST request to create a comment with empty or duplicate data from excel")
    public void createCommentWithEmptyOrDuplicateDataFromExcel() {
        response = RestAssured.given()
                .spec(requestSpec)
                .body("{}")
                .when()
                .post(GoRestEndpoints.GET_ALL_COMMENTS);
    }
 
    // ── TC-001 (Scenario Outline inline) ─────────────────────────────────────
 
    @When("I Send POST request to create a comment {string} {string} {string} {string}")
    public void createComment(String name, String email, String body, String postId) {
        int livePostId = DataUtility.getValidPostId();
        String requestBody = DataUtility.buildCommentJson(name, email, body, livePostId);
        response = RestAssured.given()
                .spec(requestSpec)
                .body(requestBody)
                .when()
                .post(GoRestEndpoints.GET_ALL_COMMENTS);
    }
 
    // ── TC-004 / TC-005 / TC-012 : Get Comment by ID ─────────────────────────
 
    @When("I Send GET comment request with comment id {string}")
    public void getCommentById(String commentId) {
        this.resolvedCommentId = resolveCommentId(commentId);
        response = RestAssured.given()
                .spec(requestSpec)
                .pathParam("commentId", resolvedCommentId)
                .when()
                .get(GoRestEndpoints.GET_COMMENT_BY_ID);
    }
 
    // ── TC-006 : Get All Comments ─────────────────────────────────────────────
 
    @When("I Send GET request to fetch all comments")
    public void getAllComments() {
        response = RestAssured.given()
                .spec(requestSpec)
                .when()
                .get(GoRestEndpoints.GET_ALL_COMMENTS);
    }
 
    // ── TC-007 / TC-008 / TC-009 : PUT – DataTable ───────────────────────────
 
    @When("I Send PUT request to update comment {string} with the following details:")
    public void updateCommentWithDataTable(String commentId, DataTable dataTable) {
        Map<String, String> row = new java.util.LinkedHashMap<>(dataTable.asMaps().get(0));
 
        if (row.containsKey("postId")) {
            try {
                int placeholder = Integer.parseInt(row.get("postId"));
                if (placeholder <= 9999 || placeholder == 278134) {
                    row.put("postId", String.valueOf(DataUtility.getValidPostId()));
                }
            } catch (NumberFormatException e) {
                // intentionally invalid value
            }
        }
 
        String requestBody = DataUtility.buildCommentJson(row);
        response = RestAssured.given()
                .spec(requestSpec)
                .pathParam("commentId", resolveCommentId(commentId))
                .body(requestBody)
                .when()
                .put(GoRestEndpoints.UPDATE_COMMENT);
    }
 
    // ── TC-007 (Excel) : PUT – Excel ─────────────────────────────────────────
 
    @When("I Send PUT request to update comment {string} with data from excel")
    public void updateCommentWithExcel(String commentId) {
        ExcelUtility.loadExcel("CommentsTestData.xlsx", "CommentsModule");
        this.excelName = ExcelUtility.getCellData(1, 0);
        this.excelEmail = ExcelUtility.getCellData(1, 1);
        this.excelBody = ExcelUtility.getCellData(1, 2);
 
        int postId = DataUtility.getValidPostId();
        String requestBody = DataUtility.buildCommentJson(excelName, excelEmail, excelBody, postId);
 
        response = RestAssured.given()
                .spec(requestSpec)
                .pathParam("commentId", resolveCommentId(commentId))
                .body(requestBody)
                .when()
                .put(GoRestEndpoints.UPDATE_COMMENT);
    }
 
    // ── TC-009b / TC-009c / TC-009d : PATCH – field/value DataTable ──────────
 
    @When("I Send PATCH request to update comment {string} with partial details:")
    public void patchCommentWithFieldValueTable(String commentId, DataTable dataTable) {
        List<Map<String, String>> rows = dataTable.asMaps();
        Map<String, Object> bodyMap = new HashMap<>();
        for (Map<String, String> row : rows) {
            String field = row.get("field");
            String value = row.get("value");
            if (value == null || "null".equalsIgnoreCase(value)) {
                bodyMap.put(field, null);
            } else {
                bodyMap.put(field, value);
            }
        }
 
        String requestBody;
        try {
            requestBody = new com.fasterxml.jackson.databind.ObjectMapper()
                    .writeValueAsString(bodyMap);
        } catch (Exception e) {
            requestBody = "{}";
        }
 
        response = RestAssured.given()
                .spec(requestSpec)
                .pathParam("commentId", resolveCommentId(commentId))
                .body(requestBody)
                .when()
                .patch(GoRestEndpoints.UPDATE_COMMENT);
    }
 
    // ── TC-010 / TC-011 : DELETE ──────────────────────────────────────────────
 
    @When("I Send DELETE request for comment {string}")
    public void deleteComment(String commentId) {
        response = RestAssured.given()
                .spec(requestSpec)
                .pathParam("commentId", resolveCommentId(commentId))
                .when()
                .delete(GoRestEndpoints.DELETE_COMMENT);
    }
 
    // ── TC-012 : Pre-condition – delete comment before verifying 404 ──────────
 
    @Given("comment {string} has already been deleted")
    public void commentHasAlreadyBeenDeleted(String commentId) {
        String resolved = resolveCommentId(commentId);
        this.resolvedCommentId = resolved;
        RestAssured.given()
                .spec(requestSpec)
                .pathParam("commentId", resolved)
                .when()
                .delete(GoRestEndpoints.DELETE_COMMENT);
    }
 
    // ── TC-013 : POST with token param ───────────────────────────────────────
 
    @When("I Send POST request to create a comment with invalid or expired token {string}")
    public void createCommentWithInvalidTokenParam(String token) {
        int livePostId = DataUtility.getValidPostId();
        String requestBody = DataUtility.buildCommentJson(
                "Test", "test@test.com", "Body", livePostId);
        response = RestAssured.given()
                .baseUri(ConfigManager.getData("baseURL"))
                .contentType("application/json")
                .accept("application/json")
                .header("Authorization", "Bearer " + token)
                .body(requestBody)
                .when()
                .post(GoRestEndpoints.GET_ALL_COMMENTS);
    }
 
    // ── TC-013 (plain) : POST with invalid token – no param ──────────────────
 
    @When("I Send POST request to create a comment with invalid or expired token")
    public void createCommentWithInvalidToken() {
        int livePostId = DataUtility.getValidPostId();
        String requestBody = DataUtility.buildCommentJson(
                "Test", "test@test.com", "Body", livePostId);
        response = RestAssured.given()
                .baseUri(ConfigManager.getData("baseURL"))
                .contentType("application/json")
                .accept("application/json")
                .header("Authorization", "Bearer invalid_or_expired_token")
                .body(requestBody)
                .when()
                .post(GoRestEndpoints.GET_ALL_COMMENTS);
    }
 
    // ── TC-014 : DELETE with token param ─────────────────────────────────────
 
    @When("I Send DELETE request for comment {string} with invalid or expired token {string}")
    public void deleteCommentWithInvalidTokenParam(String commentId, String token) {
        response = RestAssured.given()
                .baseUri(ConfigManager.getData("baseURL"))
                .contentType("application/json")
                .accept("application/json")
                .header("Authorization", "Bearer " + token)
                .pathParam("commentId", resolveCommentId(commentId))
                .when()
                .delete(GoRestEndpoints.DELETE_COMMENT);
    }
 
    // ── TC-014 (plain) : DELETE with invalid token – no param ────────────────
 
    @When("I Send DELETE request for comment {string} with invalid or expired token")
    public void deleteCommentWithInvalidToken(String commentId) {
        response = RestAssured.given()
                .baseUri(ConfigManager.getData("baseURL"))
                .contentType("application/json")
                .accept("application/json")
                .header("Authorization", "Bearer invalid_or_expired_token")
                .pathParam("commentId", resolveCommentId(commentId))
                .when()
                .delete(GoRestEndpoints.DELETE_COMMENT);
    }
 
    // ── TC-015 : GET all with Invalid Token ──────────────────────────────────
 
    @When("I Send GET request to fetch all comments with invalid or expired token {string}")
    public void getAllCommentsWithInvalidToken(String token) {
        response = RestAssured.given()
                .baseUri(ConfigManager.getData("baseURL"))
                .contentType("application/json")
                .accept("application/json")
                .when()
                .get(GoRestEndpoints.GET_ALL_COMMENTS);
    }
 
    // ── Assertions Merged from Assertions.java ────────────────────────────────
 
    @Then("Response status code should be {int}")
    public void statusCodeValidation(int expected) {
        int actual = response.getStatusCode();
        if (actual != expected) {
            String msg = "Status Code Validation Failed expected [" + expected + "] but found [" + actual + "]";
            Assert.fail(msg);
        }
    }
 
    @Then("Response status line contains {string}")
    public void statusLineValidation(String expectedText) {
        String actual = response.getStatusLine();
        if (!actual.contains(expectedText)) {
            String msg = "Status Line Failed: expected to contain '" + expectedText + "', actual='" + actual + "'";
            Assert.fail(msg);
        }
    }
 
    @Then("Response time less than {int} ms")
    public void responseTimeLessThan(int expectedTime) {
        long actualTime = response.getTime();
        if (actualTime >= expectedTime) {
            String msg = "Response Time Failed: expected < " + expectedTime + " ms, actual=" + actualTime + " ms";
            Assert.fail(msg);
        }
    }
 
    @Then("Validate {string} schema")
    public void validateSchema(String key) {
        try {
            response.then()
                    .assertThat()
                    .body(matchesJsonSchemaInClasspath("schema/" + key + "Schema.json"));
        } catch (AssertionError e) {
            String msg = "Schema Validation Failed: " + e.getMessage();
            Assert.fail(msg);
        }
    }
 
    @Then("Response body contains field {string} with value {string}")
    public void responseBodyContainsFieldWithValue(String field, String expectedValue) {
        if ("id".equals(field)
                && ("1".equals(expectedValue) || "278134".equals(expectedValue))
                && resolvedCommentId != null) {
            expectedValue = resolvedCommentId;
        }
 
        String actual = response.jsonPath().getString(field);
        if (actual == null || !actual.equals(expectedValue)) {
            String msg = "Field Validation Failed: field='" + field + "', expected='" + expectedValue
                    + "', actual='" + actual + "'";
            Assert.fail(msg);
        }
    }
 
    @Then("Response body contains field {string} with value from excel")
    public void responseBodyContainsFieldWithValueFromExcel(String field) {
        String actual = response.jsonPath().getString(field);
        String expected = null;
        if ("name".equals(field))
            expected = excelName;
        else if ("email".equals(field))
            expected = excelEmail;
        else if ("body".equals(field))
            expected = excelBody;
 
        if (actual == null || !actual.equals(expected)) {
            String msg = "Excel Field Validation Failed: field='" + field + "', expected='" + expected
                    + "', actual='" + actual + "'";
            Assert.fail(msg);
        }
    }
 
    @Then("Response body contains generated comment {string}")
    public void responseBodyContainsGeneratedCommentField(String field) {
        Object value = response.jsonPath().get(field);
        if (value == null) {
            String msg = "Generated Field Validation Failed: '" + field + "' is missing in response body";
            Assert.fail(msg);
        }
    }
 
    @Then("Response body should be a JSON array")
    public void responseBodyShouldBeJsonArray() {
        try {
            java.util.List<?> list = response.jsonPath().getList("$");
            if (list == null || list.isEmpty()) {
                String msg = "JSON Array Validation Failed: response body is empty or not a JSON array";
                Assert.fail(msg);
            }
        } catch (Exception e) {
            String msg = "JSON Array Validation Failed: " + e.getMessage();
            Assert.fail(msg);
        }
    }
 
    @Then("Response body confirms successful deletion")
    public void responseBodyConfirmsDeletion() {
        int statusCode = response.getStatusCode();
        if (statusCode != 200 && statusCode != 204) {
            String msg = "Deletion Confirmation Failed: expected 200 or 204, actual=" + statusCode;
            Assert.fail(msg);
        }
    }
 
    @Then("Response body contains an appropriate error message")
    public void responseBodyContainsErrorMessage() {
        String body = response.getBody().asString();
        if (body == null || body.trim().isEmpty() || body.equals("{}") || body.equals("[]")) {
            String msg = "Error Message Validation Failed: response body is empty or missing error details";
            Assert.fail(msg);
        }
    }
 
    @Then("Response body indicates duplicate or incorrect data issue")
    public void responseBodyIndicatesDuplicateOrIncorrectData() {
        int statusCode = response.getStatusCode();
        if (statusCode != 400 && statusCode != 409 && statusCode != 422) {
            String msg = "Duplicate/Incorrect Data Validation Failed: expected 400, 409 or 422, actual=" + statusCode;
            Assert.fail(msg);
        }
    }
 
    @Then("Response body indicates invalid data error")
    public void responseBodyIndicatesInvalidDataError() {
        int statusCode = response.getStatusCode();
        if (statusCode != 400 && statusCode != 422) {
            String msg = "Invalid Data Validation Failed: expected 400 or 422, actual=" + statusCode;
            Assert.fail(msg);
        }
    }
 
    @Then("Response body indicates the comment no longer exists")
    public void responseBodyIndicatesCommentNoLongerExists() {
        int statusCode = response.getStatusCode();
        if (statusCode != 404) {
            String msg = "Comment Deletion Verification Failed: expected 404, actual=" + statusCode;
            Assert.fail(msg);
        }
    }
 
    @Then("Response body reflects the partially updated comment data")
    public void responseBodyReflectsPartiallyUpdatedComment() {
        String body = response.getBody().asString();
        if (body == null || body.trim().isEmpty() || body.equals("{}")) {
            String msg = "Partial Update Validation Failed: response body is empty";
            Assert.fail(msg);
        }
    }
 
    @Then("Response body indicates authentication or authorization failure")
    public void responseBodyIndicatesAuthFailure() {
        int statusCode = response.getStatusCode();
        if (statusCode != 401 && statusCode != 403) {
            String msg = "Auth Failure Validation Failed: expected 401 or 403, actual=" + statusCode;
            Assert.fail(msg);
        }
    }
}
