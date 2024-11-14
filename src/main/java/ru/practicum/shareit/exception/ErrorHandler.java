package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleValidationException(ValidationException e) {
        log.error("Got 409 status Bad request {}", e.getMessage());
        return new ErrorResponse(e.getMessage(), "Bad request");
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundException(final NotFoundException e) {
        log.error("Got 404 status Not found {}", e.getMessage(), e);
        return new ErrorResponse(e.getMessage(), "Not found");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleRuntimeException(final RuntimeException e) {
        log.error("Got 500 status Internal server error {}", e.getMessage(), e);
        return new ErrorResponse(e.getMessage(), "Internal server error");
    }

}