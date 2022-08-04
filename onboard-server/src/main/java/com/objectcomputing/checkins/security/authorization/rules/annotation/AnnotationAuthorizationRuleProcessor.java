package com.objectcomputing.checkins.security.authorization.rules.annotation;

import com.objectcomputing.geoai.security.authentication.AuthenticatedActor;
import io.micronaut.http.HttpRequest;
import jakarta.inject.Singleton;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Singleton
public class AnnotationAuthorizationRuleProcessor {

    private final Map<String, AnnotationAuthorizationRule> annotationSecurityRules;

    public AnnotationAuthorizationRuleProcessor(Collection<AnnotationAuthorizationRule> annotationSecurityRules) {
        this.annotationSecurityRules =
                annotationSecurityRules.stream().collect(Collectors.toMap(r -> r.getName().toLowerCase(), r -> r));
    }

    public boolean assessRules(List<String> ruleNames, HttpRequest<?> request, AuthenticatedActor<?,?,?> authenticatedActor) {
        if(authenticatedActor == null) {
            // TODO 4/21/2022 LOG
            System.out.println("No Authenticated Actor Present");
            return false;
        }

        for(String ruleName : ruleNames) {
            AnnotationAuthorizationRule rule = annotationSecurityRules.get(ruleName.toLowerCase(Locale.ROOT));
            if(null == rule) {
                // TODO 1/6/2022 LOG
                System.out.println("No rule found!");
                return false;
            }

            if(!rule.check(request, authenticatedActor)) {
                // TODO 1/6/2022 LOG
                System.out.println("Rule Found, check was false!");
                return false;
            }
        }
        return true;
    }
}
