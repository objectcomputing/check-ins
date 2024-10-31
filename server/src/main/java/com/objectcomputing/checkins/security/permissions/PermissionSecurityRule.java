package com.objectcomputing.checkins.security.permissions;

import com.objectcomputing.checkins.services.permissions.Permission;
import com.objectcomputing.checkins.services.permissions.RequiredPermission;
import com.objectcomputing.checkins.services.role.role_permissions.RolePermission;
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

import java.util.Map;

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
        if (request == null) {
            return Mono.just(SecurityRuleResult.UNKNOWN);
        }
        return request.getAttribute(HttpAttributes.ROUTE_MATCH, RouteMatch.class)
                .map(routeMatch -> routeMatch instanceof MethodBasedRouteMatch<?,?> ? (MethodBasedRouteMatch<?,?>) routeMatch : null)
                .flatMap(routeMatch -> routeMatch.findAnnotation(RequiredPermission.class).flatMap(AnnotationValue::stringValue))
                .map(Permission::valueOf)
                .map(requiredPermission -> {
                    Map<String, Object> claims = authentication != null ? authentication.getAttributes() : null;
                    if (claims != null && claims.containsKey("roles")) {
                        JSONArray jsonArray = new JSONArray(claims.get("roles").toString());
                        boolean requiredPermissionFoundInRoles = jsonArray.toList().stream()
                                .map(Object::toString)
                                .flatMap(role -> rolePermissionServices.findByRole(role).stream())
                                .map(RolePermission::getPermission)
                                .anyMatch(p -> p == requiredPermission);
                        return requiredPermissionFoundInRoles ? SecurityRuleResult.ALLOWED : SecurityRuleResult.REJECTED;
                    } else {
                        return SecurityRuleResult.UNKNOWN;
                    }
                })
                .map(Mono::just)
                .orElseGet(() -> Mono.just(SecurityRuleResult.UNKNOWN));
    }
}
