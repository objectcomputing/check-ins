package com.objectcomputing.checkins.services.onboardeecreate.newhire.endpoint;

import com.objectcomputing.checkins.services.onboardeecreate.newhire.commons.SharableNewHireAccount;
import com.objectcomputing.checkins.services.onboardeecreate.newhire.model.NewHireAccountEntity;
import jakarta.inject.Singleton;
import reactor.core.publisher.Mono;

import java.util.Date;

@Singleton
public class SharableNewHireAccountConverter {

    public Mono<SharableNewHireAccount> convert(NewHireAccountEntity newHireAccount) {
        return Mono.defer(() -> Mono.just(new SharableNewHireAccount(
                newHireAccount.getId(), newHireAccount.getEmailAddress(), newHireAccount.getState(),
                new Date(newHireAccount.getCreatedInstant().toEpochMilli()))));
    }
}
