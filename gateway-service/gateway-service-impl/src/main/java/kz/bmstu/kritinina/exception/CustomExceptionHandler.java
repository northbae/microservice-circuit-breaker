package kz.bmstu.kritinina.exception;

import kz.bmstu.kritinina.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundException(NotFoundException notFoundException) {
        return getResponse(notFoundException, HttpStatus.NOT_FOUND, notFoundException.getMessage());
    }

    @ExceptionHandler(InvalidOperationException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundException(InvalidOperationException notFoundException) {
        return getResponse(notFoundException, HttpStatus.BAD_REQUEST, notFoundException.getMessage());
    }

    @ExceptionHandler(ServiceUnavailableException.class)
    public ResponseEntity<ErrorResponse> handleServiceUnavailableException(ServiceUnavailableException serviceUnavailableException) {
        return getResponse(serviceUnavailableException, HttpStatus.SERVICE_UNAVAILABLE, serviceUnavailableException.getMessage());
    }

    private ResponseEntity<ErrorResponse> getResponse(Exception exception, HttpStatus httpStatus, String message) {
        return ResponseEntity.status(httpStatus)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ErrorResponse().setMessage(message));
    }
}
