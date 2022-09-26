package com.objectcomputing.checkins.services.onboard.onboardee_about;

import java.util.UUID;

public interface OnboardeeAboutServices {
    OnboardeeAbout getById(UUID id);

    OnboardeeAbout saveAbout(OnboardeeAboutCreateDTO onboardeeAboutCreateDTO);

    OnboardeeAbout updateAbout(OnboardeeAboutDTO onboardeeAboutDTO);

    Boolean deleteAbout(UUID id);

}
