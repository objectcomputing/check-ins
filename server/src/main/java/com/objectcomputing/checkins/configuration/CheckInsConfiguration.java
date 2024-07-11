package com.objectcomputing.checkins.configuration;

import io.micronaut.context.annotation.ConfigurationProperties;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ConfigurationProperties("check-ins")
public class CheckInsConfiguration {

    @NotBlank
    private String webAddress;

    @NotNull
    private ApplicationConfig application;

    @Getter
    @Setter
    @ConfigurationProperties("application")
    public static class ApplicationConfig {

        @NotBlank
        private String name;
    }
}
