package com.objectcomputing.checkins.security;

import java.util.Collections;
import java.util.Map;

import com.objectcomputing.checkins.Environments;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.memberprofile.currentuser.CurrentUserServices;

import io.micronaut.context.annotation.Requires;
import io.micronaut.context.event.ApplicationEventPublisher;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.annotation.Consumes;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
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

@Requires(env = Environments.LOCAL)
@Controller("/oauth/login/google")
@Secured(SecurityRule.IS_ANONYMOUS)
public class LocalLoginController {

    protected final Authenticator authenticator;
    protected final LoginHandler loginHandler;
    protected final ApplicationEventPublisher eventPublisher;
    private final CurrentUserServices currentUserServices;

    /**
     * @param authenticator       {@link Authenticator} collaborator
     * @param loginHandler        A collaborator which helps to build HTTP response depending on success or failure.
     * @param eventPublisher      The application event publisher
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
        UsernamePasswordCredentials usernamePasswordCredentials = new UsernamePasswordCredentials(email, role);
        Flowable<AuthenticationResponse> authenticationResponseFlowable =
                Flowable.fromPublisher(authenticator.authenticate(request, usernamePasswordCredentials));
        return authenticationResponseFlowable.map(authenticationResponse -> {
            if (authenticationResponse.isAuthenticated() && authenticationResponse.getUserDetails().isPresent()) {
                UserDetails userDetails = authenticationResponse.getUserDetails().get();
                // Get member profile by work email
                MemberProfile memberProfile = currentUserServices.findOrSaveUser("", "", email);
                String firstName = memberProfile.getFirstName() != null ? memberProfile.getFirstName() : "";
                String lastName = memberProfile.getLastName() != null ? memberProfile.getLastName() : "";
                userDetails.setAttributes(Map.of("email", memberProfile.getWorkEmail(),
                        "name", firstName + ' ' + lastName, "picture", ""));
                eventPublisher.publishEvent(new LoginSuccessfulEvent(userDetails));
                return loginHandler.loginSuccess(userDetails, request);
            } else {
                eventPublisher.publishEvent(new LoginFailedEvent(authenticationResponse));
                return loginHandler.loginFailed(authenticationResponse, request);
            }
        }).first(HttpResponse.status(HttpStatus.UNAUTHORIZED));
    }

    public static String test(){
        String unused = "This variable is unused";
        string unknown = "string is highlighted as it should be String";
        System.out.println(unknown); // cant resolve method
        boolean alwaysTrue = true;
        if(alwaysTrue) { // This is a warning as alwaysTrue is always true.
            LOG.info(alwaysTrue);
        }

        return 1; // this is an error
    }

}
