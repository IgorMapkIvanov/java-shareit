package ru.practicum.shareit.exceptions;

public class EmailIsPresentException extends RuntimeException {
    public EmailIsPresentException(String message) {
        super(message);
    }

    public String getMessage() {
        return super.getMessage();
    }
}
