package com.objectcomputing.checkins.services.request_notifications;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.objectcomputing.checkins.exceptions.BadArgException;
import io.micronaut.context.annotation.Value;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;

@Singleton
public class ServiceAccountVerifier {

    private static final Logger LOG = LoggerFactory.getLogger(ServiceAccountVerifier.class);

    @Value("${check-ins.web-address}")
    private String webAddress;
    private final GoogleIdTokenVerifier verifier =
            new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                    //one dev and one prod client id
                    .setAudience(Collections.singletonList(webAddress + "/services/feedback/daily-request-check"))
                    .build();

    public void verify(String authorization) {
        try {
            LOG.info(authorization);
            LOG.info(webAddress);
            GoogleIdToken idToken = verifier.verify(authorization);
            //only one service account
            assert idToken.getPayload().getEmail().equals("sa-checkins@oci-intern-2019.iam.gserviceaccount.com");
            assert idToken.getPayload().getEmailVerified();
        } catch (Throwable e) {
            LOG.info("Authentication error", e.fillInStackTrace());
            throw new BadArgException("Authentication error");
        }
    }
}
