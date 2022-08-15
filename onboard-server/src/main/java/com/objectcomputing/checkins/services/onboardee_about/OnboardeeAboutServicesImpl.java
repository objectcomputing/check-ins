package com.objectcomputing.checkins.services.onboardee_about;

import com.objectcomputing.checkins.exceptions.AlreadyExistsException;
import com.objectcomputing.checkins.exceptions.NotFoundException;
import jakarta.inject.Singleton;

import java.util.*;

@Singleton
public class OnboardeeAboutServicesImpl implements OnboardeeAboutServices {
    private final OnboardeeAboutRespository onboardeeAboutRespository;

    public OnboardeeAboutServicesImpl(OnboardeeAboutRespository onboardeeAboutRespository) {
        this.onboardeeAboutRespository = onboardeeAboutRespository;
    }

    @Override
    public OnboardeeAbout getById(UUID id) {
        Optional<OnboardeeAbout> onboardeeAbout = onboardeeAboutRespository.findById(id);
        if (onboardeeAbout.isEmpty()) {
            throw new NotFoundException("No new about you information for id " + id);
        }
        return onboardeeAbout.get();
    }

    @Override
    public Set<OnboardeeAbout> findByValues(UUID id, String tshirtSize, String googleTraining, String introduction,
            Boolean vaccineStatus, Boolean vaccineTwoWeeks, String otherTraining, String additionalSkills,
            String certifications) {
                HashSet<OnboardeeAbout> onboardee_about = new HashSet<>(onboardeeAboutRespository.search(id, tshirtSize, googleTraining, introduction, vaccineStatus, vaccineTwoWeeks, otherTraining, additionalSkills, certifications));
                return onboardee_about;
            }

    @Override
    public OnboardeeAbout saveAbout(OnboardeeAbout onboardeeAbout) {
        if(onboardeeAbout.getId() == null) {
            return onboardeeAboutRespository.save(onboardeeAbout);
        }
        return onboardeeAboutRespository.update(onboardeeAbout);
    }

    @Override
    public Boolean deleteAbout(UUID id) {
        onboardeeAboutRespository.deleteById(id);
        return true;
    }
}
