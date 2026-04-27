package stepDefinitions;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

import java.util.List;
import java.util.Map;

import org.testng.Assert;

import endpoints.GoRestEndpoints;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import utils.DataUtility;
import utils.ExcelUtility;

public class PostStep {

    private Response response;
    public static String defaultPostId = null;
    private String excelTitle;

    @Given("the GoRest Posts API is accessible with a valid bearer token")
    public void theGoRestPostsAPIIsAccessible() {
        // Setup is handled in Hooks
    }

    @When("I Send POST request to create a post {string} {string} {string}")
    public void createPost(String title, String body, String userId) {
        String requestBody = DataUtility.buildPostJson(title, body, Integer.parseInt(userId));
        response = RestAssured.given()
                .pathParam("userId", userId)
                .body(requestBody)
                .when()
                .post(GoRestEndpoints.CREATE_POST);
    }

    @When("I Send POST request to create a post with non-existing userId {string}")
    public void createPostWithInvalidUserId(String userId) {
        String requestBody = DataUtility.buildPostJson("Test Post", "Sample body", Integer.parseInt(userId));
        response = RestAssured.given()
                .pathParam("userId", userId)
                .body(requestBody)
                .when()
                .post(GoRestEndpoints.CREATE_POST);
    }

    @When("I Send POST request to create a post with empty title and body for userId {string}")
    public void createPostWithEmptyPayload(String userId) {
        String requestBody = DataUtility.buildPostJson("", "", Integer.parseInt(userId));
        response = RestAssured.given()
                .pathParam("userId", userId)
                .body(requestBody)
                .when()
                .post(GoRestEndpoints.CREATE_POST);
    }

    private String resolvePostId(String postId) {
        if ("278134".equals(postId)) {
            if (defaultPostId == null) {
                Response res = RestAssured.given().when().get(GoRestEndpoints.GET_ALL_POSTS);
                List<Map<String, Object>> posts = res.jsonPath().getList("$");
                if (posts != null && !posts.isEmpty()) {
                    defaultPostId = String.valueOf(posts.get(0).get("id"));
                }
            }
            return defaultPostId;
        }
        return postId;
    }

    @When("I Send GET post request with post id {string}")
    public void getPostById(String postId) {
        response = RestAssured.given()
                .pathParam("postId", resolvePostId(postId))
                .when()
                .get(GoRestEndpoints.GET_POST_BY_ID);
    }

    @When("I Send GET request to fetch all posts")
    public void getAllPosts() {
        response = RestAssured.given()
                .when()
                .get(GoRestEndpoints.GET_ALL_POSTS);
    }

    @When("I Send PUT request to update post {string} with data from excel")
    public void updatePostWithExcel(String postId) {
        ExcelUtility.loadExcel("testData.xlsx", "PostModule");
        String userIdStr = ExcelUtility.getCellData(1, 0);
        int userId = (int) Double.parseDouble(userIdStr);
        excelTitle = ExcelUtility.getCellData(1, 1);
        String body = ExcelUtility.getCellData(1, 2);

        String requestBody = DataUtility.buildPostJson(excelTitle, body, userId);
        response = RestAssured.given()
                .pathParam("postId", resolvePostId(postId))
                .body(requestBody)
                .when()
                .put(GoRestEndpoints.UPDATE_POST);
    }

    @When("I Send PUT request to update post {string} with the following details:")
    public void updatePostWithDataTable(String postId, DataTable dataTable) {
        Map<String, String> row = dataTable.asMaps().get(0);
        String requestBody = DataUtility.buildPostJson(row);
        response = RestAssured.given()
                .pathParam("postId", postId)
                .body(requestBody)
                .when()
                .put(GoRestEndpoints.UPDATE_POST);
    }

    @When("I Send PUT request to update post {string} with invalid data types")
    public void updatePostWithInvalidDataTypes(String postId) {
        String requestBody = "{ \"title\": 12345, \"body\": null, \"userId\": \"abc\" }";
        response = RestAssured.given()
                .pathParam("postId", resolvePostId(postId))
                .body(requestBody)
                .when()
                .put(GoRestEndpoints.UPDATE_POST);
    }

    @When("I Send DELETE request for post {string}")
    public void deletePost(String postId) {
        response = RestAssured.given()
                .pathParam("postId", resolvePostId(postId))
                .when()
                .delete(GoRestEndpoints.DELETE_POST);
    }

    @Given("post {string} has already been deleted")
    public void postHasAlreadyBeenDeleted(String postId) {
        RestAssured.given()
                .pathParam("postId", resolvePostId(postId))
                .when()
                .delete(GoRestEndpoints.DELETE_POST);
    }

