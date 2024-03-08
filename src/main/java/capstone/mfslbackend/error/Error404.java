package capstone.mfslbackend.error;

public class Error404 extends RuntimeException {
    public Error404(String message) {
        super(message);
    }
}
