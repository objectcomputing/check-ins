package com.objectcomputing.checkins.security;

import com.objectcomputing.checkins.security.permissions.PermissionSecurityRule;
import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.permissions.Permission;
import com.objectcomputing.checkins.services.permissions.RequiredPermission;
import com.objectcomputing.checkins.services.role.Role;
import com.objectcomputing.checkins.services.role.RoleServices;
import com.objectcomputing.checkins.services.role.RoleType;
import com.objectcomputing.checkins.services.role.role_permissions.RolePermissionServices;
import io.micronaut.core.annotation.AnnotationValue;
import io.micronaut.http.HttpAttributes;
import io.micronaut.http.HttpRequest;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.rules.SecurityRuleResult;
import io.micronaut.web.router.MethodBasedRouteMatch;
import jakarta.inject.Inject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.reactivestreams.Publisher;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

class SecurityRuleResultTest extends TestContainersSuite {

    private static final List<String> USER_PERMISSIONS = List.of(
            "CAN_VIEW_FEEDBACK_REQUEST",
            "CAN_CREATE_FEEDBACK_REQUEST",
            "CAN_DELETE_FEEDBACK_REQUEST"
    );
    private static final List<String> USER_ROLES = List.of("ADMIN");

    @Inject
    PermissionSecurityRule permissionSecurityRule;

    @Inject
    RolePermissionServices rolePermissionServices;

    @Inject
    RoleServices roleServices;

    @Mock
    private MethodBasedRouteMatch<?, ?> mockMethodBasedRouteMatch;

    @Mock
    private AnnotationValue<RequiredPermission> mockRequiredPermissionAnnotation;

    private AutoCloseable mockFinalizer;

    @BeforeEach
    void resetMocks() {
        Role role = roleServices.save(new Role(RoleType.ADMIN.name(), "Admin Role"));
        rolePermissionServices.save(role.getId(), Permission.CAN_VIEW_FEEDBACK_REQUEST);
        mockFinalizer = openMocks(this);
    }

    @AfterEach
    void afterEach() throws Exception {
        reset(mockMethodBasedRouteMatch, mockRequiredPermissionAnnotation);
        mockFinalizer.close();
    }

    @Test
    void allowSecurityRuleResultTest() {
        final HttpRequest<?> request = HttpRequest.POST("/", null)
                .basicAuth("test.email.address", RoleType.Constants.ADMIN_ROLE)
                .setAttribute(HttpAttributes.ROUTE_MATCH, mockMethodBasedRouteMatch);

        Map<String, Object> attributes = Map.of(
                "permissions", USER_PERMISSIONS,
                "roles", USER_ROLES,
                "email", "test.email.address"
        );

        when(mockMethodBasedRouteMatch.findAnnotation(RequiredPermission.class)).thenReturn(Optional.of(mockRequiredPermissionAnnotation));
        when(mockRequiredPermissionAnnotation.stringValue()).thenReturn(Optional.of("CAN_VIEW_FEEDBACK_REQUEST"));

        Authentication auth = Authentication.build("test.email.address", attributes);

        Publisher<SecurityRuleResult> result = permissionSecurityRule.check(request, auth);

        assertNotNull(result);
        StepVerifier.create(result)
                .expectNext(SecurityRuleResult.ALLOWED)
                .expectComplete()
                .verify();
    }


    @Test
    void rejectSecurityRuleResultTest() {
        final HttpRequest<?> request = HttpRequest.POST("/", null)
                .basicAuth("test.email.address", RoleType.Constants.ADMIN_ROLE)
                .setAttribute(HttpAttributes.ROUTE_MATCH, mockMethodBasedRouteMatch);

        Map<String, Object> attributes = Map.of(
                "permissions", USER_PERMISSIONS,
                "roles", USER_ROLES,
                "email", "test.email.address"
        );

        when(mockMethodBasedRouteMatch.findAnnotation(RequiredPermission.class)).thenReturn(Optional.of(mockRequiredPermissionAnnotation));
        when(mockRequiredPermissionAnnotation.stringValue()).thenReturn(Optional.of("CAN_CREATE_ORGANIZATION_MEMBERS"));

        Authentication auth = Authentication.build("test.email.address", attributes);
        Publisher<SecurityRuleResult> result = permissionSecurityRule.check(request, auth);

        assertNotNull(result);
        StepVerifier.create(result)
                .expectNext(SecurityRuleResult.REJECTED)
                .expectComplete()
                .verify();
    }

    @Test
    void unknownSecurityRuleResultIfRouteMatchIsNotAnInstance() {
        final HttpRequest<?> request = HttpRequest.POST("/", null)
                .basicAuth("test.email.address", RoleType.Constants.ADMIN_ROLE);

        Publisher<SecurityRuleResult> result = permissionSecurityRule.check(request, null);

        assertNotNull(result);
        StepVerifier.create(result)
                .expectNext(SecurityRuleResult.UNKNOWN)
                .expectComplete()
                .verify();
    }

