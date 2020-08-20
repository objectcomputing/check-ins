package com.objectcomputing.checkins.security;

import io.micronaut.context.annotation.Requires;
import io.micronaut.http.HttpRequest;
import io.micronaut.security.authentication.AuthenticationProvider;
import io.micronaut.security.authentication.AuthenticationRequest;
import io.micronaut.security.authentication.AuthenticationResponse;
import io.micronaut.security.authentication.UserDetails;
import io.reactivex.Flowable;
import org.reactivestreams.Publisher;

import javax.inject.Inject;
import javax.inject.Singleton;

@Requires(env = "test")
@Singleton
public class UserPasswordAuthProvider implements AuthenticationProvider {

    @Inject
    UsersStore store;

    @Override
    public Publisher<AuthenticationResponse> authenticate(HttpRequest<?> httpRequest, AuthenticationRequest<?, ?> authReq) {
        String email = authReq.getIdentity().toString();
        String roleCred = authReq.getSecret().toString();
        UserDetails details = new UserDetails(email, store.getUserRole(roleCred));
        return Flowable.just(details);
    }
}