package capstone.mfslbackend.error;

public class Error500 extends RuntimeException {
    public Error500(String message) {
        super(message);
    }
}
