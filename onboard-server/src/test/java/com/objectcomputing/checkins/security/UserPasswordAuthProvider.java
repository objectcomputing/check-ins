package com.objectcomputing.checkins.security;

import io.micronaut.context.annotation.Requires;
import io.micronaut.context.env.Environment;
import io.micronaut.http.HttpRequest;
import io.micronaut.security.authentication.AuthenticationProvider;
import io.micronaut.security.authentication.AuthenticationRequest;
import io.micronaut.security.authentication.AuthenticationResponse;
import org.reactivestreams.Publisher;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Requires(env = Environment.TEST, missingBeans = LocalUserPasswordAuthProvider.class)
@Singleton
public class UserPasswordAuthProvider implements AuthenticationProvider {

    @Inject
    UsersStore store;

    @Override
    public Publisher<AuthenticationResponse> authenticate(HttpRequest<?> httpRequest, AuthenticationRequest<?, ?> authReq) {
        String email = authReq.getIdentity().toString();
        String roleCred = authReq.getSecret().toString();
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("email", email);

        return Mono.just(AuthenticationResponse.success(email, store.getUserRole(roleCred), attributes));
    }
}