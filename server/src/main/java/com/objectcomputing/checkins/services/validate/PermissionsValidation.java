package com.objectcomputing.checkins.services.validate;

import com.objectcomputing.checkins.exceptions.PermissionException;
import jakarta.inject.Singleton;
import jakarta.validation.constraints.NotNull;

@Singleton
public class PermissionsValidation {

    public static final String NOT_AUTHORIZED_MSG = "You are not authorized to perform this operation";

    public void validatePermissions(@NotNull boolean isError) {
        validatePermissions(isError, NOT_AUTHORIZED_MSG);
    }

    public void validatePermissions(@NotNull boolean isError, @NotNull String message, Object... args) {
        if (isError) {
            throw new PermissionException(String.format(message, args));
        }
    }
}
