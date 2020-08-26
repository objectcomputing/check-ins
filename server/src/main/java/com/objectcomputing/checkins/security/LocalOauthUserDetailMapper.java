package com.objectcomputing.checkins.security;

import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileRepository;
import com.objectcomputing.checkins.services.role.RoleRepository;
import io.micronaut.context.annotation.Requires;
import io.micronaut.core.async.publisher.Publishers;
import io.micronaut.core.util.StringUtils;
import io.micronaut.security.authentication.AuthenticationFailed;
import io.micronaut.security.authentication.AuthenticationResponse;
import io.micronaut.security.authentication.UserDetails;
import io.micronaut.security.oauth2.endpoint.authorization.state.State;
import io.micronaut.security.oauth2.endpoint.token.response.OauthUserDetailsMapper;
import io.micronaut.security.oauth2.endpoint.token.response.TokenResponse;
import io.micronaut.security.token.jwt.generator.JwtTokenGenerator;
import io.micronaut.security.token.jwt.generator.claims.JWTClaimsSetGenerator;
import io.micronaut.security.token.jwt.signature.SignatureGeneratorConfiguration;
import org.json.JSONObject;
import org.reactivestreams.Publisher;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static io.micronaut.security.oauth2.endpoint.token.response.OpenIdUserDetailsMapper.OPENID_TOKEN_KEY;

@Singleton
@Named("oauth")
@Requires(env = "local")
public class LocalOauthUserDetailMapper implements OauthUserDetailsMapper {
    @Inject
    private UsersStore usersStore;

    @Inject
    private JWTClaimsSetGenerator claimsGenerator;

    @Inject
    private SignatureGeneratorConfiguration signatureGeneratorConfiguration;

    @Inject
    private MemberProfileRepository memberProfileRepository;

    @Inject
    private RoleRepository roleRepository;

    @Override
    public Publisher<UserDetails> createUserDetails(TokenResponse tokenResponse) {
        String fakeAccessTokenAsJson = tokenResponse.getAccessToken();
        JSONObject fakeAccessToken = new JSONObject(fakeAccessTokenAsJson);
        String email = fakeAccessToken.getString("email");
        String role = fakeAccessToken.getString("role");
        UserDetails details = new UserDetails(email, usersStore.getUserRole(role));
        return Publishers.just(details);
    }

    @Override
    public Publisher<AuthenticationResponse> createAuthenticationResponse(TokenResponse tokenResponse, @Nullable State state) {
        String fakeAccessTokenAsJson = tokenResponse.getAccessToken();
        JSONObject fakeAccessToken = new JSONObject(fakeAccessTokenAsJson);
        String email = fakeAccessToken.getString("email");
        List<String> roles;
        String role;
        if (fakeAccessToken.has("role") && StringUtils.isNotEmpty(role = fakeAccessToken.getString("role"))) {
            roles = usersStore.getUserRole(role);
        } else {
            MemberProfile memberProfile = memberProfileRepository.findByWorkEmail(email).orElse(null);
            if (memberProfile == null || memberProfile.getUuid() == null) {
                return Publishers.just(new AuthenticationFailed(String.format("Email %s doesn't exist in DB", email)));
            }
            roles = roleRepository.findByMemberid(memberProfile.getUuid()).stream().map((r) -> r.getRole().toString())
                    .collect(Collectors.toList());
        }

        UserDetails details = new UserDetails(email, roles);
        Optional<String> idToken = new JwtTokenGenerator(signatureGeneratorConfiguration, null, claimsGenerator)
                .generateToken(Map.of("exp", System.currentTimeMillis() / 1000 + 60 * 60,
                        "sub", email,
                        "roles", roles));
        if (idToken.isPresent()) {
            details.setAttributes(Map.of(OPENID_TOKEN_KEY, idToken.get()));
            return Publishers.just(details);
        }
        return Publishers.just(new AuthenticationFailed());
    }
}
