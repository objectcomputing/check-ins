package com.objectcomputing.checkins.security;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.calendar.Calendar;
import com.google.auth.http.HttpCredentialsAdapter;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileRepository;
import com.objectcomputing.checkins.services.memberprofile.currentuser.CurrentUserServices;
import com.objectcomputing.checkins.services.role.Role;
import com.objectcomputing.checkins.services.role.RoleRepository;
import io.micronaut.context.annotation.Property;
import io.micronaut.context.env.Environment;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.core.async.publisher.Publishers;
import io.micronaut.core.exceptions.ExceptionHandler;
import io.micronaut.security.authentication.AuthenticationResponse;
import io.micronaut.security.authentication.UserDetails;
import io.micronaut.security.oauth2.configuration.OauthClientConfigurationProperties;
import io.micronaut.security.oauth2.endpoint.authorization.state.State;
import io.micronaut.security.oauth2.endpoint.token.response.OauthUserDetailsMapper;
import io.micronaut.security.oauth2.endpoint.token.response.OpenIdClaims;
import io.micronaut.security.oauth2.endpoint.token.response.OpenIdUserDetailsMapper;
import io.micronaut.security.oauth2.endpoint.token.response.TokenResponse;
import io.micronaut.security.token.jwt.generator.claims.JwtClaims;
import io.reactivex.Flowable;
import org.reactivestreams.Publisher;

import javax.inject.Named;
import javax.inject.Singleton;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.*;
import java.util.stream.Collectors;

@Named("google")
@Singleton
public class OauthAuthenticationMapper implements OauthUserDetailsMapper {

    private final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private final NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
    private final String applicationName;
    private final Environment environment;
    private final CurrentUserServices currentUserServices;
    private final RoleRepository roleRepository;
    private Calendar calendar;

    public OauthAuthenticationMapper(@Property(name = "check-ins.application.name") String applicationName,
                                     Environment environment,
                                     RoleRepository roleRepository, CurrentUserServices currentUserServices
                                     ) throws GeneralSecurityException, IOException {
        this.applicationName = applicationName;
        this.environment = environment;
        this.roleRepository = roleRepository;
        this.currentUserServices = currentUserServices;
    }

    public Calendar getCalendar() {
        return this.calendar;
    }

    @Override
    public Publisher<UserDetails> createUserDetails(TokenResponse tokenResponse) {
        Map<String, Object> oauthProperties = new HashMap<>();
        oauthProperties.put(OauthUserDetailsMapper.PROVIDER_KEY, "google");
        oauthProperties.put(OauthUserDetailsMapper.ACCESS_TOKEN_KEY, tokenResponse.getAccessToken());
        oauthProperties.put(OauthUserDetailsMapper.REFRESH_TOKEN_KEY, tokenResponse.getRefreshToken());
        MemberProfile currentUser = currentUserServices.getCurrentUser();
        Set<Role> roles = roleRepository.findUserRoles(currentUser.getId());
        Collection<String> roleCollection = Collections.EMPTY_LIST;
        for (Role role: roles) {
            String roleString = role.getRole();
            roleCollection.add(roleString);
        }

        UserDetails userDetails = new UserDetails(currentUser.getWorkEmail(), roleCollection, oauthProperties);
        return Flowable.just(
               userDetails
        );
    }

    @Override
    public Publisher<AuthenticationResponse> createAuthenticationResponse(TokenResponse tokenResponse, @Nullable State state) {
        String apiScope = environment.getProperty("check-ins.application.google-api.scopes.scopeForDriveApi", String.class).orElse("");
        List<String> scope = Collections.singletonList(apiScope);

        GoogleCredential credential = new GoogleCredential().setAccessToken(tokenResponse.getAccessToken()).setRefreshToken(tokenResponse.getRefreshToken()).createScoped(scope);

        calendar = new Calendar
                .Builder(httpTransport, JSON_FACTORY, credential)
                .setApplicationName(applicationName)
                .build();

        return Flowable.fromPublisher(createUserDetails(tokenResponse));
    }
}