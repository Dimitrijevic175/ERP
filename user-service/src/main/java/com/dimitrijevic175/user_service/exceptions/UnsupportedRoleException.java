package com.dimitrijevic175.user_service.exceptions;

public class UnsupportedRoleException extends IllegalArgumentException {

    public UnsupportedRoleException(String roleName) {
        super("Role '" + roleName + "' is not supported by default in the system. Contact your IT support to add this role.");
    }
}
