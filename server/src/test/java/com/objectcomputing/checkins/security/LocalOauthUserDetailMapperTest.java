package com.objectcomputing.checkins.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.fixture.MemberProfileFixture;
import com.objectcomputing.checkins.services.fixture.RoleFixture;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.role.RoleType;
import io.micronaut.security.authentication.AuthenticationResponse;
import io.micronaut.security.authentication.UserDetails;
import io.micronaut.security.oauth2.endpoint.authorization.state.DefaultState;
import io.micronaut.security.oauth2.endpoint.token.response.OauthUserDetailsMapper;
import io.micronaut.security.oauth2.endpoint.token.response.TokenResponse;
import io.micronaut.test.annotation.MicronautTest;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


@MicronautTest(environments = "local", transactional = false)
public class LocalOauthUserDetailMapperTest extends TestContainersSuite implements MemberProfileFixture, RoleFixture {

    @Inject
    OauthUserDetailsMapper oauthUserDetailsMapper;

    @Test
    void testInjection() {
        assertTrue(oauthUserDetailsMapper instanceof LocalOauthUserDetailMapper);
    }

    @Test
    void testCreateAuthenticationResponse() throws JsonProcessingException {
        TokenResponse tr = new TokenResponse();
        tr.setAccessToken(new ObjectMapper().writeValueAsString(Map.of("email", "email", "role", "ADMIN")));
        Publisher<AuthenticationResponse> pub = oauthUserDetailsMapper.createAuthenticationResponse(tr, new DefaultState());
        AtomicBoolean wasCalled = new AtomicBoolean(false);
        Subscriber<AuthenticationResponse> happySub = new Subscriber<>() {
            @Override
            public void onSubscribe(Subscription s) {
                s.request(1);
            }

            @Override
            public void onNext(AuthenticationResponse authenticationResponse) {
                Optional<UserDetails> details = authenticationResponse.getUserDetails();
                assertTrue(details.isPresent());
                UserDetails userDetails = details.get();
                assertEquals(userDetails.getUsername(), "email");
                assertEquals(userDetails.getRoles(), List.of("ADMIN"));
                wasCalled.set(true);
            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onComplete() {
                assertTrue(wasCalled.get());
            }
        };
        pub.subscribe(happySub);
    }

    @Test
    void testCreateAuthenticationResponseNoRole() throws JsonProcessingException {
        MemberProfile memberProfile = createADefaultMemberProfile();
        createDefaultRole(RoleType.ADMIN, memberProfile);
        createDefaultRole(RoleType.PDL, memberProfile);
        TokenResponse tr = new TokenResponse();
        tr.setAccessToken(new ObjectMapper().writeValueAsString(Map.of("email", memberProfile.getWorkEmail())));
        Publisher<AuthenticationResponse> pub = oauthUserDetailsMapper.createAuthenticationResponse(tr, new DefaultState());
        AtomicBoolean wasCalled = new AtomicBoolean(false);
        Subscriber<AuthenticationResponse> happySub = new Subscriber<>() {
            @Override
            public void onSubscribe(Subscription s) {
                s.request(1);
            }

            @Override
            public void onNext(AuthenticationResponse authenticationResponse) {
                Optional<UserDetails> details = authenticationResponse.getUserDetails();
                assertTrue(details.isPresent());
                UserDetails userDetails = details.get();
                assertEquals(userDetails.getUsername(), memberProfile.getWorkEmail());
                assertEquals(userDetails.getRoles(), List.of(RoleType.Constants.ADMIN_ROLE, RoleType.Constants.PDL_ROLE));
                wasCalled.set(true);
            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onComplete() {
                assertTrue(wasCalled.get());
            }
        };
        pub.subscribe(happySub);
    }

    @Test
    void testCreateDetails() throws JsonProcessingException {
        TokenResponse tr = new TokenResponse();
        tr.setAccessToken(new ObjectMapper().writeValueAsString(Map.of("email", "email", "role", "ADMIN")));
        Publisher<UserDetails> pub = oauthUserDetailsMapper.createUserDetails(tr);
        AtomicBoolean wasCalled = new AtomicBoolean(false);
        Subscriber<UserDetails> happySub = new Subscriber<>() {
            @Override
            public void onSubscribe(Subscription s) {
                s.request(1);
            }

            @Override
            public void onNext(UserDetails userDetails) {
                assertEquals(userDetails.getUsername(), "email");
                assertEquals(userDetails.getRoles(), List.of("ADMIN"));
                wasCalled.set(true);
            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onComplete() {
                assertTrue(wasCalled.get());
            }
        };
        pub.subscribe(happySub);
    }

}
