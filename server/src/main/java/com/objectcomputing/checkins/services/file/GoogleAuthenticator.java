package com.objectcomputing.checkins.services.file;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.http.HttpTransport;
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import io.micronaut.context.annotation.Property;
import javax.inject.Singleton;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.Collection;

@Singleton
public class GoogleAuthenticator {

    private final Collection<String> scopes;
    private final String googleCredentials;
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    GoogleCredentials credentials = null;


    /**
     * Creates a google drive utility for quick access
     *
     * @param scopes, the scope(s) of access to request for this application
     */
    public GoogleAuthenticator(@Property(name = "check-ins.application.scopes") Collection<String> scopes,
                               @Property(name = "google.credentials") String googleCredentials) {
        this.scopes = scopes;
        this.googleCredentials = googleCredentials;
    }

    /**
     * Creates an authorized Credential object.
     *
     * @return An authorized Credential object.
     * @throws IOException If the credentials.json file cannot be found.
     */
    GoogleCredentials setupCredentials() throws IOException {
//         // Load client secrets
//        InputStream in = new ByteArrayInputStream(googleCredentials.getBytes(StandardCharsets.UTF_8));
//                //GoogleAuthenticator.class.getResourceAsStream(googleCredentials);
//                //new FileInputStream(PRIVATE_KEY_PATH);
//                //GoogleAuthenticator.class.getResourceAsStream(googleCredentials);
//                //new ByteArrayInputStream(googleCredentials.getBytes(StandardCharsets.UTF_8));
//        if (in == null) {
//            throw new FileNotFoundException("Google Credential resource not found");
//        }
//
//        GoogleCredentials credentials = GoogleCredentials.fromStream(in);
//        credentials.refreshIfExpired();
//        AccessToken token = credentials.getAccessToken();
//
//        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));
//
//        // Build flow and trigger user authorization request.
//        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
//                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, scopes)
//                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
//                .setAccessType("offline")
//                .build();
//        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
//        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");

        final File file = new File(googleCredentials);
        if (file.exists()) {
            try (final FileInputStream s = new FileInputStream(file)) {
                credentials = GoogleCredentials.fromStream(s);
                throw new FileNotFoundException("Using Google Credentials from file: " + file.getAbsolutePath());
            }
        }

        if (credentials == null) {
            credentials = GoogleCredentials.getApplicationDefault();
            throw new FileNotFoundException("Using Google Application Default Credentials");
        }

        return scopes.isEmpty() ? credentials : credentials.createScoped(scopes);
    }
}