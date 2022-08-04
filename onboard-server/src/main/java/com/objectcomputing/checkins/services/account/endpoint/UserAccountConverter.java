package com.objectcomputing.checkins.services.account.endpoint;

import com.objectcomputing.checkins.services.commons.SharableLoginAccount;
import com.objectcomputing.checkins.services.model.LoginAccount;
import jakarta.inject.Singleton;
import reactor.core.publisher.Mono;

import java.util.Date;

@Singleton
public class UserAccountConverter {

    public Mono<SharableLoginAccount> convert(LoginAccount userAccount) {
        return Mono.defer(() -> Mono.just(new SharableLoginAccount(
                userAccount.getId(), userAccount.getEmailAddress(),
                new Date(userAccount.getCreatedInstant().toEpochMilli()), userAccount.getState(),
                userAccount.getRole())));
    }
}
