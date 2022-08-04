package com.objectcomputing.checkins.security.authorization;

import io.micronaut.aop.Around;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Target({TYPE, METHOD})
@Around
public @interface Authorization {
    String[] value() default "";
}
