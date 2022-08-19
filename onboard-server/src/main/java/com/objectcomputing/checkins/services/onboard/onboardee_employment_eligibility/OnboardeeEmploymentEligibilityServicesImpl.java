package com.objectcomputing.checkins.services.onboard.onboardee_employment_eligibility;

import com.objectcomputing.checkins.newhire.model.NewHireAccountEntity;
import com.objectcomputing.checkins.newhire.model.NewHireAccountRepository;
import com.objectcomputing.checkins.services.onboard.exceptions.NotFoundException;
import jakarta.inject.Singleton;
import reactor.core.publisher.Mono;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

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
