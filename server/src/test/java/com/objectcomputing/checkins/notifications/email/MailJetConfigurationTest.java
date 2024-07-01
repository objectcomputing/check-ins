package com.objectcomputing.checkins.notifications.email;

import io.micronaut.context.ApplicationContext;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MailJetConfigurationTest {

    @Test
    void happyConfig() {
        String email = "tim@here.com";
        String name = "Tim";

        try (var ctx = ApplicationContext.run(Map.of(
                "datasources.enabled", false,
                "mail-jet.from-address", email,
                "mail-jet.from-name", name
        ))) {
            var config = ctx.getBean(MailJetConfiguration.class);
            assertEquals(email, config.getFromAddress());
            assertEquals(name, config.getFromName());
        }

        try (var ctx = ApplicationContext.run(Map.of(
                "datasources.enabled", false,
                "mail-jet.fromAddress", email,
                "mail-jet.fromName", name
        ))) {
            var config = ctx.getBean(MailJetConfiguration.class);
            assertEquals(email, config.getFromAddress());
            assertEquals(name, config.getFromName());
        }

        try (var ctx = ApplicationContext.run(Map.of(
                "datasources.enabled", false,
                "mailJet.fromAddress", email,
                "mailJet.fromName", name
        ))) {
            var config = ctx.getBean(MailJetConfiguration.class);
            assertEquals(email, config.getFromAddress());
            assertEquals(name, config.getFromName());
        }

        try (var ctx = ApplicationContext.run(Map.of(
                "datasources.enabled", false,
                "mailJet.from_address", email,
                "mailJet.from_name", name
        ))) {
            var config = ctx.getBean(MailJetConfiguration.class);
            assertEquals(email, config.getFromAddress());
            assertEquals(name, config.getFromName());
        }
    }
}
