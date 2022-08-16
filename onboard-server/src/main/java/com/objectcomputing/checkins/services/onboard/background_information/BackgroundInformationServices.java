package com.objectcomputing.checkins.services.onboard.background_information;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface BackgroundInformationServices {
    BackgroundInformation getById(UUID id);

    Set<BackgroundInformation> findByValues(UUID id, String userId, Boolean stepComplete);

    BackgroundInformation saveProfile(BackgroundInformation backgroundInformation);

    Boolean deleteProfile(UUID id);

    List<BackgroundInformation> findAll();
}
