package com.objectcomputing.checkins.services.onboard.onboardee_about;

import com.objectcomputing.checkins.newhire.model.NewHireAccountEntity;
import com.objectcomputing.checkins.newhire.model.NewHireAccountRepository;
import com.objectcomputing.checkins.services.onboard.exceptions.NotFoundException;
import jakarta.inject.Singleton;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@Singleton
public class OnboardeeAboutServicesImpl implements OnboardeeAboutServices {
    private final OnboardeeAboutRespository onboardeeAboutRespository;
    private final NewHireAccountRepository newHireAccountRepository;

    public OnboardeeAboutServicesImpl(OnboardeeAboutRespository onboardeeAboutRespository,
            NewHireAccountRepository newHireAccountRepository) {
        this.onboardeeAboutRespository = onboardeeAboutRespository;
        this.newHireAccountRepository = newHireAccountRepository;
    }

    @Override
    public OnboardeeAbout getById(UUID id) {
        return onboardeeAboutRespository.findById(id).flatMap(backgroundInformation -> {
            if (backgroundInformation == null) {
                throw new NotFoundException("No new employee background information for id " + id);
            }
            return Mono.just(backgroundInformation);
        }).block();
    }

    @Override
    public OnboardeeAbout saveAbout(OnboardeeAboutCreateDTO onboardeeAboutCreateDTO) {
        return newHireAccountRepository.findByEmailAddress(onboardeeAboutCreateDTO.getEmailAddress())
                .flatMap(newHire -> buildNewOnboardeeAboutEntity(newHire, onboardeeAboutCreateDTO))
                .flatMap(backgroundEntity -> onboardeeAboutRespository.save(backgroundEntity)).block();
    }

    public Mono<OnboardeeAbout> buildNewOnboardeeAboutEntity(NewHireAccountEntity newHireAccount,
                                                          OnboardeeAboutCreateDTO onboardeeAboutCreateDTO) {
        return Mono.just(new OnboardeeAbout(onboardeeAboutCreateDTO.getTshirtSize(),
                onboardeeAboutCreateDTO.getGoogleTraining(), onboardeeAboutCreateDTO.getIntroduction(),
                onboardeeAboutCreateDTO.getVaccineStatus(), onboardeeAboutCreateDTO.getVaccineTwoWeeks(),
                onboardeeAboutCreateDTO.getOtherTraining(), onboardeeAboutCreateDTO.getAdditionalSkills(),
                onboardeeAboutCreateDTO.getCertifications(), newHireAccount));
    }

    @Override
    public OnboardeeAbout updateAbout(OnboardeeAboutDTO onboardeeAboutDTO) {
        return newHireAccountRepository.findByEmailAddress(onboardeeAboutDTO.getEmailAddress())
                .flatMap(newHire -> buildOnboardeeAboutEntity(newHire, onboardeeAboutDTO))
                .flatMap(backgroundEntity -> onboardeeAboutRespository.save(backgroundEntity)).block();    }

    @Override
    public Boolean deleteAbout(UUID id) {
        onboardeeAboutRespository.deleteById(id);
        return true;
    }

    public Mono<OnboardeeAbout> buildOnboardeeAboutEntity(NewHireAccountEntity newHireAccount,
            OnboardeeAboutDTO onboardeeAboutDTO) {
        return Mono.just(new OnboardeeAbout(onboardeeAboutDTO.getId(), onboardeeAboutDTO.getTshirtSize(),
                onboardeeAboutDTO.getGoogleTraining(), onboardeeAboutDTO.getIntroduction(),
                onboardeeAboutDTO.getVaccineStatus(), onboardeeAboutDTO.getVaccineTwoWeeks(),
                onboardeeAboutDTO.getOtherTraining(), onboardeeAboutDTO.getAdditionalSkills(),
                onboardeeAboutDTO.getCertifications(), newHireAccount));
    }

    public List<OnboardeeAbout> findAll() {
        return (List<OnboardeeAbout>) onboardeeAboutRespository.findAll();
    }

}
