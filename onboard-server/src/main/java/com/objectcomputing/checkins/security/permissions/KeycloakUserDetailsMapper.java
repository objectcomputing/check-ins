package com.objectcomputing.checkins.security.permissions;

import io.micronaut.context.annotation.Property;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.security.authentication.AuthenticationResponse;
import io.micronaut.security.oauth2.endpoint.authorization.state.State;
import io.micronaut.security.oauth2.endpoint.token.response.*;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

@Named("keycloak")
@Singleton
public class KeycloakUserDetailsMapper implements OpenIdAuthenticationMapper {

	private static final Logger LOG = LoggerFactory.getLogger(KeycloakUserDetailsMapper.class);

	private final String clientId;
	private final String clientSecret;
	private final HttpClient client;

	public KeycloakUserDetailsMapper(
			@Property(name = "micronaut.security.oauth2.clients.keycloak.client-id") String clientId,
			@Property(name = "micronaut.security.oauth2.clients.keycloak.client-secret") String clientSecret,
			@Client("http://localhost:8880") HttpClient client) {

		this.clientId = clientId;
		this.clientSecret = clientSecret;
		this.client = client;
	}

	@Override
	public AuthenticationResponse createAuthenticationResponse(String providerName, OpenIdTokenResponse tokenResponse,
			OpenIdClaims openIdClaims, @Nullable State state) {
		HttpResponse<KeycloakUser> response = client.toBlocking()
				.exchange(HttpRequest.POST("/auth/realms/security/protocol/openid-connect/token/introspect",
						"token=" + tokenResponse.getAccessToken()).contentType(MediaType.APPLICATION_FORM_URLENCODED)
						.basicAuth(clientId, clientSecret), KeycloakUser.class);
		KeycloakUser user = response.body();
		Map<String, Object> attrs = new HashMap<>();
		attrs.put("openIdToken", tokenResponse.getAccessToken());
		return AuthenticationResponse.success(user.getUsername(), user.getRoles(), attrs);
	}

}