package com.objectcomputing.checkins.services.onboard.background_information;

import java.util.List;
import java.util.UUID;

public interface BackgroundInformationServices {
    BackgroundInformation getById(UUID id);

    BackgroundInformation saveProfile(BackgroundInformationCreateDTO backgroundInformationCreateDTO);

    BackgroundInformation updateProfile(BackgroundInformationDTO backgroundInformationDTO);

    Boolean deleteProfile(UUID id);

    List<BackgroundInformation> findAll();

}
