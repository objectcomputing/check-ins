package com.objectcomputing.checkins.services.fixture;

import com.objectcomputing.checkins.services.onboard.background_information.BackgroundInformation;
import com.objectcomputing.checkins.services.onboardeecreate.newhire.model.NewHireAccountEntity;
import reactor.core.publisher.Mono;

public interface BackgroundInformationFixture extends RepositoryFixture {
    default Mono<BackgroundInformation> createDefaultBackgroundInformation(){

        NewHireAccountEntity newHireAccountEntity = new NewHireAccountEntity();
        return getBackgroundInformationRepository().save(new BackgroundInformation(newHireAccountEntity,true));
    }

    default Mono<BackgroundInformation> createSecondBackgroundInformation() {
        NewHireAccountEntity newHireAccountEntity = new NewHireAccountEntity();
        return getBackgroundInformationRepository().save(new BackgroundInformation(newHireAccountEntity, false));
    }
}
