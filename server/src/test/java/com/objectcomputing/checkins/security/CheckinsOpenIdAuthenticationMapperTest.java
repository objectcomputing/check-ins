package com.objectcomputing.checkins.security;

import com.nimbusds.jwt.JWTClaimsSet;
import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.fixture.MemberProfileFixture;
import com.objectcomputing.checkins.services.fixture.RoleFixture;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileUtils;
import com.objectcomputing.checkins.services.role.RoleType;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.oauth2.endpoint.token.response.JWTOpenIdClaims;
import io.micronaut.security.oauth2.endpoint.token.response.OpenIdAuthenticationMapper;
import io.micronaut.security.oauth2.endpoint.token.response.OpenIdClaims;
import io.micronaut.security.oauth2.endpoint.token.response.OpenIdTokenResponse;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;


@MicronautTest(environments = "prodtest", transactional = false)
public class CheckinsOpenIdAuthenticationMapperTest extends TestContainersSuite implements MemberProfileFixture, RoleFixture {

    @Inject
    OpenIdAuthenticationMapper openIdAuthenticationMapper;

    @Test
    void testInjection() {
        assertInstanceOf(CheckinsOpenIdAuthenticationMapper.class, openIdAuthenticationMapper);
    }


    @Test
    void testCreateAuthenticationResponse() {
        CheckinsOpenIdAuthenticationMapper checkinsOpenIdAuthenticationMapper = (CheckinsOpenIdAuthenticationMapper) openIdAuthenticationMapper;
        MemberProfile memberProfile = createADefaultMemberProfile();
        List<String> roles = List.of(RoleType.Constants.ADMIN_ROLE, RoleType.Constants.PDL_ROLE);
        for (String role : roles) {
            createAndAssignRole(RoleType.valueOf(role), memberProfile);
        }
        String provider = "Test";
        OpenIdTokenResponse openIdTokenResponse = new OpenIdTokenResponse();
        OpenIdClaims openIdClaims = new JWTOpenIdClaims(
                new JWTClaimsSet.Builder().
                        claim("email", memberProfile.getWorkEmail())
                        .claim("sub", MemberProfileUtils.getFullName(memberProfile))
                        .build());

        StepVerifier.create(checkinsOpenIdAuthenticationMapper.createAuthenticationResponse(provider, openIdTokenResponse, openIdClaims, null))
                .assertNext(auth -> {
                    assertNotNull(auth);
                    Authentication authentication = auth.getAuthentication().orElse(null);
                    assertNotNull(authentication);
                    assertEquals(MemberProfileUtils.getFullName(memberProfile), authentication.getName());
                    assertThat(authentication.getRoles(), CoreMatchers.hasItems(RoleType.Constants.PDL_ROLE, RoleType.Constants.ADMIN_ROLE));
                    assertTrue(roles.containsAll(authentication.getRoles()));
                    assertEquals(roles.size(), authentication.getRoles().size());
                }).verifyComplete();
    }
}
