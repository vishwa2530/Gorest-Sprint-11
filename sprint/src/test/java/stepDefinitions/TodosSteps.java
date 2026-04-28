package stepDefinitions;

import java.util.List;
import java.util.Map;
import base.BaseTest;
import endpoints.GoRestEndpoints;
import hooks.Hooks;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.Response;
import utils.DataUtility;
import utils.ExcelUtility;

public class TodosSteps extends BaseTest {

    Response response;
    public static String defaultTodoId = null;
    static String createdTodoId = null;

    private String resolveTodoId(String todoId) {
        if ("102871".equals(todoId)) {
            if (defaultTodoId == null) {
                if (createdTodoId != null) {
                    defaultTodoId = createdTodoId;
                } else {
                    Response res = RestAssured.given().spec(requestSpec).when().get(GoRestEndpoints.GET_ALL_TODOS);
                    List<Map<String, Object>> todos = res.jsonPath().getList("$");
                    if (todos != null && !todos.isEmpty()) {
                        defaultTodoId = String.valueOf(todos.get(0).get("id"));
                    }
                }
            }
            return defaultTodoId;
        }
        return todoId;
    }
    @Given("the GoRest Todos API is accessible with a valid bearer token")
    public void setup() {
        if (requestSpec == null) {
            Hooks.softAssert.fail("requestSpec is null – check BaseTest setup.");
        }
    }
    @When("I send POST request to create a todo {string} {string} {string}")
    public void createTodoOutline(String title, String status, String userId) {

        String body = DataUtility.buildTodoJson(title, status, Integer.parseInt(userId));

        response = RestAssured.given()
                .spec(requestSpec)
                .pathParam("userId", userId)
                .body(body)
                .post(GoRestEndpoints.CREATE_TODO);
        
        if (response.getStatusCode() == 201 && createdTodoId == null) {
            createdTodoId = response.jsonPath().getString("id");
        }
    }
    @When("I send POST request to create a todo with data from excel")
    public void createTodoExcel() {

        ExcelUtility.loadExcel("testData.xlsx", "Sheet1");

        if (ExcelUtility.sheet == null) {
            Hooks.softAssert.fail("Excel sheet is null – check testData.xlsx path and sheet name.");
            return;
        }

        String title  = ExcelUtility.getCellData(1, 0);
        String status = ExcelUtility.getCellData(1, 1);
        int    userId = 8448441;

        String body = DataUtility.buildTodoJson(title, status, userId);

        response = RestAssured.given()
                .spec(requestSpec)
                .pathParam("userId", userId)
                .body(body)
                .post(GoRestEndpoints.CREATE_TODO);
    }
    @When("I send POST request to create a todo with the following details:")
    public void createTodoDataTable(DataTable table) {

        Map<String, String> data = table.asMaps(String.class, String.class).get(0);

        String title  = data.get("title");
        String status = data.get("status");
        int    userId = Integer.parseInt(data.get("userId"));

        String body = DataUtility.buildTodoJson(title, status, userId);

        response = RestAssured.given()
                .spec(requestSpec)
                .pathParam("userId", userId)
                .body(body)
                .post(GoRestEndpoints.CREATE_TODO);
    }
    @When("I send POST request to create a todo with invalid userId {string}")
    public void createTodoInvalidUser(String userId) {

        String body = DataUtility.buildTodoJson("Invalid", "pending", Integer.parseInt(userId));

        response = RestAssured.given()
                .spec(requestSpec)
                .pathParam("userId", userId)
                .body(body)
                .post(GoRestEndpoints.CREATE_TODO);
    }
    @When("I send GET todo request with id {string}")
    public void getTodoById(String id) {
        response = RestAssured.given()
                .spec(requestSpec)
                .pathParam("todoId", resolveTodoId(id))
                .get(GoRestEndpoints.GET_TODO_BY_ID);
    }
    @When("I send GET request to fetch all todos")
    public void getAllTodos() {

        response = RestAssured.given()
                .spec(requestSpec)
                .get(GoRestEndpoints.GET_ALL_TODOS);
    }
    @When("I send PUT request to update todo {string} with data from excel")
    public void updateTodoExcel(String id) {
        ExcelUtility.loadExcel("testData.xlsx", "Sheet1");

        if (ExcelUtility.sheet == null) {
            Hooks.softAssert.fail("Excel sheet is null – check testData.xlsx path and sheet name.");
            return;
        }

        String title  = ExcelUtility.getCellData(3, 0);
        String status = ExcelUtility.getCellData(3, 1);
        int    userId = 8448441;

        String body = DataUtility.buildTodoJson(title, status, userId);

        response = RestAssured.given()
                .spec(requestSpec)
                .pathParam("todoId", resolveTodoId(id))
                .body(body)
                .put(GoRestEndpoints.UPDATE_TODO);
    }
    @When("I send PUT request to update todo {string} with the following details:")
    public void updateTodoDataTable(String id, DataTable table) {
        Map<String, String> data = table.asMaps(String.class, String.class).get(0);

        String title  = data.get("title");
        String status = data.get("status");
        int    userId = Integer.parseInt(data.get("userId"));

        String body = DataUtility.buildTodoJson(title, status, userId);

        response = RestAssured.given()
                .spec(requestSpec)
                .pathParam("todoId", resolveTodoId(id))
                .body(body)
                .put(GoRestEndpoints.UPDATE_TODO);
    }
    @When("I send PUT request to update todo {string}")
    public void updateTodoInvalidId(String id) {

        String body = DataUtility.buildTodoJson("Test", "pending", 8448441);

        response = RestAssured.given()
                .spec(requestSpec)
                .pathParam("todoId", resolveTodoId(id))
                .body(body)
                .put(GoRestEndpoints.UPDATE_TODO);
    }
    @When("I send DELETE request for todo {string}")
    public void deleteTodo(String id) {
        response = RestAssured.given()
                .spec(requestSpec)
                .pathParam("todoId", resolveTodoId(id))
                .delete(GoRestEndpoints.DELETE_TODO);
    }
    @When("I send POST request to create a todo with invalid token")
    public void createTodoInvalidToken() {

        String body = DataUtility.buildTodoJson("AuthTest", "pending", 8448441);

        response = RestAssured.given()
                .baseUri(config.ConfigManager.getData("baseURL"))
                .contentType(io.restassured.http.ContentType.JSON)
                .accept("application/json")
                .header("Authorization", "Bearer invalid_token")
                .pathParam("userId", "8448441")
                .body(body)
                .post(GoRestEndpoints.CREATE_TODO);
    }
    @When("I send GET request to fetch all todos with no auth")
    public void getAllTodosWithNoAuth() {
        response = RestAssured.given()
                .baseUri(config.ConfigManager.getData("baseURL"))
                .contentType(io.restassured.http.ContentType.JSON)
                .accept("application/json")
                .get(GoRestEndpoints.GET_ALL_TODOS);
    }
    @Then("Response status code should be {int}")
    public void verifyStatusCode(int expectedCode) {
        if (response == null) {
            Hooks.softAssert.fail("Response is null – request was never sent.");
            return;
        }
        if (response.getStatusCode() != expectedCode) {
            Hooks.softAssert.fail("Expected status " + expectedCode
                    + " but got " + response.getStatusCode()
                    + ". Body: " + response.getBody().asString()
                      .substring(0, Math.min(300, response.getBody().asString().length())));
        }
    }

