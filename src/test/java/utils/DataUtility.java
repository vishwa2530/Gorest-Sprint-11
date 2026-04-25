

package utils;

public class DataUtility {
    public static String buildTodoJson(String title, String status, int userId) {

        return "{ \"user_id\": " + userId +
               ", \"title\": \"" + title +
               "\", \"status\": \"" + status + "\" }";
    }
    public static String buildTodoWithInvalidUser(String title, String status, int userId) {

        return "{ \"user_id\": " + userId +
               ", \"title\": \"" + title +
               "\", \"status\": \"" + status + "\" }";
    }
    public static String emptyPayload() {
        return "{}";
    }
    public static String updateTodoJson(String title, String status, int userId) {

        return "{ \"user_id\": " + userId +
               ", \"title\": \"" + title +
               "\", \"status\": \"" + status + "\" }";
    }
    public static String patchTodoJson(String title) {

        return "{ \"title\": \"" + title + "\" }";
    }
}