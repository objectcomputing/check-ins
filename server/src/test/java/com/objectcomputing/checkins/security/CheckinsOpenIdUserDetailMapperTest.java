package com.objectcomputing.checkins.security;

import com.nimbusds.jwt.JWTClaimsSet;
import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.fixture.MemberProfileFixture;
import com.objectcomputing.checkins.services.fixture.RoleFixture;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.role.RoleType;
import io.micronaut.security.authentication.AuthenticationResponse;
import io.micronaut.security.authentication.UserDetails;
import io.micronaut.security.oauth2.endpoint.token.response.JWTOpenIdClaims;
import io.micronaut.security.oauth2.endpoint.token.response.OpenIdClaims;
import io.micronaut.security.oauth2.endpoint.token.response.OpenIdTokenResponse;
import io.micronaut.security.oauth2.endpoint.token.response.OpenIdUserDetailsMapper;
import io.micronaut.test.annotation.MicronautTest;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@MicronautTest(environments = "prodtest", transactional = false)
public class CheckinsOpenIdUserDetailMapperTest extends TestContainersSuite implements MemberProfileFixture, RoleFixture {

    @Inject
    OpenIdUserDetailsMapper openIdUserDetailsMapper;

    @Test
    void testInjection() {
        assertTrue(openIdUserDetailsMapper instanceof CheckinsOpenIdUserDetailMapper);
    }


    @Test
    void testCreateAuthenticationResponse() {
        CheckinsOpenIdUserDetailMapper checkinsOpenIdUserDetailMapper = (CheckinsOpenIdUserDetailMapper) openIdUserDetailsMapper;
        MemberProfile memberProfile = createADefaultMemberProfile();
        List<String> roles = List.of(RoleType.Constants.ADMIN_ROLE, RoleType.Constants.PDL_ROLE);
        for (String role : roles) {
            createDefaultRole(RoleType.valueOf(role), memberProfile);
        }
        String provider = "Test";
        OpenIdTokenResponse openIdTokenResponse = new OpenIdTokenResponse();
        OpenIdClaims openIdClaims = new JWTOpenIdClaims(
                new JWTClaimsSet.Builder().
                        claim("email", memberProfile.getWorkEmail())
                        .claim("sub", memberProfile.getName())
                        .build());
        AuthenticationResponse auth = checkinsOpenIdUserDetailMapper.createAuthenticationResponse(provider,
                openIdTokenResponse, openIdClaims, null);

        assertNotNull(auth);
        UserDetails userDetails = auth.getUserDetails().orElse(null);
        assertNotNull(userDetails);
        assertEquals(memberProfile.getName(), userDetails.getUsername());
        assertEquals(roles, userDetails.getRoles());
    }
}
