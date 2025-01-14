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

        @NotNull
        private FeedbackConfig feedback;

        @NotNull
        private GoogleApiConfig googleApi;

        @NotNull
        private NotificationsConfig notifications;

        @NotNull
        private PulseResponseConfig pulseResponse;

        @Getter
        @Setter
        @ConfigurationProperties("feedback")
        public static class FeedbackConfig {

            @NotNull
            private Integer maxSuggestions;

            @NotBlank
            private String requestSubject;
        }

        @Getter
        @Setter
        @ConfigurationProperties("google-api")
        public static class GoogleApiConfig {

            @NotBlank
            private String delegatedUser;

            @NotNull
            private ScopeConfig scopes;

            @Getter
            @Setter
            @ConfigurationProperties("scopes")
            public static class ScopeConfig {

                @NotBlank
                private String scopeForDriveApi;

                @NotBlank
                private String scopeForDirectoryApi;
            }
        }

        @Getter
        @Setter
        @ConfigurationProperties("notifications")
        public static class NotificationsConfig {

            @NotNull
            private SlackConfig slack;

            @Getter
            @Setter
            @ConfigurationProperties("slack")
            public static class SlackConfig {
                @NotBlank
                private String webhookUrl;

                @NotBlank
                private String botToken;
            }
        }

        @Getter
        @Setter
        @ConfigurationProperties("pulse-response")
        public static class PulseResponseConfig {

            @NotNull
            private SlackConfig slack;

            @Getter
            @Setter
            @ConfigurationProperties("slack")
            public static class SlackConfig {
                @NotBlank
                private String signingSecret;

                @NotBlank
                private String botToken;
            }
        }
    }
}
