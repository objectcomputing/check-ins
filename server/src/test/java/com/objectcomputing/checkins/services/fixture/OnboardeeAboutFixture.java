package com.objectcomputing.checkins.services.fixture;

import com.objectcomputing.checkins.services.onboard.onboardee_about.OnboardeeAbout;
import reactor.core.publisher.Mono;

public interface OnboardeeAboutFixture extends RepositoryFixture {
    
    default Mono<OnboardeeAbout> createADefaultOnboardeeAbout() {
        return getOnboardeeAboutRepository().save(new OnboardeeAbout("M", "Gmail", "Hi :)", true, true, "No", "Maybe", "Yes", null));
    }

    default Mono<OnboardeeAbout> createADefaultOnboardeeAbout2() {
        return getOnboardeeAboutRepository().save(new OnboardeeAbout("L", "Google Calendar", "Bye :)", true, true, "No", "Maybe", "Yes", null));
    }
}