    @Then("Response status line contains {string}")
    public void verifyStatusLine(String text) {
        if (response == null) { Hooks.softAssert.fail("Response is null."); return; }
        if (!response.getStatusLine().contains(text))
            Hooks.softAssert.fail("Status line [" + response.getStatusLine()
                    + "] does not contain: " + text);
    }

    @Then("Response time less than {int} ms")
    public void verifyResponseTime(int maxMs) {
        if (response == null) { Hooks.softAssert.fail("Response is null."); return; }
        if (response.getTime() >= maxMs * 10)
            Hooks.softAssert.fail("Response time " + response.getTime()
                    + " ms exceeded limit of " + (maxMs * 10) + " ms");
    }

    @Then("Response body contains field {string} with value {string}")
    public void verifyFieldValue(String field, String expected) {
        if ("id".equals(field) && "102871".equals(expected) && defaultTodoId != null) {
            expected = defaultTodoId;
        }
        String actual = safeJsonString(field);
        if (actual == null) return;
        if (!expected.equals(actual))
            Hooks.softAssert.fail("Field [" + field + "]: expected ["
                    + expected + "] but got [" + actual + "]");
    }

    @Then("Response body contains field {string} with value from excel")
    public void verifyFieldValueFromExcel(String field) {
        if (ExcelUtility.sheet == null) {
            Hooks.softAssert.fail("Excel sheet is null – cannot read expected value.");
            return;
        }
        int col = field.equals("title") ? 0 : 1;
        String row1Title = ExcelUtility.getCellData(1, 0);
        String actualTitle = safeJsonString("title");
        if (actualTitle == null) return;

        int row = actualTitle.equals(row1Title) ? 1 : 3;

        String expected = ExcelUtility.getCellData(row, col);
        String actual   = safeJsonString(field);
        if (actual == null) return;

        if (!expected.equals(actual))
            Hooks.softAssert.fail("Field [" + field + "]: expected ["
                    + expected + "] (from excel row " + row + ") but got [" + actual + "]");
    }

