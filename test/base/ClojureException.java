package base;

public class ClojureException extends RuntimeException {
    public ClojureException(final String message) {
        super(message);
    }

    public ClojureException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
