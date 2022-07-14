package com.objectcomputing.checkins.security.permissions;

import com.objectcomputing.checkins.services.permissions.RequiredPermission;
import io.micronaut.core.annotation.AnnotationValue;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpRequest;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.rules.SecuredAnnotationRule;
import io.micronaut.security.rules.SecurityRule;
import io.micronaut.security.rules.SecurityRuleResult;
import io.micronaut.web.router.MethodBasedRouteMatch;
import io.micronaut.web.router.RouteMatch;

import jakarta.inject.Singleton;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Optional;

@Singleton
public class PermissionSecurityRule implements SecurityRule {

    public static final Integer ORDER = SecuredAnnotationRule.ORDER - 100;

    public int getOrder() {
        return ORDER;
    }

    @Override
    public Publisher<SecurityRuleResult> check(HttpRequest<?> request, @Nullable RouteMatch<?> routeMatch, @Nullable Authentication authentication) {

        if (routeMatch instanceof MethodBasedRouteMatch) {
            MethodBasedRouteMatch methodBasedRouteMatch = (MethodBasedRouteMatch) routeMatch;

            if (methodBasedRouteMatch.hasAnnotation(RequiredPermission.class)) {
                AnnotationValue<RequiredPermission> requiredPermissionAnnotation = methodBasedRouteMatch.getAnnotation(RequiredPermission.class);
                Optional<String> optionalPermission = requiredPermissionAnnotation != null ? requiredPermissionAnnotation.stringValue("value") : Optional.empty();

                Map<String, Object> claims = authentication != null ? authentication.getAttributes() : null;

                if (optionalPermission.isPresent() && claims != null && claims.containsKey("permissions")) {
                    final String requiredPermission = optionalPermission.get();
                    final String userPermissions = claims.get("permissions").toString();

                   return Mono.just(userPermissions.contains(requiredPermission)
                            ? SecurityRuleResult.ALLOWED
                            : SecurityRuleResult.REJECTED);
                }
            }
        }

        return Mono.just(SecurityRuleResult.UNKNOWN);
    }


}
