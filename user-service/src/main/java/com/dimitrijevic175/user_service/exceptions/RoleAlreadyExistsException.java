package com.dimitrijevic175.user_service.exceptions;

public class RoleAlreadyExistsException extends RuntimeException {
    public RoleAlreadyExistsException(String roleName) {
        super("Role already exists: " + roleName);
    }
}
