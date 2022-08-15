package com.objectcomputing.checkins.services.onboard.background_information;

import com.objectcomputing.checkins.exceptions.AlreadyExistsException;
import com.objectcomputing.checkins.exceptions.NotFoundException;
import io.micronaut.core.annotation.Nullable;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.constraints.NotNull;
import java.util.*;

import static com.objectcomputing.checkins.util.Util.nullSafeUUIDToString;

@Singleton
public class BackgroundInformationServicesImpl implements BackgroundInformationServices {

    private static final Logger LOG = LoggerFactory.getLogger(BackgroundInformationServicesImpl.class);

    private final BackgroundInformationRepository backgroundInformationRepository;

    public BackgroundInformationServicesImpl(BackgroundInformationRepository backgroundInformationRepository){
        this.backgroundInformationRepository = backgroundInformationRepository;
    }

    @Override
    public BackgroundInformation getById(@NotNull UUID id){
        Optional<BackgroundInformation> backgroundInformation = backgroundInformationRepository.findById(id);
        if (backgroundInformation.isEmpty()){
            throw new NotFoundException("No new employee background information for id "+ id);
        }
        return backgroundInformation.get();
    }

    @Override
    public Set<BackgroundInformation> findByValues(
            @Nullable UUID id,
            @Nullable String userId,
            @Nullable Boolean stepComplete){
        return new HashSet<>(backgroundInformationRepository.search(nullSafeUUIDToString(id), userId,stepComplete));

    }

    @Override
    public BackgroundInformation saveProfile (BackgroundInformation backgroundInformation){
        if (backgroundInformation.getId() != null){
            throw new AlreadyExistsException("Background Information User Id exists in database");
        }
        if (backgroundInformation.getId() == null){
            return backgroundInformationRepository.save(backgroundInformation);
        }
        return backgroundInformationRepository.update(backgroundInformation);
    }

    @Override
    public Boolean deleteProfile(@NotNull UUID id){
        backgroundInformationRepository.deleteById(id);
        return true;
    }

    @Override
    public List<BackgroundInformation> findAll() { return backgroundInformationRepository.findAll();}
}
