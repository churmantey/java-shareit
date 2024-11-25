package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(DuplicateException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleValidationException(DuplicateException e) {
        log.error("Got 409 status conflicting data {}", e.getMessage());
        return new ErrorResponse(e.getMessage(), "Conflictind request");
    }

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(ValidationException e) {
        log.error("Got 400 status invalid data {}", e.getMessage());
        return new ErrorResponse(e.getMessage(), "Bad request");
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundException(NotFoundException e) {
        log.error("Got 404 status Not found {}", e.getMessage(), e);
        return new ErrorResponse(e.getMessage(), "Not found");
    }

    @ExceptionHandler(AccessException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleNotFoundException(AccessException e) {
        log.error("Got 403 status forbidden {}", e.getMessage(), e);
        return new ErrorResponse(e.getMessage(), "Forbidden");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleRuntimeException(RuntimeException e) {
        log.error("Got 500 status Internal server error {}", e.getMessage(), e);
        return new ErrorResponse(e.getMessage(), "Internal server error");
    }

}