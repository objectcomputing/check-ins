package com.objectcomputing.checkins.services.validate;

import com.objectcomputing.checkins.services.exceptions.PermissionException;

import javax.inject.Singleton;
import javax.validation.constraints.NotNull;

@Singleton
public class PermissionsValidation {

    public PermissionsValidation() {
    }

    public void validatePermissions(@NotNull boolean isError, @NotNull String message, Object... args) {

        if (isError) {
            throw new PermissionException(String.format(message, args));
        }
    }

}
