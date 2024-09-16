package com.objectcomputing.checkins.configuration;

import io.micronaut.context.ApplicationContext;
import io.micronaut.context.exceptions.BeanInstantiationException;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.AssertionFailureBuilder.assertionFailure;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CheckInsConfigurationTest {

    @Test
    void checkConfigurationGetsParsed() {
        try (var ctx = ApplicationContext.run(Map.of(
                "datasources.default.enabled", false,
                "check-ins.web-address", "http://google.com",
                "check-ins.application.name", "Fancy app"
        ))) {
            var config = ctx.getBean(CheckInsConfiguration.class);
            assertEquals("http://google.com", config.getWebAddress());
            assertEquals("Fancy app", config.getApplication().getName());
        }
    }

    @Test
    void checkWebAddressGetsValidated() {
        try (var ctx = ApplicationContext.run(Map.of(
                "datasources.default.enabled", false,
                "check-ins.web-address", "",
                "check-ins.application.name", "Fancy app"
        ))) {
            var beanInstantiationException = assertThrows(BeanInstantiationException.class, () -> ctx.getBean(CheckInsConfiguration.class));
            assertContains("CheckInsConfiguration.webAddress - must not be blank", beanInstantiationException.getMessage());
        }
    }

    @Test
    void checkApplicationNameGetsValidated() {
        try (var ctx = ApplicationContext.run(Map.of(
                "datasources.default.enabled", false,
                "check-ins.web-address", "http://google.com",
                "check-ins.application.name", ""
        ))) {
            var beanInstantiationException = assertThrows(BeanInstantiationException.class, () -> ctx.getBean(CheckInsConfiguration.class));
            assertContains("CheckInsConfiguration$ApplicationConfig.name - must not be blank", beanInstantiationException.getMessage());
        }
    }

    private void assertContains(String expected, String actual) {
        if (!actual.contains(expected)) {
            assertionFailure()
                    .message("Expected:\n'%s'\nto contain: '%s'".formatted(actual, expected))
                    .expected(expected)
                    .actual(actual)
                    .buildAndThrow();
        }
    }
}