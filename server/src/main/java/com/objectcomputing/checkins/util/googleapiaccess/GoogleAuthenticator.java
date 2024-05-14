package com.objectcomputing.checkins.util.googleapiaccess;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.objectcomputing.checkins.security.GoogleServiceConfiguration;
import io.micronaut.context.annotation.Requires;
import io.micronaut.context.env.Environment;
import jakarta.inject.Singleton;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Requires(notEnv = Environment.TEST)
@Singleton
public class GoogleAuthenticator {

    private static final Logger LOG = LoggerFactory.getLogger(GoogleAuthenticator.class);

    private GoogleServiceConfiguration gServiceConfig;

    /**
     * Creates a google drive utility for quick access
     * @param gServiceConfig, Google Drive configuration properties
     */
    public GoogleAuthenticator(GoogleServiceConfiguration gServiceConfig) {
        this.gServiceConfig = gServiceConfig;
    }

    /**
     * Creates an authorized GoogleCredentials object.
     * @param scopes, the scope(s) of access to request for this application
     * @return An authorized GoogleCredentials object.
     * @throws IOException If the service account configurations cannot be found.
     */
    GoogleCredentials setupCredentials(@NotNull final List<String> scopes) throws IOException {
        InputStream in = new ByteArrayInputStream(gServiceConfig.toString().getBytes(StandardCharsets.UTF_8));
        GoogleCredentials credentials = GoogleCredentials.fromStream(in);

        if (credentials == null) {
            credentials = GoogleCredentials.getApplicationDefault();
            throw new FileNotFoundException("Credentials not found while using Google default credentials");
        }

        return scopes.isEmpty() ? credentials : credentials.createScoped(scopes);
    }

    /**
     * Creates an authorized ServiceAccountCredentials object.
     * @param scopes, the scope(s) of access to request for this application
     * @param delegatedUser, the email of the delegated user
     * @return An authorized ServiceAccountCredentials object.
     * @throws IOException If the service account configurations cannot be found.
     */
    ServiceAccountCredentials setupServiceAccountCredentials(@NotNull final List<String> scopes, @NotNull final String delegatedUser) throws IOException {

        ServiceAccountCredentials sourceCredentials = null;
        try(InputStream in = new ByteArrayInputStream(gServiceConfig.toString().getBytes(StandardCharsets.UTF_8))) {
            sourceCredentials = ServiceAccountCredentials.fromStream(in);
            sourceCredentials = (ServiceAccountCredentials) sourceCredentials.createScoped(scopes).createDelegated(delegatedUser);
        } catch (IOException e) {
            LOG.error("An error occurred while reading the service account credentials.", e);
        }
        return sourceCredentials;
    }
}

