package com.objectcomputing.checkins.services.onboard.background_information;

import com.objectcomputing.checkins.exceptions.NotFoundException;
import com.objectcomputing.checkins.services.onboardeecreate.newhire.model.NewHireAccountEntity;
import com.objectcomputing.checkins.services.onboardeecreate.newhire.model.NewHireAccountRepository;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

@Singleton
public class BackgroundInformationServicesImpl implements BackgroundInformationServices {

    private static final Logger LOG = LoggerFactory.getLogger(BackgroundInformationServicesImpl.class);

    private final BackgroundInformationRepository backgroundInformationRepository;

    private final NewHireAccountRepository newHireAccountRepository;


    public BackgroundInformationServicesImpl(BackgroundInformationRepository backgroundInformationRepository, NewHireAccountRepository newHireAccountRepository){
        this.backgroundInformationRepository = backgroundInformationRepository;
        this.newHireAccountRepository = newHireAccountRepository;
    }

    @Override
    public BackgroundInformation getById(@NotNull UUID id){
        return backgroundInformationRepository.findById(id).flatMap(backgroundInformation -> {
            if (backgroundInformation == null) {
                throw new NotFoundException("No new employee background information for id " + id);
            }
            return Mono.just(backgroundInformation);
            }).block();
    }

    @Override
    public BackgroundInformation saveProfile (BackgroundInformationCreateDTO backgroundInformationCreateDTO){
        return newHireAccountRepository.findByEmailAddress(backgroundInformationCreateDTO.getEmailAddress())
                        .flatMap(newHire -> buildNewBackgroundInformationEntity(newHire, backgroundInformationCreateDTO))
                        .flatMap(backgroundEntity -> backgroundInformationRepository.save(backgroundEntity)).block();
    }

    public Mono<BackgroundInformation> buildNewBackgroundInformationEntity (NewHireAccountEntity newHireAccount, BackgroundInformationCreateDTO backgroundInformationCreateDTO){
        return Mono.just ( new BackgroundInformation(newHireAccount, backgroundInformationCreateDTO.getStepComplete()));
    }

    @Override
    public BackgroundInformation updateProfile (BackgroundInformationDTO backgroundInformationDTO){
        return newHireAccountRepository.findByEmailAddress(backgroundInformationDTO.getEmailAddress())
                .flatMap(newHire -> buildBackgroundInformationEntity(newHire, backgroundInformationDTO))
                .flatMap(backgroundEntity -> backgroundInformationRepository.save(backgroundEntity)).block();
    }

    public Mono<BackgroundInformation> buildBackgroundInformationEntity (NewHireAccountEntity newHireAccount, BackgroundInformationDTO backgroundInformationDTO){
        return Mono.just ( new BackgroundInformation(newHireAccount, backgroundInformationDTO.getId(), backgroundInformationDTO.getStepComplete()));
    }

    @Override
    public Boolean deleteProfile(@NotNull UUID id){
        backgroundInformationRepository.deleteById(id);
        return true;
    }

    @Override
    public List<BackgroundInformation> findAll() { return (List<BackgroundInformation>) backgroundInformationRepository.findAll();}
}
