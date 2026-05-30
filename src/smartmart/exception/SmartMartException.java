package smartmart.exception;

public class SmartMartException extends Exception {
    public SmartMartException(String message) {
        super(message);
    }

    public SmartMartException(String message, Throwable cause) {
        super(message, cause);
    }
}
