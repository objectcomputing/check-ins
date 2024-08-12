package com.objectcomputing.checkins.security;

import com.objectcomputing.checkins.Environments;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.memberprofile.currentuser.CurrentUserServices;
import com.objectcomputing.checkins.services.permissions.Permission;
import com.objectcomputing.checkins.services.permissions.RequiredPermission;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.cookie.SameSite;
import io.micronaut.http.cookie.Cookie;
import io.micronaut.http.netty.cookies.NettyCookie;
import io.micronaut.security.utils.SecurityService;
import io.micronaut.context.annotation.Requires;
import io.micronaut.context.event.ApplicationEventPublisher;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Consumes;
import io.micronaut.http.annotation.Produces;
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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.HashSet;
import java.util.Set;
import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Requires(env = Environments.LOCAL)
@Controller("/impersonation")
@ExecuteOn(TaskExecutors.BLOCKING)
@Secured(SecurityRule.IS_AUTHENTICATED)
public class ImpersonationController {
    public static final String JWT = "JWT";
    public static final String originalJWT = "OJWT";
    private static final Logger LOG = LoggerFactory.getLogger(ImpersonationController.class);
    protected final Authenticator authenticator;
    protected final LoginHandler loginHandler;
    protected final ApplicationEventPublisher eventPublisher;
    private final CurrentUserServices currentUserServices;
    private final SecurityService securityService;

    /**
     * @param authenticator       {@link Authenticator} collaborator
     * @param loginHandler        A collaborator which helps to build HTTP response depending on success or failure.
     * @param eventPublisher      The application event publisher
     * @param currentUserServices Current User services
     * @param securityService     The Security Service
     */
    public ImpersonationController(Authenticator authenticator,
                                   LoginHandler loginHandler,
                                   ApplicationEventPublisher eventPublisher,
                                   CurrentUserServices currentUserServices,
                                   SecurityService securityService) {
        this.authenticator = authenticator;
        this.loginHandler = loginHandler;
        this.eventPublisher = eventPublisher;
        this.currentUserServices = currentUserServices;
        this.securityService = securityService;
    }

    @Consumes({MediaType.APPLICATION_FORM_URLENCODED, MediaType.APPLICATION_JSON})
    @Post("/begin")
    @RequiredPermission(Permission.CAN_IMPERSONATE_MEMBERS)
    public Mono<Object> auth(HttpRequest<?> request, String email) {
        if (securityService != null) {
            Optional<Authentication> auth = securityService.getAuthentication();
            if (auth.isPresent() && auth.get().getAttributes().get("email") != null) {
                final Cookie jwt = request.getCookies().get(JWT);
                if (jwt == null) {
                    // The user is required to be logged in.  If this is null,
                    // we are in an impossible state!
                    LOG.error("Unable to locate the JWT");
                } else {
                    UsernamePasswordCredentials usernamePasswordCredentials = new UsernamePasswordCredentials(email, "");
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
                            // Store the old JWT to allow the user to revert the impersonation.
                            return ((MutableHttpResponse)loginHandler.loginSuccess(updatedAuth, request)).cookie(
                                       new NettyCookie(originalJWT, jwt.getValue()).path("/").sameSite(SameSite.Strict)
                                       .maxAge(jwt.getMaxAge()));
                        } else {
                            eventPublisher.publishEvent(new LoginFailedEvent(authenticationResponse, null, null, Locale.getDefault()));
                            return loginHandler.loginFailed(authenticationResponse, request);
                        }
                    }).single(Mono.just(HttpResponse.unauthorized()));
                }
            } else {
                LOG.error("Attempted impersonation without authentication.");
            }
        }
        return null;
    }

    @Produces(MediaType.TEXT_HTML)
    @Get("/end")
    public HttpResponse<Object> revert(HttpRequest<?> request) {
        final Cookie ojwt = request.getCookies().get(originalJWT);
        if (ojwt == null) {
            return HttpResponse.unauthorized();
        } else {
            // Swap the OJWT back to the JWT and remove the original JWT
            Set<Cookie> cookies = new HashSet<Cookie>();
            cookies.add(new NettyCookie(JWT, ojwt.getValue()).path("/")
                              .sameSite(SameSite.Strict)
                              .maxAge(ojwt.getMaxAge()).httpOnly());
            cookies.add(new NettyCookie(originalJWT, "").path("/").maxAge(0));

            // Redirect to "/" while setting the cookies.
            return HttpResponse.temporaryRedirect(URI.create("/"))
                                  .cookies(cookies);
        }
    }
}
