package com.objectcomputing.checkins.services.validate;

import com.objectcomputing.checkins.services.exceptions.BadArgException;

import javax.inject.Singleton;
import javax.validation.constraints.NotNull;

@Singleton
public class ArgumentsValidation {

    public ArgumentsValidation() {
    }

    public void validateArguments(@NotNull boolean isError, @NotNull String message, Object... args) {

        if (isError) {
            throw new BadArgException(String.format(message, args));
        }
    }


}
