package com.objectcomputing.checkins.services.onboard.onboardeeprofile;

import com.objectcomputing.checkins.exceptions.NotFoundException;
import com.objectcomputing.checkins.services.onboard.onboardee_employment_eligibility.OnboardeeEmploymentEligibility;
import com.objectcomputing.checkins.services.onboardeecreate.newhire.model.NewHireAccountEntity;
import com.objectcomputing.checkins.services.onboardeecreate.newhire.model.NewHireAccountRepository;
import jakarta.inject.Singleton;

import javax.validation.constraints.NotNull;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

@Singleton
public class OnboardingProfileServicesImpl implements OnboardingProfileServices  {
    private static final Logger LOG = LoggerFactory.getLogger(OnboardingProfileServicesImpl.class);
    private final NewHireAccountRepository newHireAccountRepository;

    private final OnboardingProfileRepository onboardingProfileRepository;

    public OnboardingProfileServicesImpl(OnboardingProfileRepository onboardingProfileRepository, NewHireAccountRepository newHireAccountRepository) {
        this.onboardingProfileRepository = onboardingProfileRepository;
        this.newHireAccountRepository = newHireAccountRepository;
    }

    @Override
    public OnboardingProfile getById(@NotNull UUID id){
        Optional<OnboardingProfile> onboardingProfile = onboardingProfileRepository.findById(id);
        if (onboardingProfile.isEmpty()) {
            throw new NotFoundException("No new employee background information for id " + id);
        }
        return onboardingProfile.get();
    }

    @Override
    public OnboardingProfile saveProfile(OnboardingProfileCreateDTO onboardingProfileCreateDTO){
        NewHireAccountEntity newHire = newHireAccountRepository.findByEmailAddress(onboardingProfileCreateDTO.getEmailAddress()).get();
        OnboardingProfile onboardingProfile = buildNewOnboardEntity(newHire, onboardingProfileCreateDTO);
        return onboardingProfileRepository.save(onboardingProfile);
    }

    public OnboardingProfile buildNewOnboardEntity(NewHireAccountEntity newHireAccount, OnboardingProfileCreateDTO onboardingProfileCreateDTO){
        return new OnboardingProfile(newHireAccount, onboardingProfileCreateDTO.getFirstName(), onboardingProfileCreateDTO.getMiddleName(),
                onboardingProfileCreateDTO.getLastName(), onboardingProfileCreateDTO.getSocialSecurityNumber(),
                onboardingProfileCreateDTO.getBirthDate(), onboardingProfileCreateDTO.getCurrentAddress(),
                onboardingProfileCreateDTO.getPreviousAddress(), onboardingProfileCreateDTO.getPhoneNumber(),
                onboardingProfileCreateDTO.getSecondPhoneNumber(), onboardingProfileCreateDTO.getPersonalEmail());
    }

    @Override
    public OnboardingProfile updateProfile(OnboardingProfileDTO onboardingProfileDTO){
        NewHireAccountEntity newHire = newHireAccountRepository.findByEmailAddress(onboardingProfileDTO.getEmailAddress()).get();
        OnboardingProfile onboardingProfile = buildOnboardEntity(newHire, onboardingProfileDTO);
        return onboardingProfileRepository.update(onboardingProfile);
    }

    public OnboardingProfile buildOnboardEntity(NewHireAccountEntity newHireAccount, OnboardingProfileDTO onboardingProfileDTO){
        return new OnboardingProfile(newHireAccount, onboardingProfileDTO.getId(), onboardingProfileDTO.getFirstName(),
                onboardingProfileDTO.getMiddleName(), onboardingProfileDTO.getLastName(), onboardingProfileDTO.getSocialSecurityNumber(),
                onboardingProfileDTO.getBirthDate(), onboardingProfileDTO.getCurrentAddress(), onboardingProfileDTO.getPreviousAddress(),
                onboardingProfileDTO.getPhoneNumber(), onboardingProfileDTO.getSecondPhoneNumber(), onboardingProfileDTO.getPersonalEmail());
    }

    @Override
    public Boolean deleteProfile(@NotNull UUID id) {
        onboardingProfileRepository.deleteById(id);
        return true;
    }

    @Override
    public OnboardingProfile findByName(String firstName, String lastName) {
        List<OnboardingProfile> searchResult = onboardingProfileRepository.search(null, firstName, null, lastName,
                null, null, null, null, null, null, null);
        if (searchResult == null || searchResult.size() != 1) {
            throw new NotFoundException(String.format("Expected exactly 1 result. Found %d.", searchResult.size()));
        }
        return searchResult.get(0);
    }
}
