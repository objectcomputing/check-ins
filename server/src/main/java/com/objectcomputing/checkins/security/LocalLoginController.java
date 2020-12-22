package com.objectcomputing.checkins.security;

import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.memberprofile.currentuser.CurrentUserServices;
import io.micronaut.context.annotation.Property;
import io.micronaut.context.annotation.Requires;
import io.micronaut.context.event.ApplicationEventPublisher;
import io.micronaut.core.io.buffer.ByteBuffer;
import io.micronaut.http.*;
import io.micronaut.http.annotation.Consumes;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.client.RxHttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.cookie.Cookies;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.authentication.AuthenticationResponse;
import io.micronaut.security.authentication.Authenticator;
import io.micronaut.security.authentication.UserDetails;
import io.micronaut.security.authentication.UsernamePasswordCredentials;
import io.micronaut.security.event.LoginFailedEvent;
import io.micronaut.security.event.LoginSuccessfulEvent;
import io.micronaut.security.handlers.LoginHandler;
import io.micronaut.security.rules.SecurityRule;
import io.micronaut.views.View;
import io.reactivex.Flowable;
import io.reactivex.Single;

import javax.inject.Inject;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

@Requires(env = "local")
//@Controller("/oauth/login/google")
@Controller("/oauth/login/keycloak")
@Secured(SecurityRule.IS_ANONYMOUS)
public class LocalLoginController {

    @Property(name = "micronaut.security.oauth2.clients.keycloak.client-id")
    private String clientId;
    @Property(name = "micronaut.security.oauth2.clients.keycloak.client-secret")
    private String clientSecret;

    protected final Authenticator authenticator;
    protected final LoginHandler loginHandler;
    protected final ApplicationEventPublisher eventPublisher;
    private final CurrentUserServices currentUserServices;

    /**
     * @param authenticator  {@link Authenticator} collaborator
     * @param loginHandler   A collaborator which helps to build HTTP response depending on success or failure.
     * @param eventPublisher The application event publisher
     * @param currentUserServices Current User services
     */
    public LocalLoginController(Authenticator authenticator,
                                LoginHandler loginHandler,
                                ApplicationEventPublisher eventPublisher,
                                CurrentUserServices currentUserServices) {
        this.authenticator = authenticator;
        this.loginHandler = loginHandler;
        this.eventPublisher = eventPublisher;
        this.currentUserServices = currentUserServices;
    }

    @View("login")
    @Get
    public Map<String, String> login() {
        return Collections.emptyMap();
    }

    @Consumes({MediaType.APPLICATION_FORM_URLENCODED, MediaType.APPLICATION_JSON})
    @Post
    public Single<MutableHttpResponse<?>> auth(HttpRequest<?> request, String email, String role) {
//        UsernamePasswordCredentials usernamePasswordCredentials = new UsernamePasswordCredentials(email, role);
        UsernamePasswordCredentials usernamePasswordCredentials = new UsernamePasswordCredentials("warnerj", "password");
        System.out.print("*********I was here first****************************************\n");
        Flowable<AuthenticationResponse> authenticationResponseFlowable =
                Flowable.fromPublisher(authenticator.authenticate(request, usernamePasswordCredentials));
        return authenticationResponseFlowable.map(authenticationResponse -> {
            System.out.print("*********authResponse*************" + authenticationResponse.getUserDetails().get() + "***************************\n");
            System.out.print("*********authenticated*************" + authenticationResponse.isAuthenticated() + "***************************\n");
            System.out.print("*********isPresent*************" + authenticationResponse.getUserDetails().isPresent() + "***************************\n");
            if (authenticationResponse.isAuthenticated() && authenticationResponse.getUserDetails().isPresent()) {
                UserDetails userDetails = authenticationResponse.getUserDetails().get();
                System.out.print("*********userDeets*************" + userDetails.getUsername() + "***************************\n");
                System.out.print("*********userDeetsMessage************" + userDetails.getMessage() + "***************************\n");
                MemberProfile memberProfile = currentUserServices.findOrSaveUser(email, email);
                String name = memberProfile.getName() != null ? memberProfile.getName() : "";
                userDetails.setAttributes(Map.of("email", memberProfile.getWorkEmail(), "name", name,
                        "picture", ""));
                LoginSuccessfulEvent lse = new LoginSuccessfulEvent(userDetails);
                System.out.print("*********login event*************" + lse.getSource().toString() + "***************************\n");
                eventPublisher.publishEvent(new LoginSuccessfulEvent(userDetails));
                MutableHttpResponse throwAway = loginHandler.loginSuccess(userDetails, request);
                System.out.print("*********response status*************" + throwAway.getStatus() + "***************************\n");
                return loginHandler.loginSuccess(userDetails, request);
            } else {
                System.out.print("*********This is a failed event***************************\n");
                eventPublisher.publishEvent(new LoginFailedEvent(authenticationResponse));
                return loginHandler.loginFailed(authenticationResponse, request);
            }
        }).first(HttpResponse.status(HttpStatus.UNAUTHORIZED));
    }
}