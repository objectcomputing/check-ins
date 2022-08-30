package com.objectcomputing.checkins.services.onboard.onboardeeprofile;

import com.objectcomputing.checkins.newhire.model.NewHireAccountEntity;
import com.objectcomputing.checkins.newhire.model.NewHireAccountRepository;
import com.objectcomputing.checkins.services.onboard.exceptions.NotFoundException;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import javax.validation.constraints.NotNull;
import java.util.UUID;

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
        return onboardingProfileRepository.findById(id).flatMap(onboardingProfile -> {
            if (onboardingProfile == null) {
                throw new NotFoundException("No new employee background information for id " + id);
            }
            return Mono.just(onboardingProfile);
        }).block();
    }

    @Override
    public OnboardingProfile saveProfile(OnboardingProfileCreateDTO onboardingProfileCreateDTO){
        return newHireAccountRepository.findByEmailAddress(onboardingProfileCreateDTO.getEmailAddress())
                .flatMap(newHire -> buildNewOnboardEntity(newHire, onboardingProfileCreateDTO))
                .flatMap(onboardeeProfileEntity -> onboardingProfileRepository.save(onboardeeProfileEntity)).block();
    }

    public Mono<OnboardingProfile> buildNewOnboardEntity(NewHireAccountEntity newHireAccount, OnboardingProfileCreateDTO onboardingProfileCreateDTO){
        return Mono.just(new OnboardingProfile(newHireAccount, onboardingProfileCreateDTO.getFirstName(), onboardingProfileCreateDTO.getMiddleName(),
                onboardingProfileCreateDTO.getLastName(), onboardingProfileCreateDTO.getSocialSecurityNumber(),
                onboardingProfileCreateDTO.getBirthDate(), onboardingProfileCreateDTO.getCurrentAddress(),
                onboardingProfileCreateDTO.getPreviousAddress(), onboardingProfileCreateDTO.getPhoneNumber(),
                onboardingProfileCreateDTO.getSecondPhoneNumber(), onboardingProfileCreateDTO.getPersonalEmail()));
    }

    @Override
    public OnboardingProfile updateProfile(OnboardingProfileDTO onboardingProfileDTO){
        return newHireAccountRepository.findByEmailAddress(onboardingProfileDTO.getEmailAddress())
                .flatMap(newHire -> buildOnboardEntity(newHire, onboardingProfileDTO))
                .flatMap(onboardeeProfileEntity -> onboardingProfileRepository.update(onboardeeProfileEntity)).block();
    }

    public Mono<OnboardingProfile> buildOnboardEntity(NewHireAccountEntity newHireAccount, OnboardingProfileDTO onboardingProfileDTO){
        return Mono.just(new OnboardingProfile(newHireAccount, onboardingProfileDTO.getId(), onboardingProfileDTO.getFirstName(),
                onboardingProfileDTO.getMiddleName(), onboardingProfileDTO.getLastName(), onboardingProfileDTO.getSocialSecurityNumber(),
                onboardingProfileDTO.getBirthDate(), onboardingProfileDTO.getCurrentAddress(), onboardingProfileDTO.getPreviousAddress(),
                onboardingProfileDTO.getPhoneNumber(), onboardingProfileDTO.getSecondPhoneNumber(), onboardingProfileDTO.getPersonalEmail()));
    }

    @Override
    public Boolean deleteProfile(@NotNull UUID id) {
        onboardingProfileRepository.deleteById(id);
        return true;
    }

    @Override
    public OnboardingProfile findByName(String firstName, String lastName) {
        return onboardingProfileRepository.search(null, firstName, null, lastName,
                null, null, null, null, null, null, null).flatMap(searchResult -> {
            if (searchResult == null) {
                throw new NotFoundException("Expected exactly 1 result. Found none.");
            }
            return Mono.just(searchResult);
        }).block();
    }
}
