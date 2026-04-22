// ---------------------------------------------------------
// Author : Rajmohan
// Module : Posts
// Description : Step Definitions for Posts Module in GoRest API
// ---------------------------------------------------------

package stepDefinitions;

import java.util.Map;
import base.BaseTest;
import endpoints.GoRestEndpoints;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import utils.DataUtility;
import utils.ExcelUtility;

public class PostStep extends BaseTest {

    Response response;

    // ── TC-001 : Scenario Outline - Create Post with Valid Payload ─────────────

    @When("I Send POST request to create a post {string} {string} {string}")
    public void createPost(String title, String body, String userId) {
        String requestBody = DataUtility.buildPostJson(title, body, Integer.parseInt(userId));

        response = RestAssured
                .given()
                .spec(requestSpec)
                .body(requestBody)
                .when()
                .post(GoRestEndpoints.CREATE_POST);
        Assertions.setResponse(response);
    }

    // ── TC-002 : Create Post with Non-Existing User ID ─────────────────────────

    @When("I Send POST request to create a post with non-existing userId {string}")
    public void createPostWithInvalidUserId(String userId) {
        String requestBody = DataUtility.buildPostJson("Test Post", "Sample body", Integer.parseInt(userId));

        response = RestAssured
                .given()
                .spec(requestSpec)
                .body(requestBody)
                .when()
                .post(GoRestEndpoints.CREATE_POST);
        Assertions.setResponse(response);
    }

    // ── TC-003 : Create Post with Empty Title and Body ─────────────────────────

    @When("I Send POST request to create a post with empty title and body for userId {string}")
    public void createPostWithEmptyPayload(String userId) {
        String requestBody = DataUtility.buildPostJson("", "", Integer.parseInt(userId));

        response = RestAssured
                .given()
                .spec(requestSpec)
                .body(requestBody)
                .when()
                .post(GoRestEndpoints.CREATE_POST);
        Assertions.setResponse(response);
    }

    // ── TC-004 and TC-005 : Get Post by ID ────────────────────────────────────

    @When("I Send GET post request with post id {string}")
    public void getPostById(String postId) {
        response = RestAssured
                .given()
                .spec(requestSpec)
                .pathParam("id", postId)
                .when()
                .get(GoRestEndpoints.GET_POST_BY_ID);
        Assertions.setResponse(response);
    }

    // ── TC-006 : Get All Posts ─────────────────────────────────────────────────

    @When("I Send GET request to fetch all posts")
    public void getAllPosts() {
        response = RestAssured
                .given()
                .spec(requestSpec)
                .when()
                .get(GoRestEndpoints.GET_ALL_POSTS);
        Assertions.setResponse(response);
    }

    // ── TC-007 : Full Update Post with Data from Excel ─────────────────────────

    @When("I Send PUT request to update post {string} with data from excel")
    public void updatePostWithExcel(String postId) {
        String title, body;
        int userId;

        ExcelUtility.loadExcel("testData.xlsx", "PostModule");
        userId = Integer.parseInt(ExcelUtility.getCellData(1, 0));
        body = ExcelUtility.getCellData(1, 1);
        title = ExcelUtility.getCellData(1, 2);

        String requestBody = DataUtility.buildPostJson(title, body, userId);

        response = RestAssured
                .given()
                .spec(requestSpec)
                .pathParam("id", postId)
                .body(requestBody)
                .when()
                .put(GoRestEndpoints.UPDATE_POST);
        Assertions.setResponse(response);
        Assertions.setExcelTitle(title);
    }

    // ── TC-008 : Full Update Post with Non-Existing ID using DataTable ─────────

    @When("I Send PUT request to update post {string} with the following details:")
    public void updatePostWithDataTable(String postId, DataTable dataTable) {
        Map<String, String> row = dataTable.asMaps().get(0);
        String requestBody = DataUtility.buildPostJson(row);

        response = RestAssured
                .given()
                .spec(requestSpec)
                .pathParam("id", postId)
                .body(requestBody)
                .when()
                .put(GoRestEndpoints.UPDATE_POST);
        Assertions.setResponse(response);
    }

    // ── TC-009 : Full Update Post with Invalid Data Types ─────────────────────

    @When("I Send PUT request to update post {string} with invalid data types")
    public void updatePostWithInvalidDataTypes(String postId) {
        String requestBody = "{ \"title\": 12345, \"body\": null, \"userId\": \"abc\" }";

        response = RestAssured
                .given()
                .spec(requestSpec)
                .pathParam("id", postId)
                .body(requestBody)
                .when()
                .put(GoRestEndpoints.UPDATE_POST);
        Assertions.setResponse(response);
    }

    // ── TC-010 and TC-011 : Delete Post by ID ─────────────────────────────────

    @When("I Send DELETE request for post {string}")
    public void deletePost(String postId) {
        response = RestAssured
                .given()
                .spec(requestSpec)
                .pathParam("id", postId)
                .when()
                .delete(GoRestEndpoints.DELETE_POST);
        Assertions.setResponse(response);
    }

    // ── TC-012 : Given - Post already deleted (pre-condition) ─────────────────

    @Given("post {string} has already been deleted")
    public void postHasAlreadyBeenDeleted(String postId) {
        RestAssured
                .given()
                .spec(requestSpec)
                .pathParam("id", postId)
                .when()
                .delete(GoRestEndpoints.DELETE_POST);
    }

    // ── TC-013 : POST with Invalid or Expired Token ───────────────────────────

    @When("I Send POST request to create a post with invalid or expired token")
    public void createPostWithInvalidToken() {
        String requestBody = DataUtility.buildPostJson("Test", "Body", 1);

        response = RestAssured
                .given()
                .spec(requestSpec)
                .header("Authorization", "Bearer invalid_or_expired_token")
                .body(requestBody)
                .when()
                .post(GoRestEndpoints.CREATE_POST);
        Assertions.setResponse(response);
    }

    // ── TC-014 : DELETE with Invalid or Expired Token ─────────────────────────

    @When("I Send DELETE request for post {string} with invalid or expired token")
    public void deletePostWithInvalidToken(String postId) {
        response = RestAssured
                .given()
                .spec(requestSpec)
                .header("Authorization", "Bearer invalid_or_expired_token")
                .pathParam("id", postId)
                .when()
                .delete(GoRestEndpoints.DELETE_POST);
        Assertions.setResponse(response);
    }

    // ── TC-015 : GET All Posts with Invalid or Expired Token ──────────────────

    @When("I Send GET request to fetch all posts with invalid or expired token")
    public void getAllPostsWithInvalidToken() {
        response = RestAssured
                .given()
                .spec(requestSpec)
                .header("Authorization", "Bearer invalid_or_expired_token")
                .when()
                .get(GoRestEndpoints.GET_ALL_POSTS);
        Assertions.setResponse(response);
    }
}
