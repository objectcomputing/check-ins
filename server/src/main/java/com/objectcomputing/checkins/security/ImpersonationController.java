package com.objectcomputing.checkins.security;

import com.objectcomputing.checkins.Environments;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileServices;
import com.objectcomputing.checkins.services.permissions.Permission;
import com.objectcomputing.checkins.services.permissions.RequiredPermission;
import com.objectcomputing.checkins.services.role.RoleServices;
import com.objectcomputing.checkins.services.role.role_permissions.RolePermissionServices;
import io.micronaut.context.annotation.Requires;
import io.micronaut.context.env.Environment;
import io.micronaut.context.event.ApplicationEventPublisher;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.annotation.*;
import io.micronaut.http.cookie.Cookie;
import io.micronaut.http.cookie.SameSite;
import io.micronaut.http.netty.cookies.NettyCookie;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.event.LoginSuccessfulEvent;
import io.micronaut.security.handlers.LoginHandler;
import io.micronaut.security.rules.SecurityRule;
import io.micronaut.security.utils.SecurityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

@Requires(env = {Environments.LOCAL, Environment.DEVELOPMENT})
@Controller("/impersonation")
@ExecuteOn(TaskExecutors.BLOCKING)
@Secured(SecurityRule.IS_AUTHENTICATED)
public class ImpersonationController {
    public static final String JWT = "JWT";
    public static final String originalJWT = "OJWT";
    private static final Logger LOG = LoggerFactory.getLogger(ImpersonationController.class);
    protected final LoginHandler loginHandler;
    protected final ApplicationEventPublisher eventPublisher;
    private final MemberProfileServices memberProfileServices;
    private final RoleServices roleServices;
    private final RolePermissionServices rolePermissionServices;

    /**
     * @param loginHandler        A collaborator which helps to build HTTP response depending on success or failure.
     * @param eventPublisher      The application event publisher
     * @param roleServices              Role services
     * @param rolePermissionServices    Role permission services
     * @param memberProfileServices     Member profile services
     */
    public ImpersonationController(LoginHandler loginHandler,
                                   ApplicationEventPublisher eventPublisher,
                                   RoleServices roleServices,
                                   RolePermissionServices rolePermissionServices,
                                   MemberProfileServices memberProfileServices) {
        this.loginHandler = loginHandler;
        this.eventPublisher = eventPublisher;
        this.roleServices = roleServices;
        this.rolePermissionServices = rolePermissionServices;
        this.memberProfileServices = memberProfileServices;
    }

    @Consumes({MediaType.APPLICATION_FORM_URLENCODED, MediaType.APPLICATION_JSON})
    @Post("/begin")
    @RequiredPermission(Permission.CAN_IMPERSONATE_MEMBERS)
    public HttpResponse<Void> auth(HttpRequest<?> request, String email) {
                final Cookie jwt = request.getCookies().get(JWT);
                if (jwt == null) {
                    // The user is required to be logged in.  If this is null,
                    // we are in an impossible state!
                    LOG.error("Unable to locate the JWT");
            return HttpResponse.unauthorized();
                } else {
            LOG.info("Processing request to switch to user \'{}\'", email);
            Set<MemberProfile> memberProfiles = memberProfileServices.findByValues(null, null, null, null, email, null, Boolean.FALSE);
            Iterator<MemberProfile> iterator = memberProfiles.iterator();
            if(!iterator.hasNext()) return HttpResponse.badRequest();

            MemberProfile memberProfile = iterator.next();
            LOG.info("Profile exists for \'{}\'", email);
                            String firstName = memberProfile.getFirstName() != null ? memberProfile.getFirstName() : "";
                            String lastName = memberProfile.getLastName() != null ? memberProfile.getLastName() : "";
            Set<String> roles = roleServices.findUserRoles(memberProfile.getId()).stream().map(role -> role.getRole()).collect(Collectors.toSet());
            Set<String> permissions = rolePermissionServices.findUserPermissions(memberProfile.getId()).stream().map(permission -> permission.name()).collect(Collectors.toSet());

            Map<String, Object> newAttributes = new HashMap<>();
                            newAttributes.put("email", memberProfile.getWorkEmail());
                            newAttributes.put("name", firstName + ' ' + lastName);
                            newAttributes.put("picture", "");
            newAttributes.put("roles", roles);
            newAttributes.put("permissions", permissions);

            LOG.info("Building authentication");
            Authentication updatedAuth = Authentication.build(email, roles, newAttributes);
            LOG.info("Publishing login");
                            eventPublisher.publishEvent(new LoginSuccessfulEvent(updatedAuth, null, Locale.getDefault()));
                            // Store the old JWT to allow the user to revert the impersonation.
            LOG.info("Attempting to swap tokens");
                            return ((MutableHttpResponse)loginHandler.loginSuccess(updatedAuth, request)).cookie(
                                       new NettyCookie(originalJWT, jwt.getValue()).path("/").sameSite(SameSite.Strict)
                                       .maxAge(jwt.getMaxAge()));
                }
    }

    @Produces(MediaType.TEXT_HTML)
    @Get("/end")
    public HttpResponse<Object> revert(HttpRequest<?> request) {
        final Cookie ojwt = request.getCookies().get(originalJWT);
        if (ojwt == null) {
            return HttpResponse.badRequest();
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
