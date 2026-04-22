package utils;

import java.util.Map;

public class DataUtility {

    public static String buildPostJson(String title, String body, int userId) {
        return String.format(
                "{\"user_id\":%d,\"title\":\"%s\",\"body\":\"%s\"}",
                userId, title, body);
    }

    public static String buildPostJson(Map<String, String> row) {
        return buildPostJson(
                row.get("title"),
                row.get("body"),
                Integer.parseInt(row.get("user_id")));
    }
}