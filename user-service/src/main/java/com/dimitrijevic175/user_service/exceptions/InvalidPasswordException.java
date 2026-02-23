package com.dimitrijevic175.user_service.exceptions;

public class InvalidPasswordException extends RuntimeException {
    public InvalidPasswordException() {
        super("Old password is incorrect");
    }
}
