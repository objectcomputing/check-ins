package com.objectcomputing.checkins.auth.authorization.rules;

import com.objectcomputing.geoai.security.authentication.AuthenticatedActor;
import com.objectcomputing.geoai.security.authorization.Authorization;
import com.objectcomputing.geoai.security.authorization.rules.annotation.AnnotationAuthorizationRuleProcessor;
import com.objectcomputing.geoai.security.rules.SecurityRule;
import com.objectcomputing.geoai.security.rules.config.SecurityRuleConfigurationProperties;
import io.micronaut.http.HttpRequest;
import io.micronaut.security.rules.SecurityRuleResult;
import io.micronaut.web.router.MethodBasedRouteMatch;
import io.micronaut.web.router.RouteMatch;
import jakarta.inject.Singleton;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Singleton
public class AnnotationAuthorizationSecurityRule implements SecurityRule {
    private static final int ORDER = SecurityRuleConfigurationProperties.ORDER - 100;

    private static final Logger LOG = LoggerFactory.getLogger(AnnotationAuthorizationSecurityRule.class);

    private final AnnotationAuthorizationRuleProcessor securityRuleProcessor;

    public AnnotationAuthorizationSecurityRule(AnnotationAuthorizationRuleProcessor securityRuleProcessor) {
        this.securityRuleProcessor = securityRuleProcessor;
    }

    @Override
    public Publisher<SecurityRuleResult> check(HttpRequest<?> request, RouteMatch<?> routeMatch, AuthenticatedActor<?,?,?> authenticatedActor) {

        if (routeMatch instanceof MethodBasedRouteMatch) {

            MethodBasedRouteMatch methodRoute = ((MethodBasedRouteMatch) routeMatch);
            if (methodRoute.hasAnnotation(Authorization.class)) {
                if(authenticatedActor == null) {
                    return Mono.just(SecurityRuleResult.REJECTED);
                }
                Optional<String[]> optionalValue = methodRoute.getValue(Authorization.class, String[].class);
                if (optionalValue.isPresent()) {

                    List<String> values = Arrays.asList(optionalValue.get());

                    if (values.contains(io.micronaut.security.rules.SecurityRule.DENY_ALL)) {
                        return Mono.just(SecurityRuleResult.REJECTED);
                    }

                    return compareRoles(values, request, authenticatedActor);
                }
            }
        }
        return Mono.just(SecurityRuleResult.UNKNOWN);
    }

    protected Publisher<SecurityRuleResult> compareRoles(List<String> ruleNames, HttpRequest<?> request, AuthenticatedActor<?,?,?> authenticatedActor) {

        if (securityRuleProcessor.assessRules(ruleNames, request, authenticatedActor)) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("The actor [{}] meet the required roles [{}]. Allowing the request", authenticatedActor.getName(), ruleNames);
            }
            return Mono.just(SecurityRuleResult.ALLOWED);
        } else {
            if (LOG.isDebugEnabled()) {
                LOG.debug("The actor [{}] did not meet the required roles [{}]. Rejecting the request", authenticatedActor.getName(), ruleNames);
            }
            return Mono.just(SecurityRuleResult.REJECTED);
        }
    }

    @Override
    public int getOrder() {
        return ORDER;
    }
}
