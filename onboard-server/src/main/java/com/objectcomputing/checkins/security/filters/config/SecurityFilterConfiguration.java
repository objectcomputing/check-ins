package com.objectcomputing.checkins.security.filters.config;

import io.micronaut.core.util.Toggleable;

public interface SecurityFilterConfiguration extends Toggleable {
    String getPattern();
}
