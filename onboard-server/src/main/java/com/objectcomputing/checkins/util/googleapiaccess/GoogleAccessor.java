package com.objectcomputing.checkins.util.googleapiaccess;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.admin.directory.Directory;
import com.google.api.services.drive.Drive;
import com.google.auth.http.HttpCredentialsAdapter;
import com.objectcomputing.checkins.security.GoogleServiceConfiguration;
import io.micronaut.context.annotation.Property;
import io.micronaut.context.annotation.Requires;
import io.micronaut.context.env.Environment;

import jakarta.inject.Singleton;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Requires(notEnv = Environment.TEST)
@Singleton
public class GoogleAccessor {

    private final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private final NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
    private final String applicationName;
    private final GoogleAuthenticator authenticator;
    private final Environment environment;

    public GoogleAccessor(@Property(name = "check-ins.application.name") String applicationName,
                          GoogleAuthenticator authenticator,
                          Environment environment) throws GeneralSecurityException, IOException {
        this.applicationName = applicationName;
        this.authenticator = authenticator;
        this.environment = environment;
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
