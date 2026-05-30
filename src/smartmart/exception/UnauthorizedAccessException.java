package smartmart.exception;

public class UnauthorizedAccessException extends SmartMartException {
    private final String username;
    private final String action;

    public UnauthorizedAccessException(String username, String action) {
        super("User '" + username + "' is not authorized to perform: " + action);
        this.username = username;
        this.action = action;
    }

    public String getUsername() {
        return username;
    }

    public String getAction() {
        return action;
    }
}
