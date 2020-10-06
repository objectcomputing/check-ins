package com.objectcomputing.checkins.services.file;

import com.google.auth.oauth2.GoogleCredentials;
import io.micronaut.context.annotation.Property;

import javax.inject.Singleton;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Collection;

@Singleton
public class GoogleAuthenticator {

    private final Collection<String> scopes;
    private DriveConfiguration driveConfiguration;

    /**
     * Creates a google drive utility for quick access
     *
     * @param scopes, the scope(s) of access to request for this application
     * @param driveConfiguration, Google Drive configuration properties
     */
    public GoogleAuthenticator(@Property(name = "check-ins.application.scopes") Collection<String> scopes,
                               DriveConfiguration driveConfiguration) {
        this.scopes = scopes;
        this.driveConfiguration = driveConfiguration;
    }

    /**
     * Creates an authorized Credential object.
     *
     * @return An authorized Credential object.
     * @throws IOException If the configured file cannot be found.
     */
    GoogleCredentials setupCredentials() throws IOException {

        InputStream in = new ByteArrayInputStream(driveConfiguration.toString().getBytes(StandardCharsets.UTF_8));
        GoogleCredentials credentials = GoogleCredentials.fromStream(in);

        if (credentials == null) {
            credentials = GoogleCredentials.getApplicationDefault();
            throw new FileNotFoundException("Credentials not found while using Google default credentials");
        }

        return scopes.isEmpty() ? credentials : credentials.createScoped(scopes);
    }
}

