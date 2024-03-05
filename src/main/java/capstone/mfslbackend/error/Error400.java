package capstone.mfslbackend.error;

public class Error400 extends RuntimeException{
    public Error400(String message) {
        super(message);
    }
}
