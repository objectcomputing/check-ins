package com.objectcomputing.checkins.services.request_notifications;


import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.objectcomputing.checkins.exceptions.BadArgException;
import io.micronaut.context.annotation.Value;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Header;
import io.micronaut.scheduling.TaskExecutors;
import io.netty.channel.EventLoopGroup;
import jakarta.annotation.security.PermitAll;
import jakarta.inject.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Collections;
import java.util.concurrent.ExecutorService;

@Controller("/services/feedback/daily-request-check")
@PermitAll
public class CheckServicesController {

    private static final Logger LOG = LoggerFactory.getLogger(CheckServicesController.class);
    private final CheckServices checkServices;
    private final EventLoopGroup eventLoopGroup;
    private final ExecutorService ioExecutorService;

    @Value("${check-ins.web-address}")
    private String webAddress;

    private final GoogleIdTokenVerifier verifier =
            new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                    //one dev and one prod client id
                    .setAudience(Collections.singletonList(webAddress+"/services/feedback/daily-request-check"))
                    .build();

    public CheckServicesController(CheckServices checkServices, EventLoopGroup eventLoopGroup,
                              @Named(TaskExecutors.IO) ExecutorService ioExecutorService) {
        this.checkServices = checkServices;
        this.eventLoopGroup = eventLoopGroup;
        this.ioExecutorService = ioExecutorService;
    }

    @Get
    public Mono<? extends HttpResponse<?>> sendScheduledEmails(@Header("Authorization") String authorizationHeader) {
        try {
            String authorization = authorizationHeader.split(" ")[1];
            GoogleIdToken idToken = verifier.verify(authorization);
            //only one service account
            assert idToken.getPayload().getEmail().equals("sa-checkins@oci-intern-2019.iam.gserviceaccount.com");
            assert idToken.getPayload().getEmailVerified();
        } catch (Throwable e) {
            LOG.info("Authentication error", e.fillInStackTrace());
            throw new BadArgException(e.getMessage());
        }
        return Mono.fromCallable(checkServices::sendScheduledEmails)
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .map(success -> (HttpResponse<?>) HttpResponse.ok())
                .subscribeOn(Schedulers.fromExecutor(ioExecutorService));

    }

}
