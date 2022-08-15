package com.objectcomputing.checkins.services.fixture;

import com.objectcomputing.checkins.services.onboardee_about.OnboardeeAbout;

public interface OnboardeeAboutFixture extends RepositoryFixture {
    
    default OnboardeeAbout createADefaultOnboardeeAbout() {
        return getOnboardeeAboutRepository().save(new OnboardeeAbout("M", "Gmail", "Hi :)", true, true, "No", "Maybe", "Yes"));
    }

    default OnboardeeAbout createADefaultOnboardeeAbout2() {
        return getOnboardeeAboutRepository().save(new OnboardeeAbout("L", "Google Calendar", "Bye :)", true, true, "No", "Maybe", "Yes"));
    }
}
