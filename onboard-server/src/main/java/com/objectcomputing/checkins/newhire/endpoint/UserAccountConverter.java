package com.objectcomputing.checkins.newhire.endpoint;

import com.objectcomputing.checkins.newhire.model.LoginAccount;
import jakarta.inject.Singleton;
import reactor.core.publisher.Mono;

import java.util.Date;

@Singleton
public class UserAccountConverter {

    public Mono<LoginAccount> convert(LoginAccount userAccount) {
        return Mono.defer(() -> Mono.just(new LoginAccount(
                userAccount.getId(), userAccount.getEmailAddress(), userAccount.getState(),
                userAccount.getRole(), userAccount.getCreatedInstant(), null )));
    }
}
