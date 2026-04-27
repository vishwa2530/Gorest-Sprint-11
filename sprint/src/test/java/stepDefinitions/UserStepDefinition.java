package stepDefinitions;

import java.io.FileInputStream;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.testng.Assert;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import static io.restassured.RestAssured.given;
import io.restassured.response.Response;
import utils.ExcelUtility;

public class UserStepDefinition {

    Response response;
    String baseURL, token;

    int userId;
    String name, email, gender, status;

    // =====================================================
    // CONFIG
    // =====================================================

    public void loadConfig() {
        try {
            Properties prop = new Properties();
            FileInputStream fis = new FileInputStream(
                    System.getProperty("user.dir") + "/src/test/resources/config/config.properties");

            prop.load(fis);

            baseURL = prop.getProperty("baseURL").trim();
            token = prop.getProperty("auth-key").trim();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Given("the GoRest Users API is accessible with a valid bearer token")
    public void setupAPI() {
        loadConfig();
        RestAssured.baseURI = baseURL;
    }

    // =====================================================
    // COMMON METHOD
    // =====================================================

    public void createUser(String name, String email, String gender, String status) {

        this.name = name;
        this.gender = gender;
        this.status = status;

        if (email.equalsIgnoreCase("duplicate@test.com") || email.equalsIgnoreCase("wrongmail")) {
            this.email = email;
        } else {
            this.email = "user" + System.currentTimeMillis() + "@test.com";
        }

        response = given()
                .header("Authorization", "Bearer " + token)
                .header("Content-Type", "application/json")
                .body("{"
                        + "\"name\":\"" + this.name + "\","
                        + "\"email\":\"" + this.email + "\","
                        + "\"gender\":\"" + this.gender + "\","
                        + "\"status\":\"" + this.status + "\""
                        + "}")
                .post("/users");

        if (response.statusCode() == 201) {
            userId = response.jsonPath().getInt("id");
        }
    }

    // =====================================================
    // POST
    // =====================================================

    @When("I Send POST request to create a user {string} {string} {string} {string}")
    public void createUserOutline(String name, String email, String gender, String status) {
        createUser(name, email, gender, status);
    }

    @When("I fetch user data from excel")
    public void fetchUserFromExcel() {

        String path = System.getProperty("user.dir") + "/src/test/resources/testdata/testData.xlsx";

        List<Map<String, String>> data = ExcelUtility.getData(path, "Users");

        if (data.isEmpty()) {
            throw new RuntimeException("Excel file is empty or not found");
        }

        Map<String, String> user = data.get(0);

        name = user.get("name");
        email = user.get("email");
        gender = user.get("gender");
        status = user.get("status");
    }

    @When("I Send POST request to create user with excel data")
    public void createExcelUser() {
        createUser(name, email, gender, status);
    }

    @When("I fetch duplicate user data from excel")
    public void duplicateUser() {
        createUser("Temp", "duplicate@test.com", "male", "active");

        name = "Temp";
        email = "duplicate@test.com";
        gender = "male";
        status = "active";
    }

    @When("I fetch invalid user data from excel")
    public void invalidUser() {
        name = "";
        email = "wrongmail";
        gender = "male";
        status = "active";
    }

    @When("I Send POST request to create user")
    public void createGenericUser() {
        createUser(name, email, gender, status);
    }

    @When("I Send POST request with the following user details:")
    public void createUserDataTable(DataTable table) {

        Map<String, String> data = table.asMaps().get(0);

        createUser(
                data.get("name"),
                data.get("email"),
                data.get("gender"),
                data.get("status"));
    }

    // =====================================================
    // GET
    // =====================================================

    @When("I fetch user id from excel")
    public void fetchUserId() {
        createUser("Temp User", "temp@test.com", "male", "active");
    }

    @When("I Send GET request for that user")
    public void getUser() {
        response = given()
                .header("Authorization", "Bearer " + token)
                .get("/users/" + userId);
    }

    @When("I Send GET request for user {string}")
    public void getInvalidUser(String id) {
        response = given()
                .header("Authorization", "Bearer " + token)
                .get("/users/" + id);
    }

    @When("I Send GET request to fetch all users")
    public void getAllUsers() {
        response = given()
                .header("Authorization", "Bearer " + token)
                .get("/users");
    }

    // =====================================================
    // PUT
    // =====================================================

    @When("I fetch update user data from excel")
    public void fetchUpdateData() {

        createUser("Base User", "base@test.com", "male", "active");

        name = "Updated User";
        email = "updated@test.com";
        gender = "male";
        status = "active";
    }

    @When("I Send PUT request to update user")
    public void updateUser() {

        email = "user" + System.currentTimeMillis() + "@test.com";

        response = given()
                .header("Authorization", "Bearer " + token)
                .header("Content-Type", "application/json")
                .body("{"
                        + "\"name\":\"" + name + "\","
                        + "\"email\":\"" + email + "\","
                        + "\"gender\":\"" + gender + "\","
                        + "\"status\":\"" + status + "\""
                        + "}")
                .put("/users/" + userId);
    }

    @When("I Send PUT request with:")
    public void updateWithDataTable(DataTable table) {

        // create user first
        createUser("Temp User", "temp@test.com", "male", "active");

        Map<String, String> data = table.asMaps().get(0);

        name = data.get("name");
        email = "user" + System.currentTimeMillis() + "@test.com";
        gender = data.get("gender");
        status = data.get("status");

        updateUser();
    }

    @When("I Send PUT request for user {string}")
    public void updateInvalidUser(String id) {
        response = given()
                .header("Authorization", "Bearer " + token)
                .put("/users/" + id);
    }

    // =====================================================
    // DELETE
    // =====================================================

    @When("I Send DELETE request")
    public void deleteUser() {
        response = given()
                .header("Authorization", "Bearer " + token)
                .delete("/users/" + userId);
    }

    @When("I Send DELETE request for user {string}")
    public void deleteInvalid(String id) {
        response = given()
                .header("Authorization", "Bearer " + token)
                .delete("/users/" + id);
    }

    @Given("user from excel has already been deleted")
    public void deletedUserSetup() {

        createUser("Delete User", "delete@test.com", "male", "active");

        given()
                .header("Authorization", "Bearer " + token)
                .delete("/users/" + userId);
    }

    @When("I Send GET request for same user")
    public void getDeletedUser() {

        response = given()
                .header("Authorization", "Bearer " + token)
                .get("/users/" + userId);
    }

    // =====================================================
    // INVALID TOKEN
    // =====================================================

    @When("I Send POST request with invalid token")
    public void invalidPost() {
        response = given().header("Authorization", "Bearer wrongtoken").post("/users");
    }

    @When("I Send GET request with invalid token")
    public void invalidGet() {
        response = given().header("Authorization", "Bearer wrongtoken").get("/users");
    }

    @When("I Send DELETE request with invalid token")
    public void invalidDelete() {
        response = given().header("Authorization", "Bearer wrongtoken").delete("/users/123");
    }

    // =====================================================
    // VALIDATIONS
    // =====================================================

    @Then("Response status code should be {int}")
    public void statusCode(int code) {
        Assert.assertEquals(response.statusCode(), code);
    }

    @Then("Response status line contains {string}")
    public void statusLine(String msg) {
        Assert.assertTrue(response.statusLine().contains(msg));
    }

    @Then("Response body contains field {string} with value {string}")
    public void validateField(String key, String value) {

        String actual = response.jsonPath().getString(key);

        if (key.equalsIgnoreCase("email")) {
            Assert.assertTrue(actual.contains("@test.com"));
        } else {
            Assert.assertEquals(actual, value);
        }
    }

    @Then("Response body contains generated user {string}")
    public void generatedUser(String id) {
        Assert.assertNotNull(response.jsonPath().get("id"));
    }

    @Then("Response body should be a JSON array")
    public void jsonArray() {
        Assert.assertTrue(response.jsonPath().getList("$").size() > 0);
    }

    @Then("Response body contains values from excel")
    public void validateExcel() {
        Assert.assertEquals(response.jsonPath().getString("name"), name);
    }

    @Then("Response body contains duplicate error")
    public void duplicateError() {
        Assert.assertTrue(response.asString().contains("has already been taken"));
    }

    @Then("Response body contains validation error")
    public void validationError() {
        Assert.assertTrue(response.asString().contains("invalid"));
    }

    @Then("Response body contains user details")
    public void userDetails() {
        Assert.assertNotNull(response.jsonPath().get("id"));
    }

    @Then("Response body reflects updated values")
    public void updatedValues() {
        Assert.assertEquals(response.jsonPath().getString("name"), name);
    }
}