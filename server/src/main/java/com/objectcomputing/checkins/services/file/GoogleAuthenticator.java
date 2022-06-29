package com.objectcomputing.checkins.services.file;

import com.google.auth.oauth2.GoogleCredentials;
import com.objectcomputing.checkins.security.GoogleServiceConfiguration;
import io.micronaut.context.annotation.Property;

import jakarta.inject.Singleton;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Collection;

@Singleton
public class GoogleAuthenticator {

    private final Collection<String> scopes;
    private final GoogleServiceConfiguration gServiceConfig;

    /**
     * Creates a google drive utility for quick access
     *
     * @param scopes, the scope(s) of access to request for this application
     * @param gServiceConfig, Google Drive configuration properties
     */
    public GoogleAuthenticator(@Property(name = "check-ins.application.scopes") Collection<String> scopes,
                               GoogleServiceConfiguration gServiceConfig) {
        this.scopes = scopes;
        this.gServiceConfig = gServiceConfig;
    }

    /**
     * Creates an authorized Credential object.
     *
     * @return An authorized Credential object.
     * @throws IOException If the configured file cannot be found.
     */
    GoogleCredentials setupCredentials() throws IOException {

        InputStream in = new ByteArrayInputStream(gServiceConfig.toString().getBytes(StandardCharsets.UTF_8));
        GoogleCredentials credentials = GoogleCredentials.fromStream(in);

        if (credentials == null) {
            credentials = GoogleCredentials.getApplicationDefault();
            throw new FileNotFoundException("Credentials not found while using Google default credentials");
        }

        return scopes.isEmpty() ? credentials : credentials.createScoped(scopes);
    }
}

