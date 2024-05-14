package com.objectcomputing.checkins.security.permissions;

import com.objectcomputing.checkins.services.permissions.Permission;
import com.objectcomputing.checkins.services.permissions.RequiredPermission;
import com.objectcomputing.checkins.services.role.role_permissions.RolePermissionServices;
import io.micronaut.core.annotation.AnnotationValue;
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

import java.util.*;
import java.util.stream.Collectors;

@Singleton
public class PermissionSecurityRule implements SecurityRule<HttpRequest<?>> {

    public static final Integer ORDER = SecuredAnnotationRule.ORDER - 100;

    public final RolePermissionServices rolePermissionServices;

    public PermissionSecurityRule(RolePermissionServices rolePermissionServices) {
        this.rolePermissionServices = rolePermissionServices;
    }

    public int getOrder() {
        return ORDER;
    }

    @Override
    public Publisher<SecurityRuleResult> check(@Nullable HttpRequest request, @Nullable Authentication authentication) {
        RouteMatch routeMatch = request.getAttribute(HttpAttributes.ROUTE_MATCH, RouteMatch.class).orElse(null);
        if (routeMatch instanceof MethodBasedRouteMatch) {
            MethodBasedRouteMatch methodBasedRouteMatch = (MethodBasedRouteMatch) routeMatch;
            if (methodBasedRouteMatch.hasAnnotation(RequiredPermission.class)) {

                AnnotationValue<RequiredPermission> requiredPermissionAnnotation =
                        methodBasedRouteMatch.getAnnotation(RequiredPermission.class);

                Optional<String> optionalPermission = requiredPermissionAnnotation != null ?
                        requiredPermissionAnnotation.stringValue("value") : Optional.empty();

                Map<String, Object> claims = authentication != null ? authentication.getAttributes() : null;

                if (optionalPermission.isPresent() && claims != null && claims.containsKey("roles")) {

                    final Permission requiredPermission = Permission.valueOf(optionalPermission.get());
                    final Set<Permission> userPermissions = new HashSet<>();

                    JSONArray jsonArray = new JSONArray(claims.get("roles").toString());
                    final List<String> roles = jsonArray.toList().stream().map(Object::toString).collect(Collectors.toList());


                    roles.forEach(role -> rolePermissionServices.findByRole(role)
                            .forEach(rolePermission -> userPermissions.add(rolePermission.getPermission()))
                    );


                    return Mono.just(userPermissions.contains(requiredPermission)
                            ? SecurityRuleResult.ALLOWED
                            : SecurityRuleResult.REJECTED);
                }
            }
        }

        return Mono.just(SecurityRuleResult.UNKNOWN);
    }
}
