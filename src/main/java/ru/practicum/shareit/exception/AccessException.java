package ru.practicum.shareit.exception;

public class AccessException extends RuntimeException {
    public AccessException(String message) {
        super(message);
    }

    public AccessException(String message, Throwable e) {
        super(message, e);
    }
}
