package com.objectcomputing.checkins.security;

import com.objectcomputing.checkins.services.TestContainersSuite;
import io.micronaut.context.ApplicationContext;
import io.micronaut.core.type.Argument;
import io.micronaut.json.JsonMapper;
import io.micronaut.validation.validator.Validator;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class GoogleServiceConfigurationTest extends TestContainersSuite {

    @Inject
    JsonMapper jsonMapper;

    @Inject
    private Validator validator;

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
    void testConfigurationLoadedCorrectlyFromConfiguration() throws IOException {
        assertConfig((values, cfg) -> {
            notNullAndEquals(values.get("directory"), cfg.getDirectoryId());
            notNullAndEquals(values.get("type"), cfg.getType());
            notNullAndEquals(values.get("project"), cfg.getProjectId());
            notNullAndEquals(values.get("keyId"), cfg.getPrivateKeyId());
            notNullAndEquals(values.get("key"), cfg.getPrivateKey());
            notNullAndEquals(values.get("clientEmail"), cfg.getClientEmail());
            notNullAndEquals(values.get("clientId"), cfg.getClientId());
            notNullAndEquals(values.get("authUri"), cfg.getAuthUri());
            notNullAndEquals(values.get("tokenUri"), cfg.getTokenUri());
            notNullAndEquals(values.get("authProvider"), cfg.getAuthProviderX509CertUrl());
            notNullAndEquals(values.get("certUrl"), cfg.getClientX509CertUrl());
            notNullAndEquals(values.get("oauthClientId"), cfg.getOauthClientId());
            notNullAndEquals(values.get("oauthClient"), cfg.getOauthClientSecret());
        });
    }

    @Test
    void testConfigurationSerializedCorrectlyFromConfiguration() throws IOException {
        assertConfig((values, cfg) -> {
            Map<String, String> map = asStringyMap(cfg);
            notNullAndEquals(values.get("directory"), map.get("directory_id"));
            notNullAndEquals(values.get("type"), map.get("type"));
            notNullAndEquals(values.get("project"), map.get("project_id"));
            notNullAndEquals(values.get("keyId"), map.get("private_key_id"));
            notNullAndEquals(values.get("key"), map.get("private_key"));
            notNullAndEquals(values.get("clientEmail"), map.get("client_email"));
            notNullAndEquals(values.get("clientId"), map.get("client_id"));
            notNullAndEquals(values.get("authUri"), map.get("auth_uri"));
            notNullAndEquals(values.get("tokenUri"), map.get("token_uri"));
            notNullAndEquals(values.get("authProvider"), map.get("auth_provider_x509_cert_url"));
            notNullAndEquals(values.get("certUrl"), map.get("client_x509_cert_url"));
            notNullAndEquals(values.get("oauthClientId"), map.get("oauth_client_id"));
            notNullAndEquals(values.get("oauthClient"), map.get("oauth_client_secret"));
        });
    }

    private Map<String, String> asStringyMap(GoogleServiceConfiguration cfg) {
        try {
            return jsonMapper.readValue(jsonMapper.writeValueAsString(cfg), Argument.mapOf(String.class, String.class));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void notNullAndEquals(String expected, String actual) {
        assertNotNull(expected);
        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    private void assertConfig(BiConsumer<Map<String, String>, GoogleServiceConfiguration> test) {
        var values = Map.ofEntries(
                Map.entry("directory", "some.directory.id"),
                Map.entry("type", "some.type"),
                Map.entry("project", "some.project.id"),
                Map.entry("keyId", "some.private.key.id"),
                Map.entry("key", "some.private.key"),
                Map.entry("clientEmail", "some.client.email"),
                Map.entry("clientId", "some.client.id"),
                Map.entry("authUri", "some.auth.uri"),
                Map.entry("tokenUri", "some.token.uri"),
                Map.entry("authProvider", "some.auth.provider"),
                Map.entry("certUrl", "some.cert.url"),
                Map.entry("oauthClientId", "some.oauth.client.id"),
                Map.entry("oauthClient", "some.oauth.client")
        );
        try (var ctx = ApplicationContext.run(Map.ofEntries(
                Map.entry("datasources.enabled", false),
                Map.entry("service-account-credentials.directory-id", values.get("directory")),
                Map.entry("service-account-credentials.type", values.get("type")),
                Map.entry("service-account-credentials.project-id", values.get("project")),
                Map.entry("service-account-credentials.private-key_id", values.get("keyId")),
                Map.entry("service-account-credentials.private-key", values.get("key")),
                Map.entry("service-account-credentials.client-email", values.get("clientEmail")),
                Map.entry("service-account-credentials.client-id", values.get("clientId")),
                Map.entry("service-account-credentials.auth-uri", values.get("authUri")),
                Map.entry("service-account-credentials.token-uri", values.get("tokenUri")),
                Map.entry("service-account-credentials.auth-provider-x509-cert-url", values.get("authProvider")),
                Map.entry("service-account-credentials.client-x509-cert-url", values.get("certUrl")),
                Map.entry("service-account-credentials.oauth-client-id", values.get("oauthClientId")),
                Map.entry("service-account-credentials.oauth-client-secret", values.get("oauthClient"))
        ))) {
            var cfg = ctx.getBean(GoogleServiceConfiguration.class);
            test.accept(values, cfg);
        }
    }
}
