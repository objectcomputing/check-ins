package com.objectcomputing.checkins;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import io.micronaut.context.annotation.Property;

import javax.inject.Singleton;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.Collection;

@Singleton
public class GoogleAuthenticator {
    private static final String CREDENTIALS_FILE_PATH = "/secrets/credentials.json";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    private final NetHttpTransport httpTransport;

    private final Collection<String> scopes;

    /**
     * Creates a google drive utility for quick access
     *
     * @param scopes, the scope(s) of access to request for this application
     */
    public GoogleAuthenticator(@Property(name = "check-ins.application.scopes") Collection<String> scopes)
            throws GeneralSecurityException, IOException {
        this.httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        this.scopes = scopes;
    }

    /**
     * Creates an authorized Credential object.
     *
     * @return An authorized Credential object.
     * @throws IOException If the credentials.json file cannot be found.
     */
    Credential setupCredentials() throws IOException {
        InputStream in = GoogleDriveAccessor.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }

        // return GoogleCredential.fromStream(in, httpTransport, JSON_FACTORY).createDelegated("kimberlinm@objectcomputing.com ").createScoped(scopes);
        return GoogleCredential.fromStream(in, httpTransport, JSON_FACTORY).createScoped(scopes);
        
    }

}
