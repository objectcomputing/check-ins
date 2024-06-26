package com.objectcomputing.checkins.configuration;

import io.micronaut.context.ApplicationContext;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CheckInsConfigurationTest {

    @Test
    void getWebAddress() {

        try (var ctx = ApplicationContext.run(Map.of(
                "datasources.enabled", false,
                "check-ins.web-address", "http://google.com"
        ))) {
            var config = ctx.getBean(CheckInsConfiguration.class);
            assertEquals("http://google.com", config.getWebAddress());
        }
    }
}