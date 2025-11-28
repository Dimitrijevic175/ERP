package com.dimitrijevic175.user_service.exceptions;

public class RoleNotFoundException extends RuntimeException{
    public RoleNotFoundException(String roleName) {
        super("Role not found: " + roleName);
    }
}