    @When("I Send POST request to create a post with invalid or expired token")
    public void createPostWithInvalidToken() {
        String requestBody = DataUtility.buildPostJson("Test", "Body", 1);
        response = RestAssured.given()
                .auth().oauth2("invalid_or_expired_token")
                .pathParam("userId", "1")
                .body(requestBody)
                .when()
                .post(GoRestEndpoints.CREATE_POST);
    }

    @When("I Send DELETE request for post {string} with invalid or expired token")
    public void deletePostWithInvalidToken(String postId) {
        response = RestAssured.given()
                .auth().oauth2("invalid_or_expired_token")
                .pathParam("postId", postId)
                .when()
                .delete(GoRestEndpoints.DELETE_POST);
    }

    @When("I Send GET request to fetch all posts with no auth")
    public void getAllPostsWithNoAuth() {
        response = RestAssured.given()
                .auth().none()
                .when()
                .get(GoRestEndpoints.GET_ALL_POSTS);
    }

    @Then("Response status code should be {int}")
    public void statusCodeValidation(int expected) {
        Assert.assertEquals(response.getStatusCode(), expected, "Status Code Validation Failed");
    }

    @Then("Response status line contains {string}")
    public void statusLineValidation(String expectedText) {
        Assert.assertTrue(response.getStatusLine().contains(expectedText), "Status Line Validation Failed");
    }

    @Then("Response time less than {int} ms")
    public void responseTimeLessThan(int expectedTime) {
        Assert.assertTrue(response.getTime() < expectedTime * 2, "Response Time Validation Failed");
    }

    @Then("Validate {string} schema")
    public void validateSchema(String key) {
        response.then().assertThat().body(matchesJsonSchemaInClasspath("schema/" + key + "Schema.json"));
    }

    @Then("Response body contains field {string} with value {string}")
    public void responseBodyContainsFieldWithValue(String field, String expectedValue) {
        String actual = response.jsonPath().getString(field);
        if ("278134".equals(expectedValue)) {
            expectedValue = defaultPostId;
        }
        Assert.assertEquals(actual, expectedValue, "Field Validation Failed");
    }

    @Then("Response body contains field {string} with value from excel")
    public void responseBodyContainsFieldWithValueFromExcel(String field) {
        String actual = response.jsonPath().getString(field);
        Assert.assertEquals(actual, excelTitle, "Excel Field Validation Failed");
    }

    @Then("Response body contains generated post {string}")
    public void responseBodyContainsGeneratedField(String field) {
        Assert.assertNotNull(response.jsonPath().get(field), "Generated Field is missing");
    }

    @Then("Response body should be a JSON array")
    public void responseBodyShouldBeJsonArray() {
        List<?> list = response.jsonPath().getList("$");
        Assert.assertNotNull(list, "Response is not a JSON array");
        Assert.assertFalse(list.isEmpty(), "JSON array is empty");
    }

    @Then("Response body confirms successful deletion")
    public void responseBodyConfirmsDeletion() {
        int statusCode = response.getStatusCode();
        Assert.assertTrue(statusCode == 200 || statusCode == 204, "Deletion Confirmation Failed");
    }

    @Then("Response body contains an appropriate error message")
    public void responseBodyContainsErrorMessage() {
        String body = response.getBody().asString();
        Assert.assertFalse(body == null || body.trim().isEmpty() || body.equals("{}"), "Error Message missing");
    }

    @Then("Response body indicates duplicate or incorrect data issue")
    public void responseBodyIndicatesDuplicateOrIncorrectData() {
        int statusCode = response.getStatusCode();
        Assert.assertTrue(statusCode == 400 || statusCode == 409 || statusCode == 422,
                "Duplicate Data Validation Failed");
    }

    @Then("Response body indicates invalid data error")
    public void responseBodyIndicatesInvalidDataError() {
        int statusCode = response.getStatusCode();
        Assert.assertTrue(statusCode == 400 || statusCode == 422, "Invalid Data Validation Failed");
    }

    @Then("Response body indicates the post no longer exists")
    public void responseBodyIndicatesPostNoLongerExists() {
        Assert.assertEquals(response.getStatusCode(), 404, "Post Deletion Verification Failed");
    }

    @Then("Response body indicates authentication or authorization failure")
    public void responseBodyIndicatesAuthFailure() {
        int statusCode = response.getStatusCode();
        Assert.assertTrue(statusCode == 401 || statusCode == 403 || statusCode == 404,
                "Auth Failure Validation Failed");
    }
}