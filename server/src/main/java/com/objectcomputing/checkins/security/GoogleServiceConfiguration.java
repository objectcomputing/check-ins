package com.objectcomputing.checkins.security;

import io.micronaut.context.annotation.ConfigurationProperties;
import io.micronaut.context.annotation.Factory;
import io.micronaut.core.type.Argument;
import io.micronaut.json.JsonMapper;
import io.micronaut.validation.validator.constraints.ConstraintValidator;
import jakarta.inject.Singleton;
import jakarta.validation.Constraint;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Base64;
import java.util.Map;
import java.util.stream.Stream;

@Getter
@Setter
@ConfigurationProperties("service-account-credentials")
public class GoogleServiceConfiguration {

    private static final Logger LOG = LoggerFactory.getLogger(GoogleServiceConfiguration.class);

    @NotNull
    private String directoryId;

    @ValidEncodedGoogleServiceConfiguration
    private String encodedGcpCredentials;

    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @Constraint(validatedBy = {})
    @interface ValidEncodedGoogleServiceConfiguration {
    }

    @Factory
    static class CustomValidationFactory {

        private final JsonMapper jsonMapper;
        private static final Base64.Decoder DECODER = Base64.getDecoder();

        CustomValidationFactory(JsonMapper jsonMapper) {
            this.jsonMapper = jsonMapper;
        }

        @Singleton
        ConstraintValidator<ValidEncodedGoogleServiceConfiguration, String> e164Validator() {
            return (value, annotation, context) -> {
                if (value == null || !isValid(value)) {
                    context.buildConstraintViolationWithTemplate("must be a valid encoded Google Service Configuration")
                            .addConstraintViolation();
                    return false;
                }
                return true;
            };
        }

        // Check the decoded json string for the required fields
        private boolean isValid(String value) {
            try {
                Map<String, Object> map = jsonMapper.readValue(DECODER.decode(value), Argument.mapOf(String.class, Object.class));
                return Stream.of(
                        "type",
                        "project_id",
                        "private_key_id",
                        "private_key",
                        "client_email",
                        "client_id",
                        "auth_uri",
                        "token_uri",
                        "auth_provider_x509_cert_url",
                        "client_x509_cert_url"
                ).allMatch(map::containsKey);
            } catch (Exception e) {
                LOG.error("An error occurred while decoding the Google Service Configuration.", e);
            }
            return false;
        }
    }
}
