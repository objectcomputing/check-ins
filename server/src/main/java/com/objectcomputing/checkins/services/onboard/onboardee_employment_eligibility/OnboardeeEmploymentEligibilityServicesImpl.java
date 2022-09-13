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
        var backgroundInformation = onboardeeEmploymentEligibilityRepository.findById(id);
        if (backgroundInformation.isEmpty()) {
            throw new NotFoundException("No employment eligibility information for id " + id);
        }
        return backgroundInformation.get();
    }

    //implement other methods as well
    @Override
    public OnboardeeEmploymentEligibility saveProfile (OnboardeeEmploymentEligibilityCreateDTO onboardeeEmploymentEligibilityCreateDTO){
        NewHireAccountEntity newHire = newHireAccountRepository.findByEmailAddress(onboardeeEmploymentEligibilityCreateDTO.getEmailAddress()).get();
        OnboardeeEmploymentEligibility onboardeeEmploymentEligibility = buildNewOnboardeeEmploymentEligibilityEntity(newHire, onboardeeEmploymentEligibilityCreateDTO);
        return onboardeeEmploymentEligibilityRepository.save(onboardeeEmploymentEligibility);
    }

    public OnboardeeEmploymentEligibility buildNewOnboardeeEmploymentEligibilityEntity (NewHireAccountEntity newHireAccount, OnboardeeEmploymentEligibilityCreateDTO onboardeeEmploymentEligibilityCreateDTO){
        return new OnboardeeEmploymentEligibility(newHireAccount, onboardeeEmploymentEligibilityCreateDTO.getAgeLegal(),
                onboardeeEmploymentEligibilityCreateDTO.getUsCitizen(), onboardeeEmploymentEligibilityCreateDTO.getVisaStatus(),
                onboardeeEmploymentEligibilityCreateDTO.getExpirationDate(), onboardeeEmploymentEligibilityCreateDTO.getFelonyStatus(),
                onboardeeEmploymentEligibilityCreateDTO.getFelonyExplanation());
    }

    @Override
    public OnboardeeEmploymentEligibility updateProfile (OnboardeeEmploymentEligibilityDTO onboardeeEmploymentEligibilityDTO){
        NewHireAccountEntity newHire = newHireAccountRepository.findByEmailAddress(onboardeeEmploymentEligibilityDTO.getEmailAddress()).get();
        OnboardeeEmploymentEligibility onboardeeEmploymentEligibility = updateOnboardeeEmploymentEligibilityEntity(newHire, onboardeeEmploymentEligibilityDTO);
        return onboardeeEmploymentEligibilityRepository.save(onboardeeEmploymentEligibility);
    }

    public OnboardeeEmploymentEligibility updateOnboardeeEmploymentEligibilityEntity (NewHireAccountEntity newHireAccount, OnboardeeEmploymentEligibilityDTO onboardeeEmploymentEligibilityDTO){
        return new OnboardeeEmploymentEligibility(newHireAccount, onboardeeEmploymentEligibilityDTO.getId(),
                onboardeeEmploymentEligibilityDTO.getAgeLegal(), onboardeeEmploymentEligibilityDTO.getUsCitizen(),
                onboardeeEmploymentEligibilityDTO.getVisaStatus(), onboardeeEmploymentEligibilityDTO.getExpirationDate(),
                onboardeeEmploymentEligibilityDTO.getFelonyStatus(), onboardeeEmploymentEligibilityDTO.getFelonyExplanation());
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
