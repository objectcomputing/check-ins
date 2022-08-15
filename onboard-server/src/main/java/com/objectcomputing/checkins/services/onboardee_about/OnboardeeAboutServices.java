package com.objectcomputing.checkins.services.onboardee_about;

import java.util.Set;
import java.util.UUID;

public interface OnboardeeAboutServices {
    OnboardeeAbout getById(UUID id);

    Set<OnboardeeAbout> findByValues(UUID id, String tshirtSize, String googleTraining, String introduction,
            Boolean vaccineStatus, Boolean vaccineTwoWeeks, String otherTraining, String additionalSkills,
            String certifications);

    OnboardeeAbout saveAbout(OnboardeeAbout onboardeeAbout);

    Boolean deleteAbout(UUID id);

}
