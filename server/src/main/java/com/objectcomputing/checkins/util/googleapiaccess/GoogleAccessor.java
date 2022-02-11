package com.objectcomputing.checkins.util.googleapiaccess;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.auth.oauth.OAuthParameters;
import com.google.api.client.auth.oauth2.*;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.*;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.DataStoreFactory;
import com.google.api.client.util.store.MemoryDataStoreFactory;
import com.google.api.services.admin.directory.Directory;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.services.drive.Drive;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.OAuth2Credentials;
import com.objectcomputing.checkins.security.GoogleServiceConfiguration;
import io.micronaut.context.annotation.Property;
import io.micronaut.context.annotation.Requires;
import io.micronaut.context.env.Environment;
import com.google.api.services.calendar.Calendar;
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import io.micronaut.security.authentication.Authentication;

import javax.inject.Singleton;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Collection;

import javax.inject.Singleton;

import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Requires(notEnv = Environment.TEST)
@Singleton
public class GoogleAccessor {

    private final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private final NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
    private final String applicationName;
    private final GoogleAuthenticator authenticator;
    private final Environment environment;
    private final GoogleServiceConfiguration gServiceConfig;

    public GoogleAccessor(@Property(name = "check-ins.application.name") String applicationName,
                          GoogleAuthenticator authenticator,
                          Environment environment,
                          GoogleServiceConfiguration gServiceConfig) throws GeneralSecurityException, IOException {
        this.applicationName = applicationName;
        this.authenticator = authenticator;
        this.environment = environment;
        this.gServiceConfig =gServiceConfig;
    }

    private TokenResponse getUserToken(GoogleCredentials credential) throws IOException {

        final TokenResponse response = new RefreshTokenRequest(httpTransport, JSON_FACTORY, new GenericUrl(gServiceConfig.getToken_uri()), credential.refreshAccessToken().getTokenValue())
                .setClientAuthentication(new ClientParametersAuthentication(gServiceConfig.getOauth_client_id(), gServiceConfig.getOauth_client_secret()))
                .execute();
        return response;

//        final TokenRequest request = new RefreshTokenRequest(
//                this.httpTransport, JSON_FACTORY,
//                new GenericUrl(credential.),
//                credential.getRefreshToken())
//                .setClientAuthentication(credential.getClientAuthentication())
//                .setRequestInitializer(credential);
//        final TokenResponse response = request.execute();
//        return (String) response.get("id_token");
    }

    public Credential getCalendarCredential () throws IOException {


        String apiScope = environment.getProperty("check-ins.application.google-api.scopes.scopeForCalendarApi", String.class).orElse("");
        List<String> scope = Collections.singletonList(apiScope);
        GoogleCredentials googleCredentials = authenticator.setupCredentials(scope);
        TokenResponse response = getUserToken(googleCredentials);
        HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(googleCredentials);

        return new Credential.Builder(BearerToken.authorizationHeaderAccessMethod())
                .setTransport(httpTransport)
                .setJsonFactory(JSON_FACTORY)
                .setRequestInitializer(requestInitializer)
                .setTokenServerUrl(new GenericUrl(gServiceConfig.getToken_uri()))
                //revisit set client authentication??
//                .setClientAuthentication(new GoogleAuthentication(gServiceConfig.getOauth_client_id(), gServiceConfig.getOauth_client_secret()))
                .build()
                .setFromTokenResponse(response)
                .setAccessToken(googleCredentials.getAccessToken().toString());




        }
    /**
     * Create and return the google drive access object
     *
     * @return a google drive access object
     * @throws IOException
     */
    public Drive accessGoogleDrive() throws IOException {

        String apiScope = environment.getProperty("check-ins.application.google-api.scopes.scopeForDriveApi", String.class).orElse("");
        List<String> scope = Collections.singletonList(apiScope);
        HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(authenticator.setupCredentials(scope));
        return new Drive
                .Builder(httpTransport, JSON_FACTORY, requestInitializer)
                .setApplicationName(applicationName)
                .build();
    }

        /**
     * Create and return the google drive access object
     *
     * @return a google drive access object
     * @throws IOException
     */
    public Calendar accessGoogleCalendar() throws IOException {
        String apiScope = environment.getProperty("check-ins.application.google-api.scopes.scopeForCalendarApi", String.class).orElse("");
        List<String> scope = Collections.singletonList(apiScope);
        HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(authenticator.setupCredentials(scope));
        return new Calendar.Builder(httpTransport, JSON_FACTORY, getCalendarCredential()).setApplicationName(applicationName).build();
    }



    /**
     * Create and return the google directory access object
     *
     * @return a google directory access object
     * @throws IOException
     */
    public Directory accessGoogleDirectory() throws IOException {

        String apiScope = environment.getProperty("check-ins.application.google-api.scopes.scopeForDirectoryApi", String.class).orElse("");
        String delegatedUser = environment.getProperty("check-ins.application.google-api.delegated-user", String.class).orElse("");
        List<String> scope = Arrays.asList(apiScope.split(","));

        HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(
                authenticator.setupServiceAccountCredentials(scope, delegatedUser));

        return new Directory
                .Builder(httpTransport, JSON_FACTORY, requestInitializer)
                .setApplicationName(applicationName)
                .build();
    }
}
