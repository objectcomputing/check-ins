package com.objectcomputing.checkins.util.googleapiaccess;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ImpersonatedCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.objectcomputing.checkins.security.GoogleServiceConfiguration;
import io.micronaut.context.annotation.Requires;
import io.micronaut.context.env.Environment;

import javax.inject.Singleton;
import javax.validation.constraints.NotNull;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Singleton
@Requires(notEnv = Environment.TEST)
public class GoogleAuthenticator {

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
     * Creates an authorized ImpersonatedCredentials object.
     * @param scopes, the scope(s) of access to request for this application
     * @param impersonatedUser, the email of the impersonated user
     * @return An authorized ImpersonatedCredentials object.
     * @throws IOException If the service account configurations cannot be found.
     */
    ImpersonatedCredentials setupImpersonatedCredentials(@NotNull final List<String> scopes, @NotNull final String impersonatedUser) throws IOException {
        ImpersonatedCredentials targetCredentials = null;
        ServiceAccountCredentials sourceCredentials;
        try(InputStream in = new ByteArrayInputStream(gServiceConfig.toString().getBytes(StandardCharsets.UTF_8))) {
            sourceCredentials = ServiceAccountCredentials.fromStream(in);
            sourceCredentials = (ServiceAccountCredentials) sourceCredentials.createScoped(scopes);
            targetCredentials = ImpersonatedCredentials.create(sourceCredentials,
                    impersonatedUser, null, scopes, 3600);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return targetCredentials;
    }
}

