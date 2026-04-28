package endpoints;

public interface GoRestEndpoints {

    /* ----------------------User Module---------------------- */
    public static final String CREATE_USER = "/public/v2/users";
    public static final String GET_ALL_USERS = "/public/v2/users";
    public static final String GET_USER_BY_ID = "/public/v2/users/{userId}";
    public static final String UPDATE_USER = "/public/v2/users/{userId}";
    public static final String DELETE_USER = "/public/v2/users/{userId}";

    /* ----------------------Posts Module--------------------- */
    public static final String CREATE_POST = "/public/v2/users/{userId}/posts";
    public static final String GET_ALL_POSTS = "/public/v2/posts";
    public static final String GET_POST_BY_ID = "/public/v2/posts/{postId}";
    public static final String UPDATE_POST = "/public/v2/posts/{postId}";
    public static final String DELETE_POST = "/public/v2/posts/{postId}";

    /* ----------------------Comments Module------------------ */
    public static final String CREATE_COMMENT = "/public/v2/posts/{postId}/comments";
    public static final String GET_ALL_COMMENTS = "/public/v2/comments";
    public static final String GET_COMMENT_BY_ID = "/public/v2/comments/{commentId}";
    public static final String UPDATE_COMMENT = "/public/v2/comments/{commentId}";
    public static final String DELETE_COMMENT = "/public/v2/comments/{commentId}";

    /* ----------------------Todos Module--------------------- */
    public static final String CREATE_TODO = "/public/v2/users/{userId}/todos";
    public static final String GET_ALL_TODOS = "/public/v2/todos";
    public static final String GET_TODO_BY_ID = "/public/v2/todos/{todoId}";
    public static final String UPDATE_TODO = "/public/v2/todos/{todoId}";
    public static final String DELETE_TODO = "/public/v2/todos/{todoId}";
}
