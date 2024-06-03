package com.objectcomputing.checkins.security;

import com.objectcomputing.checkins.services.TestContainersSuite;
import io.micronaut.validation.validator.Validator;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class GoogleServiceConfigurationTest extends TestContainersSuite {

    @Inject
    private Validator validator;

    @Test
    void testInstantiation() {
        GoogleServiceConfiguration googleServiceConfiguration = new GoogleServiceConfiguration();
        assertNull(googleServiceConfiguration.getDirectory_id());
        assertNull(googleServiceConfiguration.getType());
        assertNull(googleServiceConfiguration.getProject_id());
        assertNull(googleServiceConfiguration.getPrivate_key_id());
        assertNull(googleServiceConfiguration.getPrivate_key());
        assertNull(googleServiceConfiguration.getClient_email());
        assertNull(googleServiceConfiguration.getClient_id());
        assertNull(googleServiceConfiguration.getAuth_uri());
        assertNull(googleServiceConfiguration.getToken_uri());
        assertNull(googleServiceConfiguration.getAuth_provider_x509_cert_url());
        assertNull(googleServiceConfiguration.getClient_x509_cert_url());
        assertNull(googleServiceConfiguration.getOauth_client_id());
        assertNull(googleServiceConfiguration.getOauth_client_secret());
    }

    @Test
    void testConstraintViolation() {
        GoogleServiceConfiguration googleServiceConfiguration = new GoogleServiceConfiguration();

        Set<ConstraintViolation<GoogleServiceConfiguration>> violations = validator.validate(googleServiceConfiguration);
        assertEquals(13, violations.size());
        for (ConstraintViolation<GoogleServiceConfiguration> violation : violations) {
            assertEquals("must not be null", violation.getMessage());
        }
    }

    @Test
    void testPopulate() {
        GoogleServiceConfiguration googleServiceConfiguration = new GoogleServiceConfiguration();

        googleServiceConfiguration.setDirectory_id("some.directory.id");
        assertEquals("some.directory.id", googleServiceConfiguration.getDirectory_id());

        googleServiceConfiguration.setType("some.type");
        assertEquals("some.type", googleServiceConfiguration.getType());

        googleServiceConfiguration.setProject_id("some.project.id");
        assertEquals("some.project.id", googleServiceConfiguration.getProject_id());

        googleServiceConfiguration.setPrivate_key_id("some.private.key.id");
        assertEquals("some.private.key.id", googleServiceConfiguration.getPrivate_key_id());

        googleServiceConfiguration.setPrivate_key("some.private.key");
        assertEquals("some.private.key", googleServiceConfiguration.getPrivate_key());

        googleServiceConfiguration.setClient_email("some.client.email");
        assertEquals("some.client.email", googleServiceConfiguration.getClient_email());

        googleServiceConfiguration.setClient_id("some.client.id");
        assertEquals("some.client.id", googleServiceConfiguration.getClient_id());

        googleServiceConfiguration.setAuth_uri("some.auth.uri");
        assertEquals("some.auth.uri", googleServiceConfiguration.getAuth_uri());

        googleServiceConfiguration.setToken_uri("some.token.uri");
        assertEquals("some.token.uri", googleServiceConfiguration.getToken_uri());

        googleServiceConfiguration.setAuth_provider_x509_cert_url("some.auth.provider");
        assertEquals("some.auth.provider", googleServiceConfiguration.getAuth_provider_x509_cert_url());

        googleServiceConfiguration.setClient_x509_cert_url("some.cert.url");
        assertEquals("some.cert.url", googleServiceConfiguration.getClient_x509_cert_url());

        googleServiceConfiguration.setOauth_client_id("some.client.id");
        assertEquals("some.client.id", googleServiceConfiguration.getClient_id());

        googleServiceConfiguration.setOauth_client_secret("some.client.secret");
        assertEquals("some.client.secret", googleServiceConfiguration.getOauth_client_secret());

        String toString = googleServiceConfiguration.toString();
        assertTrue(toString.contains("some.directory.id"));
        assertTrue(toString.contains("some.type"));
        assertTrue(toString.contains("some.project.id"));
        assertTrue(toString.contains("some.private.key.id"));
        assertTrue(toString.contains("some.private.key"));
        assertTrue(toString.contains("some.client.email"));
        assertTrue(toString.contains("some.client.id"));
        assertTrue(toString.contains("some.auth.uri"));
        assertTrue(toString.contains("some.token.uri"));
        assertTrue(toString.contains("some.auth.provider"));
        assertTrue(toString.contains("some.cert.url"));
        assertTrue(toString.contains("some.client.id"));
        assertTrue(toString.contains("some.client.secret"));

        Set<ConstraintViolation<GoogleServiceConfiguration>> violations = validator.validate(googleServiceConfiguration);
        assertTrue(violations.isEmpty());
    }
}
