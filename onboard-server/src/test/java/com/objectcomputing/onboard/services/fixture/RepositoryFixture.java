package com.objectcomputing.onboard.services.fixture;

import com.objectcomputing.checkins.services.onboardeeprofile.OnboardingProfileRepository;

import io.micronaut.runtime.server.EmbeddedServer;

public interface RepositoryFixture {
    EmbeddedServer getEmbeddedServer();

    default OnboardingProfileRepository getOnboardingProfileRepository() {
        return getEmbeddedServer().getApplicationContext().getBean(OnboardingProfileRepository.class);
    }
//    default EmailRepository getEmailRepository() {
//        return getEmbeddedServer().getApplicationContext().getBean(EmailRepository.class);
//    }
}