    @Test
    void unknownSecurityRuleResultIfMethodBasedRouteMatchFailsToHaveAnnotation() {
        final HttpRequest<?> request = HttpRequest.POST("/", null)
                .basicAuth("test.email.address", RoleType.Constants.ADMIN_ROLE)
                .setAttribute(HttpAttributes.ROUTE_MATCH, mockMethodBasedRouteMatch);

        when(mockMethodBasedRouteMatch.findAnnotation(RequiredPermission.class)).thenReturn(Optional.empty());

        Publisher<SecurityRuleResult> result = permissionSecurityRule.check(request, null);

        assertNotNull(result);
        StepVerifier.create(result)
                .expectNext(SecurityRuleResult.UNKNOWN)
                .expectComplete()
                .verify();
    }

    @Test
    void unknownSecurityRuleResultIfRequiredPermissionIsNull() {

        final HttpRequest<?> request = HttpRequest.POST("/", null)
                .basicAuth("test.email.address", RoleType.Constants.ADMIN_ROLE)
                .setAttribute(HttpAttributes.ROUTE_MATCH, mockMethodBasedRouteMatch);

        when(mockMethodBasedRouteMatch.findAnnotation(RequiredPermission.class)).thenReturn(Optional.of(mockRequiredPermissionAnnotation));
        when(mockRequiredPermissionAnnotation.stringValue()).thenReturn(Optional.empty());

        Publisher<SecurityRuleResult> result = permissionSecurityRule.check(request, null);

        assertNotNull(result);
        StepVerifier.create(result)
                .expectNext(SecurityRuleResult.UNKNOWN)
                .expectComplete()
                .verify();
    }

    @Test
    void unknownSecurityRuleResultIfClaimsIsNull() {

        final HttpRequest<?> request = HttpRequest.POST("/", null)
                .basicAuth("test.email.address", RoleType.Constants.ADMIN_ROLE)
                .setAttribute(HttpAttributes.ROUTE_MATCH, mockMethodBasedRouteMatch);

        when(mockMethodBasedRouteMatch.findAnnotation(RequiredPermission.class)).thenReturn(Optional.of(mockRequiredPermissionAnnotation));
        when(mockRequiredPermissionAnnotation.stringValue()).thenReturn(Optional.of("CAN_CREATE_ORGANIZATION_MEMBERS"));

        Publisher<SecurityRuleResult> result = permissionSecurityRule.check(request, null);

        assertNotNull(result);
        StepVerifier.create(result)
                .expectNext(SecurityRuleResult.UNKNOWN)
                .expectComplete()
                .verify();
    }

    @Test
    void unknownSecurityRuleResultIfClaimsDoesNotContainPermissions() {

        final HttpRequest<?> request = HttpRequest.POST("/", null)
                .basicAuth("test.email.address", RoleType.Constants.ADMIN_ROLE)
                .setAttribute(HttpAttributes.ROUTE_MATCH, mockMethodBasedRouteMatch);

        when(mockMethodBasedRouteMatch.findAnnotation(RequiredPermission.class)).thenReturn(Optional.of(mockRequiredPermissionAnnotation));
        when(mockRequiredPermissionAnnotation.stringValue()).thenReturn(Optional.of("CAN_CREATE_ORGANIZATION_MEMBERS"));

        Map<String, Object> attributes = Map.of("email", "test.email.address");

        Authentication auth = Authentication.build("test.email.address", attributes);

        Publisher<SecurityRuleResult> result = permissionSecurityRule.check(request, auth);

        assertNotNull(result);
        StepVerifier.create(result)
                .expectNext(SecurityRuleResult.UNKNOWN)
                .expectComplete()
                .verify();
    }

    @Test
    void unknownWhenRequestIsNull() {
        Map<String, Object> attributes = Map.of(
                "permissions", USER_PERMISSIONS,
                "roles", USER_ROLES,
                "email", "test.email.address"
        );

        when(mockMethodBasedRouteMatch.findAnnotation(RequiredPermission.class)).thenReturn(Optional.of(mockRequiredPermissionAnnotation));
        when(mockRequiredPermissionAnnotation.stringValue()).thenReturn(Optional.of("CAN_VIEW_FEEDBACK_REQUEST"));

        Authentication auth = Authentication.build("test.email.address", attributes);

        Publisher<SecurityRuleResult> result = permissionSecurityRule.check(null, auth);

        assertNotNull(result);
        StepVerifier.create(result)
                .expectNext(SecurityRuleResult.UNKNOWN)
                .expectComplete()
                .verify();
    }
}
