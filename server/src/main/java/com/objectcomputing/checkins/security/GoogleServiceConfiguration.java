package com.objectcomputing.checkins.security;

import io.micronaut.context.annotation.ConfigurationProperties;
import io.micronaut.core.annotation.Introspected;
import jakarta.validation.constraints.NotNull;

@ConfigurationProperties("service-account-credentials")
@Introspected
public class GoogleServiceConfiguration {

    @NotNull
    public String directory_id;

    @NotNull
    public String type;

    @NotNull
    public String project_id;

    @NotNull
    public String private_key_id;

    @NotNull
    public String private_key;

    @NotNull
    public String client_email;

    @NotNull
    public String client_id;

    @NotNull
    public String auth_uri;

    @NotNull
    public String token_uri;

    @NotNull
    public String auth_provider_x509_cert_url;

    @NotNull
    public String client_x509_cert_url;

    @NotNull
    public String oauth_client_id;

    @NotNull
    public String oauth_client_secret;

    public String getDirectory_id() {
        return directory_id;
    }

    public void setDirectory_id(String directory_id) {
        this.directory_id = directory_id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getProject_id() {
        return project_id;
    }

    public void setProject_id(String project_id) {
        this.project_id = project_id;
    }

    public String getPrivate_key_id() {
        return private_key_id;
    }

    public void setPrivate_key_id(String private_key_id) {
        this.private_key_id = private_key_id;
    }

    public String getPrivate_key() {
        return private_key;
    }

    public void setPrivate_key(String private_key) {
        this.private_key = private_key;
    }

    public String getClient_email() {
        return client_email;
    }

    public void setClient_email(String client_email) {
        this.client_email = client_email;
    }

    public String getClient_id() {
        return client_id;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }

    public String getAuth_uri() {
        return auth_uri;
    }

    public void setAuth_uri(String auth_uri) {
        this.auth_uri = auth_uri;
    }

    public String getToken_uri() {
        return token_uri;
    }

    public void setToken_uri(String token_uri) {
        this.token_uri = token_uri;
    }

    public String getAuth_provider_x509_cert_url() {
        return auth_provider_x509_cert_url;
    }

    public void setAuth_provider_x509_cert_url(String auth_provider_x509_cert_url) {
        this.auth_provider_x509_cert_url = auth_provider_x509_cert_url;
    }

    public String getClient_x509_cert_url() {
        return client_x509_cert_url;
    }

    public void setClient_x509_cert_url(String client_x509_cert_url) {
        this.client_x509_cert_url = client_x509_cert_url;
    }

    public String getOauth_client_id() {
        return oauth_client_id;
    }

    public void setOauth_client_id(String oauth_client_id) {
        this.oauth_client_id = oauth_client_id;
    }

    public String getOauth_client_secret() {
        return oauth_client_secret;
    }

    public void setOauth_client_secret(String oauth_client_secret) {
        this.oauth_client_secret = oauth_client_secret;
    }

    public String toString() {
        return "{" +
                "\"directory_id\":\"" + directory_id +
                "\", \"type\":\"" + type +
                "\", \"project_id\":\"" + project_id +
                "\", \"private_key_id\":\"" + private_key_id +
                "\", \"private_key\":\"" + private_key +
                "\", \"client_email\":\"" + client_email +
                "\", \"client_id\":\"" + client_id +
                "\", \"auth_uri\":\"" + auth_uri +
                "\", \"token_uri\":\"" + token_uri +
                "\", \"auth_provider_x509_cert_url\":\"" + auth_provider_x509_cert_url +
                "\", \"client_x509_cert_url\":\"" + client_x509_cert_url +
                "\", \"oauth_client_id\":\"" + oauth_client_id +
                "\", \"oauth_client_secret\":\"" + oauth_client_secret +
                "\"}";
    }
}
