package com.objectcomputing.checkins.services.fixture;

import com.objectcomputing.checkins.services.onboard.background_information.BackgroundInformation;

public interface BackgroundInformationFixture extends RepositoryFixture {
    default BackgroundInformation createDefaultBackgroundInformation(){
        return getBackgroundInformationRepository().save(new BackgroundInformation("McDaniel",true));
    }

    default BackgroundInformation createSecondBackgroundInformation() {
        return getBackgroundInformationRepository().save(new BackgroundInformation("brandish", false));
    }
}
