package com.objectcomputing.checkins.services.permissions;

import io.micronaut.aop.Around;
import java.lang.annotation.*;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Retention(RUNTIME)
@Target({METHOD})
@Around
public @interface RequiredPermission {

    /**
     * The permission required, e.g. Can View Organization Members, Can Create/Delete Organization Members
     * @return permission
     */
    Permission value();
}
