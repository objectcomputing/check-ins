package com.objectcomputing.checkins.services.request_notifications;


import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Header;
import io.micronaut.scheduling.TaskExecutors;
import io.netty.channel.EventLoopGroup;
import jakarta.annotation.security.PermitAll;
import jakarta.inject.Named;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.concurrent.ExecutorService;

@Controller("/services/feedback/daily-request-check")
@PermitAll
public class CheckServicesController {
    private final CheckServices checkServices;
    private final EventLoopGroup eventLoopGroup;
    private final ExecutorService ioExecutorService;
    private final GoogleIdTokenVerifier verifier =
            new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                    .setAudience(Collections.singletonList("https://checkins.objectcomputing.com/services/feedback/daily-request-check"))
                    .build();

    public CheckServicesController(CheckServices checkServices, EventLoopGroup eventLoopGroup,
                              @Named(TaskExecutors.IO) ExecutorService ioExecutorService) {
        this.checkServices = checkServices;
        this.eventLoopGroup = eventLoopGroup;
        this.ioExecutorService = ioExecutorService;
    }

    @Get
    public Mono<? extends HttpResponse<?>> GetTodaysRequests(@Header("Authorization") String authorizationHeader) {
        System.out.println("!!!!!!-"+authorizationHeader);
        String authorization = authorizationHeader.split(" ")[1];
        GoogleIdToken idToken = null;
        try {
            idToken = verifier.verify(authorization);
            GoogleIdToken.Payload payload = idToken.getPayload();

            // Print user identifier
            String userId = payload.getSubject();
            System.out.println("User ID: " + userId);

            // Get profile information from payload
            String email = payload.getEmail();
            System.out.println("Email:"+ email);
            String hostedDomain = payload.getHostedDomain();
            System.out.println("Hosted Domain" + hostedDomain);
            String prettyString = payload.toPrettyString();
            System.out.println(prettyString);
            boolean emailVerified = payload.getEmailVerified();
            System.out.println("emailVerified:" + emailVerified);
//            String name = (String) payload.get("name");
//            String pictureUrl = (String) payload.get("picture");
//            String locale = (String) payload.get("locale");
//            String familyName = (String) payload.get("family_name");
//            String givenName = (String) payload.get("given_name");
        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException(e);
        }

        GoogleIdToken.Payload payload = idToken.getPayload();
        return Mono.fromCallable(checkServices::GetTodaysRequests)
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .map(success -> (HttpResponse<?>) HttpResponse.ok())
                .subscribeOn(Schedulers.fromExecutor(ioExecutorService));

    }

}
