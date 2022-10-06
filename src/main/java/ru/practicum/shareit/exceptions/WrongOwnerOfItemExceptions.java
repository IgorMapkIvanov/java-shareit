package ru.practicum.shareit.exceptions;

public class WrongOwnerOfItemExceptions extends RuntimeException {
    public WrongOwnerOfItemExceptions(String message) {
        super(message);
    }

    public String getMessage() {
        return super.getMessage();
    }
}
