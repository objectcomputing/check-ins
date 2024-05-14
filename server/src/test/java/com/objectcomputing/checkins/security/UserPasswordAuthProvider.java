package com.objectcomputing.checkins.security;

import io.micronaut.context.annotation.Requires;
import io.micronaut.context.env.Environment;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.core.async.annotation.SingleResult;
import io.micronaut.http.HttpRequest;
import io.micronaut.security.authentication.AuthenticationRequest;
import io.micronaut.security.authentication.AuthenticationResponse;
import io.micronaut.security.authentication.provider.ReactiveAuthenticationProvider;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Requires(env = Environment.TEST, missingBeans = LocalUserPasswordAuthProvider.class)
@Singleton
public class UserPasswordAuthProvider implements ReactiveAuthenticationProvider<HttpRequest<?>, String, String> {

    @Inject
    UsersStore store;

    @Override
    @SingleResult
    public @NonNull Publisher<AuthenticationResponse> authenticate(@Nullable HttpRequest<?> requestContext, @NonNull AuthenticationRequest<String, String> authReq) {
        String email = authReq.getIdentity();
        String roleCred = authReq.getSecret();
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("email", email);

        return Mono.just(AuthenticationResponse.success(email, store.getUserRole(roleCred), attributes));
    }
}