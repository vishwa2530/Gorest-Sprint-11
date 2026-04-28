// // ---------------------------------------------------------
// // Author : Rajmohan
// // Module : Posts
// // Description : Assertion Step Definitions for Posts Module in GoRest API
// // ---------------------------------------------------------

// package stepDefinitions;

// import hooks.Hooks;
// import io.cucumber.java.en.Then;
// import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
// import io.restassured.response.Response;

// public class Assertions {

//     public static Response response;
//     private static String excelTitle;

//     public static void setResponse(Response r) {
//         response = r;
//     }

//     public static void setExcelTitle(String title) {
//         excelTitle = title;
//     }

//     // ── Core Validations ──────────────────────────────────────────────────────

//     @Then("Response status code should be {int}")
//     public void statusCodeValidation(int expected) {
//         int actual = response.getStatusCode();
//         if (actual != expected) {
//             String msg = "Status Code Failed: expected=" + expected + ", actual=" + actual;
//             Hooks.scenario.log(msg);
//             Hooks.softAssert.fail(msg);
//         } else {
//             Hooks.scenario.log("Status Code Passed: " + actual);
//         }
//     }

//     @Then("Response status line contains {string}")
//     public void statusLineValidation(String expectedText) {
//         String actual = response.getStatusLine();
//         if (!actual.contains(expectedText)) {
//             String msg = "Status Line Failed: expected to contain '" + expectedText + "', actual='" + actual + "'";
//             Hooks.scenario.log(msg);
//             Hooks.softAssert.fail(msg);
//         } else {
//             Hooks.scenario.log("Status Line Passed: " + actual);
//         }
//     }

//     @Then("Response time less than {int} ms")
//     public void responseTimeLessThan(int expectedTime) {
//         long actualTime = response.getTime();
//         if (actualTime >= expectedTime) {
//             String msg = "Response Time Failed: expected < " + expectedTime + " ms, actual=" + actualTime + " ms";
//             Hooks.scenario.log(msg);
//             Hooks.softAssert.fail(msg);
//         } else {
//             Hooks.scenario.log("Response Time Passed: " + actualTime + " ms");
//         }
//     }

//     @Then("Validate {string} schema")
//     public void validateSchema(String key) {
//         try {
//             response.then()
//                     .assertThat()
//                     .body(matchesJsonSchemaInClasspath("schema/" + key + "Schema.json"));
//             Hooks.scenario.log("Schema Validation Passed: " + key);
//         } catch (AssertionError e) {
//             String msg = "Schema Validation Failed: " + e.getMessage();
//             Hooks.scenario.log(msg);
//             Hooks.softAssert.fail(msg);
//         }
//     }

//     // ── Post Field Validations ────────────────────────────────────────────────

//     @Then("Response body contains field {string} with value {string}")
//     public void responseBodyContainsFieldWithValue(String field, String expectedValue) {
//         String actual = response.jsonPath().getString(field);
//         if (actual == null || !actual.equals(expectedValue)) {
//             String msg = "Field Validation Failed: field='" + field + "', expected='" + expectedValue + "', actual='"
//                     + actual + "'";
//             Hooks.scenario.log(msg);
//             Hooks.softAssert.fail(msg);
//         } else {
//             Hooks.scenario.log("Field Validation Passed: " + field + "=" + actual);
//         }
//     }

//     @Then("Response body contains field {string} with value from excel")
//     public void responseBodyContainsFieldWithValueFromExcel(String field) {
//         String actual = response.jsonPath().getString(field);
//         if (actual == null || !actual.equals(excelTitle)) {
//             String msg = "Excel Field Validation Failed: field='" + field + "', expected from excel='" + excelTitle
//                     + "', actual='" + actual + "'";
//             Hooks.scenario.log(msg);
//             Hooks.softAssert.fail(msg);
//         } else {
//             Hooks.scenario.log("Excel Field Validation Passed: " + field + "=" + actual);
//         }
//     }

