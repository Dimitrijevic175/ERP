package com.dimitrijevic175.user_service.exceptions;

public class SelfDeletionException extends RuntimeException {
    public SelfDeletionException() {
        super("Admin cannot delete themselves");
    }
}
