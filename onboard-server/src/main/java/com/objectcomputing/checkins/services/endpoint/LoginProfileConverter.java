package com.objectcomputing.checkins.services.endpoint;

import com.objectcomputing.checkins.services.commons.SharableLoginAccount;
import com.objectcomputing.checkins.services.model.LoginAccount;
import jakarta.inject.Singleton;
import reactor.core.publisher.Mono;

import java.util.Date;

@Singleton
public class LoginProfileConverter {

    public Mono<SharableLoginAccount> convert(LoginAccount loginAccount) {
        return Mono.defer(() -> Mono.just(new SharableLoginAccount(
                loginAccount.getId(), loginAccount.getEmailAddress(),
                new Date(loginAccount.getCreatedInstant().toEpochMilli()), loginAccount.getState(),
                loginAccount.getRole())));
    }
}