//     @Then("Response body contains generated post {string}")
//     public void responseBodyContainsGeneratedField(String field) {
//         Object value = response.jsonPath().get(field);
//         if (value == null) {
//             String msg = "Generated Field Validation Failed: '" + field + "' is missing in response body";
//             Hooks.scenario.log(msg);
//             Hooks.softAssert.fail(msg);
//         } else {
//             Hooks.scenario.log("Generated Field Validation Passed: " + field + "=" + value);
//         }
//     }

//     @Then("Response body should be a JSON array")
//     public void responseBodyShouldBeJsonArray() {
//         try {
//             java.util.List<?> list = response.jsonPath().getList("$");
//             if (list == null || list.isEmpty()) {
//                 String msg = "JSON Array Validation Failed: response body is empty or not a JSON array";
//                 Hooks.scenario.log(msg);
//                 Hooks.softAssert.fail(msg);
//             } else {
//                 Hooks.scenario.log("JSON Array Validation Passed: array size=" + list.size());
//             }
//         } catch (Exception e) {
//             String msg = "JSON Array Validation Failed: " + e.getMessage();
//             Hooks.scenario.log(msg);
//             Hooks.softAssert.fail(msg);
//         }
//     }

//     // ── Delete / Error / Auth Validations ────────────────────────────────────

//     @Then("Response body confirms successful deletion")
//     public void responseBodyConfirmsDeletion() {
//         int statusCode = response.getStatusCode();
//         if (statusCode == 200 || statusCode == 204) {
//             Hooks.scenario.log("Deletion Confirmation Passed: status=" + statusCode);
//         } else {
//             String msg = "Deletion Confirmation Failed: expected 200 or 204, actual=" + statusCode;
//             Hooks.scenario.log(msg);
//             Hooks.softAssert.fail(msg);
//         }
//     }

//     @Then("Response body contains an appropriate error message")
//     public void responseBodyContainsErrorMessage() {
//         String body = response.getBody().asString();
//         if (body == null || body.trim().isEmpty() || body.equals("{}")) {
//             String msg = "Error Message Validation Failed: response body is empty or missing error details";
//             Hooks.scenario.log(msg);
//             Hooks.softAssert.fail(msg);
//         } else {
//             Hooks.scenario.log("Error Message Validation Passed: body=" + body);
//         }
//     }

//     @Then("Response body indicates duplicate or incorrect data issue")
//     public void responseBodyIndicatesDuplicateOrIncorrectData() {
//         int statusCode = response.getStatusCode();
//         String body = response.getBody().asString();
//         if (statusCode == 400 || statusCode == 409) {
//             Hooks.scenario.log("Duplicate/Incorrect Data Validation Passed: status=" + statusCode + ", body=" + body);
//         } else {
//             String msg = "Duplicate/Incorrect Data Validation Failed: expected 400 or 409, actual=" + statusCode;
//             Hooks.scenario.log(msg);
//             Hooks.softAssert.fail(msg);
//         }
//     }

//     @Then("Response body indicates invalid data error")
//     public void responseBodyIndicatesInvalidDataError() {
//         int statusCode = response.getStatusCode();
//         String body = response.getBody().asString();
//         if (statusCode == 400 || statusCode == 422) {
//             Hooks.scenario.log("Invalid Data Validation Passed: status=" + statusCode + ", body=" + body);
//         } else {
//             String msg = "Invalid Data Validation Failed: expected 400 or 422, actual=" + statusCode;
//             Hooks.scenario.log(msg);
//             Hooks.softAssert.fail(msg);
//         }
//     }

//     @Then("Response body indicates the post no longer exists")
//     public void responseBodyIndicatesPostNoLongerExists() {
//         int statusCode = response.getStatusCode();
//         if (statusCode == 404) {
//             Hooks.scenario.log("Post Deletion Verification Passed: post correctly returns 404 after deletion");
//         } else {
//             String msg = "Post Deletion Verification Failed: expected 404, actual=" + statusCode;
//             Hooks.scenario.log(msg);
//             Hooks.softAssert.fail(msg);
//         }
//     }

