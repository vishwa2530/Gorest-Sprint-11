package utils;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.databind.ObjectMapper;

public class DataUtility {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static int getValidPostId() {
        try {
            io.restassured.response.Response resp = io.restassured.RestAssured
                .given()
                .header("Authorization", "Bearer " + config.ConfigManager.getData("auth-key"))
                .accept("application/json")
                .get("https://gorest.co.in/public/v2/posts");
            return resp.jsonPath().getInt("[0].id");
        } catch (Exception e) {
            return 1;
        }
    }

    public static String getValidCommentId() {
        try {
            io.restassured.response.Response resp = io.restassured.RestAssured
                .given()
                .header("Authorization", "Bearer " + config.ConfigManager.getData("auth-key"))
                .accept("application/json")
                .get("https://gorest.co.in/public/v2/comments");
            return resp.jsonPath().getString("[0].id");
        } catch (Exception e) {
            e.printStackTrace();
            return "1";
        }
    }

    public static String buildCommentJson(String name, String email, String body, int postId) {
        try {
            Map<String, Object> map = new HashMap<>();
            map.put("name", name);
            map.put("email", email);
            map.put("body", body);
            map.put("post_id", postId);
            return mapper.writeValueAsString(map);
        } catch (Exception e) {
            return "{}";
        }
    }

    public static String buildCommentJson(Map<String, String> row) {
        try {
            Map<String, Object> map = new HashMap<>();
            for (Map.Entry<String, String> entry : row.entrySet()) {
                if (entry.getValue() != null && !entry.getValue().equals("null")) {
                    if (entry.getKey().equals("postId")) {
                        try {
                            map.put("post_id", Integer.parseInt(entry.getValue()));
                        } catch (NumberFormatException e) {
                            map.put("post_id", entry.getValue());
                        }
                    } else {
                        map.put(entry.getKey(), entry.getValue());
                    }
                }
            }
            return mapper.writeValueAsString(map);
        } catch (Exception e) {
            return "{}";
        }
    }
}
