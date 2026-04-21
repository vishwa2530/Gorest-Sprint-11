package base;

import config.ConfigManager;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

public class BaseTest {

    protected static RequestSpecification requestSpec;

    public void setup() {
        requestSpec = new RequestSpecBuilder()
                .setBaseUri(ConfigManager.getData("baseURL"))
                .setContentType(ContentType.JSON)
                .setAccept("application/json")
                .addHeader("auth-key", ConfigManager.getData("auth-key"))
                .build();
    }

}
