package com.objectcomputing.checkins.security;

import com.objectcomputing.checkins.Environments;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.memberprofile.currentuser.CurrentUserServices;
import com.objectcomputing.checkins.security.ImpersonationController;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.netty.cookies.NettyCookie;
import io.micronaut.context.annotation.Requires;
import io.micronaut.context.event.ApplicationEventPublisher;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Consumes;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.authentication.AuthenticationResponse;
import io.micronaut.security.authentication.Authenticator;
import io.micronaut.security.authentication.UsernamePasswordCredentials;
import io.micronaut.security.event.LoginFailedEvent;
import io.micronaut.security.event.LoginSuccessfulEvent;
import io.micronaut.security.handlers.LoginHandler;
import io.micronaut.security.rules.SecurityRule;
import io.micronaut.views.View;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Requires(env = Environments.LOCAL)
@Controller("/oauth/login/google")
@ExecuteOn(TaskExecutors.BLOCKING)
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

    @ExecuteOn(TaskExecutors.BLOCKING)
    @Consumes({MediaType.APPLICATION_FORM_URLENCODED, MediaType.APPLICATION_JSON})
    @Post
    public Mono<Object> auth(HttpRequest<?> request, String email, String role) {
        UsernamePasswordCredentials usernamePasswordCredentials = new UsernamePasswordCredentials(email, role);
        Flux<AuthenticationResponse> authenticationResponseFlux =
                Flux.from(authenticator.authenticate(request, usernamePasswordCredentials));
        return authenticationResponseFlux.map(authenticationResponse -> {
            if (authenticationResponse.isAuthenticated() && authenticationResponse.getAuthentication().isPresent()) {
                Authentication authentication = authenticationResponse.getAuthentication().get();
                // Get member profile by work email
                MemberProfile memberProfile = currentUserServices.findOrSaveUser("", "", email);
                String firstName = memberProfile.getFirstName() != null ? memberProfile.getFirstName() : "";
                String lastName = memberProfile.getLastName() != null ? memberProfile.getLastName() : "";

                Map<String, Object> newAttributes = new HashMap<>(authentication.getAttributes());
                newAttributes.put("email", memberProfile.getWorkEmail());
                newAttributes.put("name", firstName + ' ' + lastName);
                newAttributes.put("picture", "");
                Authentication updatedAuth = Authentication.build(authentication.getName(), authentication.getRoles(), newAttributes);

                eventPublisher.publishEvent(new LoginSuccessfulEvent(updatedAuth, null, Locale.getDefault()));

                // Remove the original JWT on login.
                return ((MutableHttpResponse)loginHandler.loginSuccess(updatedAuth, request))
                           .cookie(new NettyCookie(ImpersonationController.originalJWT, "").path("/").maxAge(0));
            } else {
                eventPublisher.publishEvent(new LoginFailedEvent(authenticationResponse, null, null, Locale.getDefault()));
                return loginHandler.loginFailed(authenticationResponse, request);
            }
        }).single(Mono.just(HttpResponse.status(HttpStatus.UNAUTHORIZED)));
    }
}
