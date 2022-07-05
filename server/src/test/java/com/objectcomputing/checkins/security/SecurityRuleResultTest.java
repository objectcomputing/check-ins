package com.objectcomputing.checkins.security;

import com.objectcomputing.checkins.security.permissions.PermissionSecurityRule;
import com.objectcomputing.checkins.services.permissions.RequiredPermission;
import com.objectcomputing.checkins.services.role.RoleType;
import io.micronaut.core.annotation.AnnotationValue;
import io.micronaut.http.HttpRequest;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.rules.SecurityRuleResult;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.web.router.MethodBasedRouteMatch;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import jakarta.inject.Inject;
import org.reactivestreams.Publisher;
import reactor.test.StepVerifier;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@MicronautTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SecurityRuleResultTest {

    List<String> userPermissions = List.of(
            "CAN_VIEW_FEEDBACK",
            "CAN_CREATE_FEEDBACK",
            "CAN_DELETE_FEEDBACK"
    );

    @Inject
    PermissionSecurityRule permissionSecurityRule;

    @Mock
    private MethodBasedRouteMatch mockMethodBasedRouteMatch;

    @Mock
    private AnnotationValue<RequiredPermission> mockRequiredPermissionAnnotation;

    @BeforeAll
    void initMocksAndInitializeFile() {
        MockitoAnnotations.openMocks(this);
    }

    @BeforeEach
    void resetMocks() {
        Mockito.reset(mockMethodBasedRouteMatch);
        Mockito.reset(mockRequiredPermissionAnnotation);
    }

    @Test
    public void allowSecurityRuleResultTest() {

        final HttpRequest<?> request = HttpRequest.POST("/", null)
                .basicAuth("test.email.address", RoleType.Constants.ADMIN_ROLE);

        Map<String, Object> attributes = new HashMap<>();
        attributes.put("permissions", userPermissions);
        attributes.put("email", "test.email.address");

        when(mockMethodBasedRouteMatch.hasAnnotation(RequiredPermission.class)).thenReturn(true);
        when(mockMethodBasedRouteMatch.getAnnotation(RequiredPermission.class)).thenReturn(mockRequiredPermissionAnnotation);
        when(mockRequiredPermissionAnnotation.stringValue("value")).thenReturn(Optional.of("CAN_VIEW_FEEDBACK"));

        Authentication auth = Authentication.build("test.email.address", attributes);

        Publisher<SecurityRuleResult> result = permissionSecurityRule.check(request, mockMethodBasedRouteMatch, auth);

        assertNotNull(result);
        StepVerifier.create(result)
                        .expectNext(SecurityRuleResult.ALLOWED)
                        .expectComplete()
                        .verify();

    }

    @Test
    public void rejectSecurityRuleResultTest() {

        final HttpRequest<?> request = HttpRequest.POST("/", null)
                .basicAuth("test.email.address", RoleType.Constants.ADMIN_ROLE);

        Map<String, Object> attributes = new HashMap<>();
        attributes.put("permissions", userPermissions);
        attributes.put("email", "test.email.address");

        when(mockMethodBasedRouteMatch.hasAnnotation(RequiredPermission.class)).thenReturn(true);
        when(mockMethodBasedRouteMatch.getAnnotation(RequiredPermission.class)).thenReturn(mockRequiredPermissionAnnotation);
        when(mockRequiredPermissionAnnotation.stringValue("value")).thenReturn(Optional.of("CAN_CREATE_ORGANIZATION_MEMBERS"));

        Authentication auth = Authentication.build("test.email.address", attributes);
        Publisher<SecurityRuleResult> result = permissionSecurityRule.check(request, mockMethodBasedRouteMatch, auth);

        assertNotNull(result);
        StepVerifier.create(result)
                .expectNext(SecurityRuleResult.REJECTED)
                .expectComplete()
                .verify();
    }

    @Test
    public void unknownSecurityRuleResultIfRouteMatchIsNotAnInstance() {

        final HttpRequest<?> request = HttpRequest.POST("/", null)
                .basicAuth("test.email.address", RoleType.Constants.ADMIN_ROLE);

        Publisher<SecurityRuleResult> result = permissionSecurityRule.check(request, null, null);

        assertNotNull(result);
        StepVerifier.create(result)
                        .expectNext(SecurityRuleResult.UNKNOWN)
                        .expectComplete()
                        .verify();
    }

    @Test
    public void unknownSecurityRuleResultIfMethodBasedRouteMatchFailsToHaveAnnotation() {

        final HttpRequest<?> request = HttpRequest.POST("/", null)
                .basicAuth("test.email.address", RoleType.Constants.ADMIN_ROLE);

        when(mockMethodBasedRouteMatch.hasAnnotation(RequiredPermission.class)).thenReturn(false);

        Publisher<SecurityRuleResult> result = permissionSecurityRule.check(request, mockMethodBasedRouteMatch, null);

        assertNotNull(result);
        StepVerifier.create(result)
                .expectNext(SecurityRuleResult.UNKNOWN)
                .expectComplete()
                .verify();
    }

    @Test
    public void unknownSecurityRuleResultIfRequiredPermissionIsNull() {

        final HttpRequest<?> request = HttpRequest.POST("/", null)
                .basicAuth("test.email.address", RoleType.Constants.ADMIN_ROLE);

        when(mockMethodBasedRouteMatch.hasAnnotation(RequiredPermission.class)).thenReturn(true);
        when(mockMethodBasedRouteMatch.getAnnotation(RequiredPermission.class)).thenReturn(null);

        Publisher<SecurityRuleResult> result = permissionSecurityRule.check(request, mockMethodBasedRouteMatch, null);

        assertNotNull(result);
        StepVerifier.create(result)
                .expectNext(SecurityRuleResult.UNKNOWN)
                .expectComplete()
                .verify();
    }

    @Test
    public void unknownSecurityRuleResultIfClaimsIsNull() {

        final HttpRequest<?> request = HttpRequest.POST("/", null)
                .basicAuth("test.email.address", RoleType.Constants.ADMIN_ROLE);

        when(mockMethodBasedRouteMatch.hasAnnotation(RequiredPermission.class)).thenReturn(true);
        when(mockMethodBasedRouteMatch.getAnnotation(RequiredPermission.class)).thenReturn(mockRequiredPermissionAnnotation);
        when(mockRequiredPermissionAnnotation.stringValue("value")).thenReturn(Optional.of("CAN_CREATE_ORGANIZATION_MEMBERS"));

        Publisher<SecurityRuleResult> result = permissionSecurityRule.check(request, mockMethodBasedRouteMatch, null);

        assertNotNull(result);
        StepVerifier.create(result)
                .expectNext(SecurityRuleResult.UNKNOWN)
                .expectComplete()
                .verify();
    }

    @Test
    public void unknownSecurityRuleResultIfClaimsDoesNotContainPermissions() {

        final HttpRequest<?> request = HttpRequest.POST("/", null)
                .basicAuth("test.email.address", RoleType.Constants.ADMIN_ROLE);

        when(mockMethodBasedRouteMatch.hasAnnotation(RequiredPermission.class)).thenReturn(true);
        when(mockMethodBasedRouteMatch.getAnnotation(RequiredPermission.class)).thenReturn(mockRequiredPermissionAnnotation);
        when(mockRequiredPermissionAnnotation.stringValue("value")).thenReturn(Optional.of("CAN_CREATE_ORGANIZATION_MEMBERS"));

        Map<String, Object> attributes = new HashMap<>();
        attributes.put("email", "test.email.address");

        Authentication auth = Authentication.build("test.email.address", attributes);

        Publisher<SecurityRuleResult> result = permissionSecurityRule.check(request, mockMethodBasedRouteMatch, auth);

        assertNotNull(result);
        StepVerifier.create(result)
                .expectNext(SecurityRuleResult.UNKNOWN)
                .expectComplete()
                .verify();
    }
}
