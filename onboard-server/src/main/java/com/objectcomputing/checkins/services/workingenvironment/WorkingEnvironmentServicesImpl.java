package com.objectcomputing.checkins.services.workingenvironment;

import jakarta.inject.Singleton;
import reactor.core.publisher.Mono;

import java.util.*;

import com.objectcomputing.checkins.newhire.model.NewHireAccountEntity;
import com.objectcomputing.checkins.newhire.model.NewHireAccountRepository;
import com.objectcomputing.checkins.services.onboardeeprofile.exceptions.NotFoundException;

@Singleton
public class WorkingEnvironmentServicesImpl implements WorkingEnvironmentServices {
    private final WorkingEnvironmentRepository workingEnvironmentRepository;
    private final NewHireAccountRepository newHireAccountRepository;

    public WorkingEnvironmentServicesImpl(WorkingEnvironmentRepository workingEnvironmentRepository,
            NewHireAccountRepository newHireAccountRepository) {
        this.workingEnvironmentRepository = workingEnvironmentRepository;
        this.newHireAccountRepository = newHireAccountRepository;
    }

    @Override
    public WorkingEnvironment getById(UUID id) {
        return workingEnvironmentRepository.findById(id).flatMap(workingEnvironmentInformation -> {
            if (workingEnvironmentInformation == null) {
                throw new NotFoundException("No new employee background information for id " + id);
            }
            return Mono.just(workingEnvironmentInformation);
        }).block();
    }

    @Override
    public WorkingEnvironment saveWorkingEnvironment(WorkingEnvironmentCreateDTO workingEnvironmentCreateDTO) {
        return newHireAccountRepository.findByEmailAddress(workingEnvironmentCreateDTO.getEmailAddress())
                .flatMap(newHire -> buildNewWorkingEnvironmentEntity(newHire, workingEnvironmentCreateDTO))
                .flatMap(backgroundEntity -> workingEnvironmentRepository.save(backgroundEntity)).block();
    }

    private Mono<WorkingEnvironment> buildNewWorkingEnvironmentEntity(NewHireAccountEntity newHire,
            WorkingEnvironmentCreateDTO workingEnvironmentCreateDTO) {
        return Mono.just(new WorkingEnvironment(workingEnvironmentCreateDTO.getWorkLocation(),
                workingEnvironmentCreateDTO.getKeyType(), workingEnvironmentCreateDTO.getOsType(),
                workingEnvironmentCreateDTO.getAccessories(), workingEnvironmentCreateDTO.getOtherAccessories(),
                newHire));
    }

    @Override
    public WorkingEnvironment updateWorkingEnvironment(WorkingEnvironmentDTO workingEnvironmentDTO) {
        return newHireAccountRepository.findByEmailAddress(workingEnvironmentDTO.getEmailAddress())
                .flatMap(newHire -> buildWorkingEnvironmentEntity(newHire, workingEnvironmentDTO))
                .flatMap(backgroundEntity -> workingEnvironmentRepository.save(backgroundEntity)).block();
    }

    private Mono<WorkingEnvironment> buildWorkingEnvironmentEntity(NewHireAccountEntity newHire,
            WorkingEnvironmentDTO workingEnvironmentDTO) {
        return Mono.just(new WorkingEnvironment(workingEnvironmentDTO.getId(), workingEnvironmentDTO.getWorkLocation(),
                workingEnvironmentDTO.getKeyType(), workingEnvironmentDTO.getOsType(),
                workingEnvironmentDTO.getAccessories(), workingEnvironmentDTO.getOtherAccessories(),
                newHire));
    }

    @Override
    public Boolean deleteWorkingEnvironment(UUID id) {
        workingEnvironmentRepository.deleteById(id);
        return true;
    }

    public List<WorkingEnvironment> findAll() { return (List<WorkingEnvironment>) workingEnvironmentRepository.findAll();}
}
