package com.objectcomputing.checkins.security;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.micronaut.context.annotation.ConfigurationProperties;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ConfigurationProperties("service-account-credentials")
public class GoogleServiceConfiguration {

    @NotNull
    @JsonProperty("directory_id")
    public String directoryId;

    @NotNull
    public String type;

    @NotNull
    @JsonProperty("project_id")
    public String projectId;

    @NotNull
    @JsonProperty("private_key_id")
    public String privateKeyId;

    @NotNull
    @JsonProperty("private_key")
    public String privateKey;

    @NotNull
    @JsonProperty("client_email")
    public String clientEmail;

    @NotNull
    @JsonProperty("client_id")
    public String clientId;

    @NotNull
    @JsonProperty("auth_uri")
    public String authUri;

    @NotNull
    @JsonProperty("token_uri")
    public String tokenUri;

    @NotNull
    @JsonProperty("auth_provider_x509_cert_url")
    public String authProviderX509CertUrl;

    @NotNull
    @JsonProperty("client_x509_cert_url")
    public String clientX509CertUrl;

    @NotNull
    @JsonProperty("oauth_client_id")
    public String oauthClientId;

    @NotNull
    @JsonProperty("oauth_client_secret")
    public String oauthClientSecret;
}
