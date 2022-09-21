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
        return backgroundInformationRepository.findById(id).orElseThrow(() -> new NotFoundException("No new employee background information for id " + id));
    }

    @Override
    public BackgroundInformation saveProfile (BackgroundInformationCreateDTO backgroundInformationCreateDTO){
        NewHireAccountEntity newHireAccount = newHireAccountRepository.findByEmailAddress(backgroundInformationCreateDTO.getEmailAddress()).orElseThrow(() -> new NotFoundException("A new hire with the provided email address could not be found."));
        BackgroundInformation backgroundInformation = buildNewBackgroundInformationEntity(newHireAccount, backgroundInformationCreateDTO);
        return backgroundInformationRepository.save(backgroundInformation);
    }

    public BackgroundInformation buildNewBackgroundInformationEntity (NewHireAccountEntity newHireAccount, BackgroundInformationCreateDTO backgroundInformationCreateDTO){
        return new BackgroundInformation(newHireAccount, backgroundInformationCreateDTO.getStepComplete());
    }

    @Override
    public BackgroundInformation updateProfile (BackgroundInformationDTO backgroundInformationDTO){
        NewHireAccountEntity newHireAccount = newHireAccountRepository.findByEmailAddress(backgroundInformationDTO.getEmailAddress()).orElseThrow(() -> new NotFoundException("A new hire with the provided email address could not be found."));
        BackgroundInformation backgroundInformation = buildBackgroundInformationEntity(newHireAccount, backgroundInformationDTO);
        return backgroundInformationRepository.save(backgroundInformation);
    }

    public BackgroundInformation buildBackgroundInformationEntity (NewHireAccountEntity newHireAccount, BackgroundInformationDTO backgroundInformationDTO){
        return new BackgroundInformation(newHireAccount, backgroundInformationDTO.getId(), backgroundInformationDTO.getStepComplete());
    }

    @Override
    public Boolean deleteProfile(@NotNull UUID id){
        backgroundInformationRepository.deleteById(id);
        return true;
    }

    @Override
    public List<BackgroundInformation> findAll() { return (List<BackgroundInformation>) backgroundInformationRepository.findAll();}
}
