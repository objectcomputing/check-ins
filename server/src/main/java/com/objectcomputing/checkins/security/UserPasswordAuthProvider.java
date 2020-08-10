package com.objectcomputing.checkins.security;

import io.micronaut.context.annotation.Requires;
import io.micronaut.http.HttpRequest;
import io.micronaut.security.authentication.*;
import io.reactivex.Flowable;
import org.reactivestreams.Publisher;

import javax.inject.Inject;
import javax.inject.Singleton;

@Requires(env = {"local"})
@Singleton
public class UserPasswordAuthProvider implements AuthenticationProvider {

    @Inject
    UsersStore store;

    @Override
    public Publisher<AuthenticationResponse> authenticate(HttpRequest<?> httpRequest, AuthenticationRequest<?, ?> authReq) {
        String username = authReq.getIdentity().toString();
        String password = authReq.getSecret().toString();
        if (password.equals(store.getUserPassword(username))) {
            UserDetails details = new UserDetails(username, store.getUserRole(username));
            return Flowable.just(details);
        } else {
            return Flowable.just(new AuthenticationFailed());
        }
    }
}