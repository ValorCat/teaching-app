package teaching.model;

public class HashingException extends RuntimeException {

    public HashingException(Throwable cause) {
        super("An error occurred during password hashing", cause);
    }
}
