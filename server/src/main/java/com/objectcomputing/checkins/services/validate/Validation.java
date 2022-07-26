package com.objectcomputing.checkins.services.validate;

import jakarta.inject.Singleton;

import java.util.function.Supplier;

@Singleton
public class Validation {

    public static ValidationHandler validate(boolean isValid) {
        return new ValidationHandler(isValid);
    }

    public static class ValidationHandler {

        private final boolean isValid;

        public ValidationHandler(boolean isValid) {
            this.isValid = isValid;
        }

        public <X extends Throwable> void orElseThrow(Supplier<? extends X> exceptionSupplier) throws X {
            if (!isValid) {
                throw exceptionSupplier.get();
            }
        }
    }

}

