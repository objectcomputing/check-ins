package com.objectcomputing.checkins.services.onboard.onboardee_employment_eligibility;

import  com.objectcomputing.checkins.exceptions.AlreadyExistsException;
import com.objectcomputing.checkins.exceptions.NotFoundException;
import com.objectcomputing.checkins.services.onboard.background_information.BackgroundInformation;
import com.objectcomputing.checkins.services.onboard.background_information.BackgroundInformationCreateDTO;
import com.objectcomputing.checkins.services.onboardeecreate.newhire.model.NewHireAccountEntity;
import com.objectcomputing.checkins.services.onboardeecreate.newhire.model.NewHireAccountRepository;
import jakarta.inject.Singleton;
import reactor.core.publisher.Mono;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.*;

import static com.objectcomputing.checkins.util.Util.nullSafeUUIDToString;

@Singleton
public class OnboardeeEmploymentEligibilityServicesImpl implements OnboardeeEmploymentEligibilityServices {
    private final OnboardeeEmploymentEligibilityRepository onboardeeEmploymentEligibilityRepository;

    private final NewHireAccountRepository newHireAccountRepository;

    public OnboardeeEmploymentEligibilityServicesImpl(OnboardeeEmploymentEligibilityRepository onboardeeEmploymentEligibilityRepository, NewHireAccountRepository newHireAccountRepository) {
        this.onboardeeEmploymentEligibilityRepository = onboardeeEmploymentEligibilityRepository;
        this.newHireAccountRepository = newHireAccountRepository;
    }

    @Override
    public OnboardeeEmploymentEligibility getById(@NotNull UUID id){
        return onboardeeEmploymentEligibilityRepository.findById(id).flatMap(backgroundInformation -> {
            if (backgroundInformation == null) {
                throw new NotFoundException("No employment eligibility information for id " + id);
            }
            return Mono.just(backgroundInformation);
        }).block();
    }

    //implement other methods as well
    @Override
    public OnboardeeEmploymentEligibility saveProfile (OnboardeeEmploymentEligibilityCreateDTO onboardeeEmploymentEligibilityCreateDTO){
        return newHireAccountRepository.findByEmailAddress(onboardeeEmploymentEligibilityCreateDTO.getEmailAddress())
                .flatMap(newHire -> buildNewOnboardeeEmploymentEligibilityEntity(newHire, onboardeeEmploymentEligibilityCreateDTO))
                .flatMap(onboardeeEmploymentEligibility -> onboardeeEmploymentEligibilityRepository.save(onboardeeEmploymentEligibility)).block();
    }

    public Mono<OnboardeeEmploymentEligibility> buildNewOnboardeeEmploymentEligibilityEntity (NewHireAccountEntity newHireAccount, OnboardeeEmploymentEligibilityCreateDTO onboardeeEmploymentEligibilityCreateDTO){
        return Mono.just ( new OnboardeeEmploymentEligibility(newHireAccount, onboardeeEmploymentEligibilityCreateDTO.getAgeLegal(),
                onboardeeEmploymentEligibilityCreateDTO.getUsCitizen(), onboardeeEmploymentEligibilityCreateDTO.getVisaStatus(),
                onboardeeEmploymentEligibilityCreateDTO.getExpirationDate(), onboardeeEmploymentEligibilityCreateDTO.getFelonyStatus(),
                onboardeeEmploymentEligibilityCreateDTO.getFelonyExplanation()));
    }

    @Override
    public OnboardeeEmploymentEligibility updateProfile (OnboardeeEmploymentEligibilityDTO onboardeeEmploymentEligibilityDTO){
        return newHireAccountRepository.findByEmailAddress(onboardeeEmploymentEligibilityDTO.getEmailAddress())
                .flatMap(newHire -> updateOnboardeeEmploymentEligibilityEntity(newHire, onboardeeEmploymentEligibilityDTO))
                .flatMap(onboardeeEmploymentEligibility -> onboardeeEmploymentEligibilityRepository.save(onboardeeEmploymentEligibility)).block();
    }

    public Mono<OnboardeeEmploymentEligibility> updateOnboardeeEmploymentEligibilityEntity (NewHireAccountEntity newHireAccount, OnboardeeEmploymentEligibilityDTO onboardeeEmploymentEligibilityDTO){
        return Mono.just ( new OnboardeeEmploymentEligibility(newHireAccount, onboardeeEmploymentEligibilityDTO.getId(),
                onboardeeEmploymentEligibilityDTO.getAgeLegal(), onboardeeEmploymentEligibilityDTO.getUsCitizen(),
                onboardeeEmploymentEligibilityDTO.getVisaStatus(), onboardeeEmploymentEligibilityDTO.getExpirationDate(),
                onboardeeEmploymentEligibilityDTO.getFelonyStatus(), onboardeeEmploymentEligibilityDTO.getFelonyExplanation()));
    }

    @Override
    public Boolean deleteProfile(@NotNull UUID id) {
        onboardeeEmploymentEligibilityRepository.deleteById(id);
        return true;
    }

    @Override
    public List<OnboardeeEmploymentEligibility> findAll() {
        return (List<OnboardeeEmploymentEligibility>) onboardeeEmploymentEligibilityRepository.findAll();
    }
}
