package com.objectcomputing.checkins.services.file;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import io.micronaut.context.annotation.Property;

import javax.inject.Singleton;
import java.io.IOException;
import java.security.GeneralSecurityException;

@Singleton
public class GoogleDriveAccessor {

    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private final NetHttpTransport httpTransport;
    private final String applicationName;
    private GoogleAuthenticator authenticator;

    /**
     * Creates a google drive utility for quick access
     *
     * @param applicationName the name of this application
     */
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
        return new Drive
                .Builder(httpTransport, JSON_FACTORY, authenticator.setupCredentials())
                .setApplicationName(applicationName)
                .build();
    }
}
