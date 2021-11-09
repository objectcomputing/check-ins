package com.objectcomputing.checkins.security.permissions;

import com.objectcomputing.checkins.services.permissions.RequiredPermission;
import io.micronaut.core.annotation.AnnotationValue;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpRequest;
import io.micronaut.security.rules.SecuredAnnotationRule;
import io.micronaut.security.rules.SecurityRule;
import io.micronaut.security.rules.SecurityRuleResult;
import io.micronaut.web.router.MethodBasedRouteMatch;
import io.micronaut.web.router.RouteMatch;

import javax.inject.Singleton;
import java.util.Map;
import java.util.Optional;

@Singleton
public class PermissionSecurityRule implements SecurityRule {

    public static final Integer ORDER = SecuredAnnotationRule.ORDER - 100;

    public int getOrder() {
        return ORDER;
    }

    @Override
    public SecurityRuleResult check(HttpRequest<?> request, @Nullable RouteMatch<?> routeMatch, @Nullable  Map<String, Object> claims) {
        if (routeMatch instanceof MethodBasedRouteMatch) {
            MethodBasedRouteMatch methodBasedRouteMatch = (MethodBasedRouteMatch) routeMatch;

            if (methodBasedRouteMatch.hasAnnotation(RequiredPermission.class)) {
                AnnotationValue<RequiredPermission> requiredPermissionAnnotation = methodBasedRouteMatch.getAnnotation(RequiredPermission.class);
                Optional<String> optionalPermission = requiredPermissionAnnotation != null ? requiredPermissionAnnotation.stringValue("value") : Optional.empty();

                if (optionalPermission.isPresent() && claims != null && claims.containsKey("permissions")) {
                    final String requiredPermission = optionalPermission.get();
                    final String userPermissions = claims.get("permissions").toString();

                   return userPermissions.contains(requiredPermission)
                            ? SecurityRuleResult.ALLOWED
                            : SecurityRuleResult.REJECTED;
                }
            }
        }

        return SecurityRuleResult.UNKNOWN;
    }
}
