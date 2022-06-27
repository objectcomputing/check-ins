package com.objectcomputing.checkins.services.file;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.auth.http.HttpCredentialsAdapter;
import io.micronaut.context.annotation.Property;

import jakarta.inject.Singleton;
import java.io.IOException;
import java.security.GeneralSecurityException;

@Singleton
public class GoogleDriveAccessor {

    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private final NetHttpTransport httpTransport;
    private final String applicationName;
    private final GoogleAuthenticator authenticator;

    public GoogleDriveAccessor(@Property(name = "check-ins.application.name") String applicationName,
                               GoogleAuthenticator authenticator) throws GeneralSecurityException, IOException {
        this.httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        this.applicationName = applicationName;
        this.authenticator = authenticator;
    }

    /**
     * Create and return the google drive access object
     *
     * @return a google drive access object
     * @throws IOException
     */
    public Drive accessGoogleDrive() throws IOException {
        HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(authenticator.setupCredentials());
        return new Drive
                .Builder(httpTransport, JSON_FACTORY, requestInitializer)
                .setApplicationName(applicationName)
                .build();
    }
}
