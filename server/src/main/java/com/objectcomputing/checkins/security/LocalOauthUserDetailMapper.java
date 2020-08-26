package com.objectcomputing.checkins.security;

import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.memberprofile.currentuser.CurrentUserServices;
import com.objectcomputing.checkins.services.role.Role;
import com.objectcomputing.checkins.services.role.RoleRepository;
import com.objectcomputing.checkins.services.role.RoleType;
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
@Requires(env = {"local"})
public class LocalOauthUserDetailMapper implements OauthUserDetailsMapper {
    @Inject
    private UsersStore usersStore;

    @Inject
    private JWTClaimsSetGenerator claimsGenerator;

    @Inject
    private SignatureGeneratorConfiguration signatureGeneratorConfiguration;

    @Inject
    private CurrentUserServices currentUserServices;

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

        MemberProfile memberProfile = currentUserServices.findOrSaveUser(email, email);
        String name = memberProfile.getName();

        List<String> roles;
        String role;
        if (fakeAccessToken.has("role") && StringUtils.isNotEmpty(role = fakeAccessToken.getString("role"))) {
            roles = usersStore.getUserRole(role);
            List<String> currentRoles = roleRepository.findByMemberid(memberProfile.getUuid()).stream().map(r -> r.getRole().toString()).collect(Collectors.toList());
            currentRoles.removeAll(roles);

            // Create the roles if they don't already exist, delete roles not asked for
            for(String curRole : currentRoles) {
                roleRepository.deleteByRoleAndMemberid(RoleType.valueOf(curRole), memberProfile.getUuid());
            }

            for (String curRole : roles) {
                try {
                    RoleType roleType = RoleType.valueOf(curRole);
                    if (roleRepository.findByRoleAndMemberid(roleType, memberProfile.getUuid()).isEmpty()) {
                        roleRepository.save(new Role(roleType, memberProfile.getUuid()));
                    }
                } catch (IllegalArgumentException ignored) {
                }
            }
        } else {
            roles = roleRepository.findByMemberid(memberProfile.getUuid()).stream().map((r) -> r.getRole().toString())
                    .collect(Collectors.toList());
        }

        UserDetails details = new UserDetails(email, roles);
        Optional<String> idToken = new JwtTokenGenerator(signatureGeneratorConfiguration, null, claimsGenerator)
                .generateToken(Map.of("exp", System.currentTimeMillis() / 1000 + 60 * 60,
                        "sub", email,
                        "roles", roles,
                        "email", email,
                        "name", name,
                        "picture", "https://upload.wikimedia.org/wikipedia/commons/7/74/SNL_MrBill_Doll.jpg"));

        if (idToken.isPresent()) {
            details.setAttributes(Map.of(OPENID_TOKEN_KEY, idToken.get()));
            return Publishers.just(details);
        }
        return Publishers.just(new AuthenticationFailed());
    }
}