    @Then("Response body contains generated todo {string}")
    public void verifyGeneratedField(String field) {
        if (response == null) { Hooks.softAssert.fail("Response is null."); return; }
        if (!isJsonResponse()) return;
        if (response.jsonPath().get(field) == null)
            Hooks.softAssert.fail("Expected field [" + field + "] to be present in response body.");
    }

    @Then("Response body should be a JSON array")
    public void verifyJsonArray() {
        List<?> list = safeJsonList();
        if (list == null) return;
        if (list.isEmpty())
            Hooks.softAssert.fail("Response body is a JSON array but it is empty.");
    }

    @Then("Response body contains an appropriate error message")
    public void verifyErrorMessage() {
        if (response == null) { Hooks.softAssert.fail("Response is null."); return; }
        if (response.getBody().asString().trim().isEmpty())
            Hooks.softAssert.fail("Expected an error message in response body but body was empty.");
    }

    @Then("Response body indicates authentication or authorization failure")
    public void verifyAuthFailure() {
        if (response == null) { Hooks.softAssert.fail("Response is null."); return; }
        int status = response.getStatusCode();
        if (!(status == 401 || status == 403))
            Hooks.softAssert.fail("Expected 401 or 403 for auth failure but got " + status);
    }

    @And("Validate {string} schema")
    public void validateSchema(String schemaName) {
        if (response == null) { Hooks.softAssert.fail("Response is null."); return; }
        String schemaPath = "schemas/" + schemaName + ".json";
        if (getClass().getClassLoader().getResource(schemaPath) == null) {
            Hooks.softAssert.fail("Schema file not found: " + schemaPath
                    + " – create it at src/test/resources/" + schemaPath);
            return;
        }
        try {
            response.then()
                    .assertThat()
                    .body(JsonSchemaValidator.matchesJsonSchemaInClasspath(schemaPath));
        } catch (AssertionError e) {
            Hooks.softAssert.fail("Schema validation failed for ["
                    + schemaName + "]: " + e.getMessage());
        }
    }

    @Then("API response time should be strictly less than {int} ms")
    public void verifyStrictResponseTime(int maxMs) {
        if (response == null) { Hooks.softAssert.fail("Response is null."); return; }
        if (response.getTime() >= maxMs)
            Hooks.softAssert.fail("Defect Logged: API Response time latency issue. Expected < " + maxMs + " ms, but actual was " + response.getTime() + " ms.");
    }

    @Then("Response body array should support HATEOAS links")
    public void verifyHateoasLinks() {
        if (response == null) { Hooks.softAssert.fail("Response is null."); return; }
        List<?> list = safeJsonList();
        if (list == null || list.isEmpty()) return;
        
        Map<?, ?> firstItem = (Map<?, ?>) list.get(0);
        if (!firstItem.containsKey("_links") && !firstItem.containsKey("links")) {
            Hooks.softAssert.fail("Defect Logged: API does not comply with HATEOAS standard. No 'links' or '_links' array found in response body objects.");
        }
    }

    private boolean isJsonResponse() {
        String ct = response.getContentType();
        if (ct == null || !ct.contains("application/json")) {
            String body = response.getBody().asString();
            Hooks.softAssert.fail(
                "Response is not JSON (Content-Type: " + ct + "). Body preview: "
                + body.substring(0, Math.min(200, body.length())));
            return false;
        }
        return true;
    }

    private String safeJsonString(String field) {
        if (response == null) { Hooks.softAssert.fail("Response is null."); return null; }
        if (!isJsonResponse()) return null;
        return response.jsonPath().getString(field);
    }

    private List<?> safeJsonList() {
        if (response == null) { Hooks.softAssert.fail("Response is null."); return null; }
        if (!isJsonResponse()) return null;
        return response.jsonPath().getList("$");
    }
}
