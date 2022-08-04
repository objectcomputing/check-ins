package com.objectcomputing.geoai.platform.account.endpoint;

import com.objectcomputing.geoai.platform.account.commons.SharableUserAccount;
import com.objectcomputing.geoai.platform.account.model.UserAccount;
import jakarta.inject.Singleton;
import reactor.core.publisher.Mono;

import java.util.Date;

@Singleton
public class UserAccountConverter {

    public Mono<SharableUserAccount> convert(UserAccount userAccount) {
        return Mono.defer(() -> Mono.just(new SharableUserAccount(
                userAccount.getId(), userAccount.getOrganization(), userAccount.getEmailAddress(),
                new Date(userAccount.getCreatedInstant().toEpochMilli()), userAccount.getState(),
                userAccount.getRole(), userAccount.getAuthenticationMethod(), userAccount.getMemberships())));
    }
}
