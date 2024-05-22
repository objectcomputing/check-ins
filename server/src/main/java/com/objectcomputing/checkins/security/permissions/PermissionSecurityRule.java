package com.objectcomputing.checkins.security.permissions;

import com.objectcomputing.checkins.services.permissions.Permission;
import com.objectcomputing.checkins.services.permissions.RequiredPermission;
import com.objectcomputing.checkins.services.role.role_permissions.RolePermission;
import com.objectcomputing.checkins.services.role.role_permissions.RolePermissionServices;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpAttributes;
import io.micronaut.http.HttpRequest;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.rules.SecuredAnnotationRule;
import io.micronaut.security.rules.SecurityRule;
import io.micronaut.security.rules.SecurityRuleResult;
import io.micronaut.web.router.MethodBasedRouteMatch;
import io.micronaut.web.router.RouteMatch;
import jakarta.inject.Singleton;
import org.json.JSONArray;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Optional;

@Singleton
public class PermissionSecurityRule implements SecurityRule<HttpRequest<?>> {

    public static final Integer ORDER = SecuredAnnotationRule.ORDER - 100;

    public final RolePermissionServices rolePermissionServices;

    public PermissionSecurityRule(RolePermissionServices rolePermissionServices) {
        this.rolePermissionServices = rolePermissionServices;
    }

    @Override
    public int getOrder() {
        return ORDER;
    }

    @Override
    public Publisher<SecurityRuleResult> check(@Nullable HttpRequest request, @Nullable Authentication authentication) {
        RouteMatch routeMatch = request.getAttribute(HttpAttributes.ROUTE_MATCH, RouteMatch.class).orElse(null);
        if (routeMatch instanceof MethodBasedRouteMatch methodBasedRouteMatch
                && methodBasedRouteMatch.hasAnnotation(RequiredPermission.class)) {
            Optional<String> optionalPermission = methodBasedRouteMatch.findAnnotation(RequiredPermission.class).flatMap(r -> r.stringValue("value"));

            Map<String, Object> claims = authentication != null ? authentication.getAttributes() : null;
            if (optionalPermission.isPresent() && claims != null && claims.containsKey("roles")) {
                final Permission requiredPermission = Permission.valueOf(optionalPermission.get());

                JSONArray jsonArray = new JSONArray(claims.get("roles").toString());

                boolean requiredPermissionFoundInRoles = jsonArray.toList().stream()
                        .map(Object::toString)
                        .flatMap(role -> rolePermissionServices.findByRole(role).stream())
                        .map(RolePermission::getPermission)
                        .anyMatch(p -> p == requiredPermission);

                return Mono.just(requiredPermissionFoundInRoles ? SecurityRuleResult.ALLOWED : SecurityRuleResult.REJECTED);
            }
        }
        return Mono.just(SecurityRuleResult.UNKNOWN);
    }
}
