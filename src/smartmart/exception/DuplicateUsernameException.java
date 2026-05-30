package smartmart.exception;

public class DuplicateUsernameException extends SmartMartException {
    public DuplicateUsernameException(String username) {
        super("Username '" + username + "' already exists in the system.");
    }
}
