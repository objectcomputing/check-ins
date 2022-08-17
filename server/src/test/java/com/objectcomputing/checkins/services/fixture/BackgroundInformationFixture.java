package com.objectcomputing.checkins.services.fixture;

import com.objectcomputing.checkins.services.onboard.background_information.BackgroundInformation;

import java.util.UUID;

public interface BackgroundInformationFixture extends RepositoryFixture {
    default BackgroundInformation createDefaultBackgroundInformation(){

        return getBackgroundInformationRepository().save(new BackgroundInformation(UUID.randomUUID()
,true));
    }

    default BackgroundInformation createSecondBackgroundInformation() {
        return getBackgroundInformationRepository().save(new BackgroundInformation(UUID.randomUUID(), false));
    }
}
