package com.objectcomputing.checkins.services.validate;

import com.objectcomputing.checkins.services.exceptions.BadArgException;
import com.objectcomputing.checkins.services.exceptions.PermissionException;

import javax.inject.Singleton;
import javax.validation.constraints.NotNull;

@Singleton
public class Validation {

    public void validateArguments(@NotNull boolean isError, @NotNull String message, Object... args) {
        if (isError) {
            throw new BadArgException(String.format(message, args));
        }
    }

    public void validatePermissions(@NotNull boolean isError, @NotNull String message, Object... args) {
        if (isError) {
            throw new PermissionException(String.format(message, args));
        }
    }


}