//     @Then("Response body indicates authentication or authorization failure")
//     public void responseBodyIndicatesAuthFailure() {
//         int statusCode = response.getStatusCode();
//         String body = response.getBody().asString();
//         if (statusCode == 401 || statusCode == 403) {
//             Hooks.scenario.log("Auth Failure Validation Passed: status=" + statusCode + ", body=" + body);
//         } else {
//             String msg = "Auth Failure Validation Failed: expected 401 or 403, actual=" + statusCode;
//             Hooks.scenario.log(msg);
//             Hooks.softAssert.fail(msg);
//         }
//     }
//     // ── TODOS VALIDATIONS ───────────────────────────────────────────────

// // Validate generated todo ID
// @Then("Response body contains generated todo {string}")
// public void responseBodyContainsGeneratedTodo(String field) {
//     Object value = response.jsonPath().get(field);
//     if (value == null) {
//         String msg = "Todo ID Validation Failed: '" + field + "' is missing";
//         Hooks.scenario.log(msg);
//         Hooks.softAssert.fail(msg);
//     } else {
//         Hooks.scenario.log("Todo ID Validation Passed: " + field + "=" + value);
//     }
// }


// // Validate full todo details (id, title, status, user_id)
// @Then("Response body should contain the todo details including id, title, status, and userId")
// public void validateTodoDetails() {
//     Integer id = response.jsonPath().getInt("id");
//     String title = response.jsonPath().getString("title");
//     String status = response.jsonPath().getString("status");
//     Integer userId = response.jsonPath().getInt("user_id");

//     if (id == null || title == null || status == null || userId == null) {
//         String msg = "Todo Details Validation Failed: Missing fields";
//         Hooks.scenario.log(msg);
//         Hooks.softAssert.fail(msg);
//     } else {
//         Hooks.scenario.log("Todo Details Validation Passed");
//     }
// }


// // Validate update response
// @Then("Response body should reflect the fully updated todo data matching the request payload")
// public void validateUpdatedTodo() {
//     String title = response.jsonPath().getString("title");

//     if (title == null) {
//         String msg = "Update Validation Failed: title missing";
//         Hooks.scenario.log(msg);
//         Hooks.softAssert.fail(msg);
//     } else {
//         Hooks.scenario.log("Update Validation Passed: " + title);
//     }
// }


// // Validate delete response
// @Then("Response body should confirm the todo has been successfully deleted or be empty")
// public void validateDeleteTodo() {
//     int statusCode = response.getStatusCode();

//     if (statusCode == 200 || statusCode == 204) {
//         Hooks.scenario.log("Delete Validation Passed: " + statusCode);
//     } else {
//         String msg = "Delete Validation Failed: expected 200/204, actual=" + statusCode;
//         Hooks.scenario.log(msg);
//         Hooks.softAssert.fail(msg);
//     }
// }


// // Validate deleted todo not found
// @Then("Response body should indicate that no todo exists for the given ID")
// public void validateDeletedTodoNotFound() {
//     int statusCode = response.getStatusCode();

//     if (statusCode == 404) {
//         Hooks.scenario.log("Deleted Todo Validation Passed");
//     } else {
//         String msg = "Deleted Todo Validation Failed: expected 404, actual=" + statusCode;
//         Hooks.scenario.log(msg);
//         Hooks.softAssert.fail(msg);
//     }
// }


// // Validate JSON format
// @Then("Response Content-Type header should be application/json")
// public void validateContentType() {
//     String contentType = response.getHeader("Content-Type");

//     if (contentType == null || !contentType.contains("application/json")) {
//         String msg = "Content-Type Validation Failed: " + contentType;
//         Hooks.scenario.log(msg);
//         Hooks.softAssert.fail(msg);
//     } else {
//         Hooks.scenario.log("Content-Type Validation Passed: " + contentType);
//     }
// }
// }
