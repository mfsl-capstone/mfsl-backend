package capstone.mfslbackend.configuration;

import capstone.mfslbackend.error.Error400;
import capstone.mfslbackend.error.Error404;
import capstone.mfslbackend.error.Error500;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(Error400.class)
    public ResponseEntity<String> handleCustomBadRequestException(Error400 ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Error404.class)
    public ResponseEntity<String> handleCustomNotFoundException(Error404 ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Error500.class)
    public ResponseEntity<String> handleCustomInternalServerErrorException(Error500 ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
