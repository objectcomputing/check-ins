package com.objectcomputing;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import io.micronaut.context.annotation.Property;

import javax.inject.Singleton;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Collection;

@Singleton
public class GoogleDriveAccessor {
    private static final String CREDENTIALS_FILE_PATH = "/secrets/credentials.json";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";


    private final NetHttpTransport httpTransport;

    private final String applicationName;

    private final Collection<String> scopes;

    /**
     * Creates a google drive utility for quick access
     * @param applicationName the name of this application
     * @param scopes, the scope(s) of access to request for this application
     */
    public GoogleDriveAccessor(@Property(name = "oci-google-drive.application.name") String applicationName,
                               @Property(name = "oci-google-drive.application.scopes") Collection<String> scopes)
            throws GeneralSecurityException, IOException {
        this.httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        this.applicationName = applicationName;
        this.scopes = scopes;
    }

    /**
     * Creates an authorized Credential object.
     * @return An authorized Credential object.
     * @throws IOException If the credentials.json file cannot be found.
     */
    private Credential setupCredentials() throws IOException {
        InputStream in = GoogleDriveAccessor.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                httpTransport, JSON_FACTORY, clientSecrets, scopes)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

    /**
     * Create and return the google drive access object
     * @return a google drive access object
     * @throws IOException
     */
    public Drive accessGoogleDrive() throws IOException {
        return new Drive
                .Builder(httpTransport, JSON_FACTORY, setupCredentials())
                .setApplicationName(applicationName)
                .build();
    }

}
