package com.objectcomputing.checkins.util.googleapiaccess;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.directory.Directory;
import com.google.api.services.drive.Drive;
import com.google.auth.http.HttpCredentialsAdapter;
import com.objectcomputing.checkins.configuration.CheckInsConfiguration;
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

    private final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private final NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
    private final GoogleAuthenticator authenticator;
    private final CheckInsConfiguration checkInsConfiguration;

    public GoogleAccessor(
            GoogleAuthenticator authenticator,
            CheckInsConfiguration checkInsConfiguration
    ) throws GeneralSecurityException, IOException {
        this.checkInsConfiguration = checkInsConfiguration;
        this.authenticator = authenticator;
    }

    /**
     * Create and return the google drive access object
     *
     * @return a google drive access object
     * @throws IOException
     */
    public Drive accessGoogleDrive() throws IOException {

        String apiScope = checkInsConfiguration.getApplication().getGoogleApi().getScopes().getScopeForDriveApi();
        List<String> scope = Collections.singletonList(apiScope);

        HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(authenticator.setupCredentials(scope));
        return new Drive
                .Builder(httpTransport, JSON_FACTORY, requestInitializer)
                .setApplicationName(checkInsConfiguration.getApplication().getName())
                .build();
    }

    /**
     * Create and return the google directory access object
     *
     * @return a google directory access object
     * @throws IOException
     */
    public Directory accessGoogleDirectory() throws IOException {

        String apiScope = checkInsConfiguration.getApplication().getGoogleApi().getScopes().getScopeForDirectoryApi();
        String delegatedUser = checkInsConfiguration.getApplication().getGoogleApi().getDelegatedUser();
        List<String> scope = Arrays.asList(apiScope.split(","));

        HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(
                authenticator.setupServiceAccountCredentials(scope, delegatedUser));

        return new Directory
                .Builder(httpTransport, JSON_FACTORY, requestInitializer)
                .setApplicationName(checkInsConfiguration.getApplication().getName())
                .build();
    }
}
