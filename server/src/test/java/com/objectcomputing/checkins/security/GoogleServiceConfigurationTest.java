package com.objectcomputing.checkins.security;

import com.objectcomputing.checkins.services.TestContainersSuite;
import io.micronaut.context.ApplicationContext;
import io.micronaut.json.JsonMapper;
import io.micronaut.validation.validator.Validator;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class GoogleServiceConfigurationTest extends TestContainersSuite {

    private static final String EXAMPLE_GOOGLE_SERVICE_CONFIGURATION = """
            {
              "type": "service_account",
              "project_id": "test",
              "private_key_id": "private-key-id",
              "private_key": "-----BEGIN PRIVATE KEY-----\\nsome-made-up-value\\n-----END PRIVATE KEY-----\\n",
              "client_email": "woo@demo.iam.gserviceaccount.com",
              "client_id": "007",
              "auth_uri": "https://accounts.google.com/o/oauth2/auth",
              "token_uri": "https://oauth2.googleapis.com/token",
              "auth_provider_x509_cert_url": "https://www.googleapis.com/oauth2/v1/certs",
              "client_x509_cert_url": "https://www.googleapis.com/robot/v1/metadata/x509/woo%40demo.iam.gserviceaccount.com"
            }""";
    private static final String ENCODED_EXAMPLE_GOOGLE_SERVICE_CONFIGURATION = Base64.getEncoder().encodeToString(EXAMPLE_GOOGLE_SERVICE_CONFIGURATION.getBytes());

    @Inject
    JsonMapper jsonMapper;

    @Inject
    private Validator validator;

    @Test
    void testConstraintViolation() {
        GoogleServiceConfiguration googleServiceConfiguration = new GoogleServiceConfiguration();

        Set<ConstraintViolation<GoogleServiceConfiguration>> violations = validator.validate(googleServiceConfiguration);
        assertEquals(1, violations.size());
        assertEquals(
                List.of("encodedValue:must be a valid base64 encoded Google Service Configuration"),
                violations.stream().map(v -> v.getPropertyPath() + ":" + v.getMessage()).toList()
        );
    }

    @Test
    void testConstraintViolationPasses() {
        GoogleServiceConfiguration googleServiceConfiguration = new GoogleServiceConfiguration();
        googleServiceConfiguration.setEncodedValue(ENCODED_EXAMPLE_GOOGLE_SERVICE_CONFIGURATION);

        Set<ConstraintViolation<GoogleServiceConfiguration>> violations = validator.validate(googleServiceConfiguration);
        assertEquals(0, violations.size());
    }

    @Test
    void testConfigurationLoadedCorrectlyFromConfiguration() throws IOException {
        var values = Map.ofEntries(
                Map.entry("encoded-gcp-credentials", ENCODED_EXAMPLE_GOOGLE_SERVICE_CONFIGURATION)
        );
        try (var ctx = ApplicationContext.run(Map.ofEntries(
                Map.entry("datasources.enabled", false),
                Map.entry("service-account-credentials.encoded-value", values.get("encoded-gcp-credentials"))
        ))) {
            var cfg = ctx.getBean(GoogleServiceConfiguration.class);

            assertNotNull(cfg.getEncodedValue());
            assertEquals(values.get("encoded-gcp-credentials"), cfg.getEncodedValue());
        }
    }
}
