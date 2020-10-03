package com.objectcomputing.checkins.services.file;

import com.google.auth.oauth2.GoogleCredentials;
import io.micronaut.context.annotation.Property;
import org.json.JSONObject;

import javax.inject.Singleton;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Collection;

@Singleton
public class GoogleAuthenticator {

    private final Collection<String> scopes;
    GoogleCredentials credentials = null;


    /**
     * Creates a google drive utility for quick access
     *
     * @param scopes, the scope(s) of access to request for this application
     */
    public GoogleAuthenticator(@Property(name = "check-ins.application.scopes") Collection<String> scopes) {
        this.scopes = scopes;
    }

    /**
     * Creates an authorized Credential object.
     *
     * @return An authorized Credential object.
     * @throws IOException If the configured  file cannot be found.
     */
    GoogleCredentials setupCredentials() throws IOException {

        // Load client secrets
        //insert secrets here for testing

        InputStream in = new ByteArrayInputStream(jsonString.getBytes(StandardCharsets.UTF_8));
        credentials = GoogleCredentials.fromStream(in);

        if (credentials == null) {
        credentials = GoogleCredentials.getApplicationDefault();
        throw new FileNotFoundException("Using Google Application Default Credentials");
        }

        return scopes.isEmpty() ? credentials : credentials.createScoped(scopes);
    }
}

