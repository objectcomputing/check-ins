package com.objectcomputing.checkins.services.onboard.workingenvironment;

import jakarta.inject.Singleton;

import java.util.*;

import com.objectcomputing.checkins.exceptions.NotFoundException;
import com.objectcomputing.checkins.services.onboardeecreate.newhire.model.NewHireAccountEntity;
import com.objectcomputing.checkins.services.onboardeecreate.newhire.model.NewHireAccountRepository;


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

        Optional<WorkingEnvironment> workingEnvironmentInformation = workingEnvironmentRepository.findById(id);

        if (workingEnvironmentInformation.isEmpty()) {
            throw new NotFoundException("No new employee background information for id " + id);
        }

        WorkingEnvironment found = workingEnvironmentInformation.get();
        if(found.getNewHireAccount() != null) {
            Optional<NewHireAccountEntity> newHire = newHireAccountRepository.findById(found.getNewHireAccount().getId());
            if (!newHire.isEmpty()) {
                found.setNewHireAccount(newHire.get());
            }
        }

        return found;
    }

    @Override
    public WorkingEnvironment saveWorkingEnvironment(WorkingEnvironmentCreateDTO workingEnvironmentCreateDTO) {
        NewHireAccountEntity newHire = newHireAccountRepository.findByEmailAddress(workingEnvironmentCreateDTO.getEmailAddress()).get();
        WorkingEnvironment workingEnvironment = buildNewWorkingEnvironmentEntity (newHire,workingEnvironmentCreateDTO);

        WorkingEnvironment found = workingEnvironmentRepository.save(workingEnvironment);
        if(found.getNewHireAccount() != null) {
            Optional<NewHireAccountEntity> account = newHireAccountRepository.findById(found.getNewHireAccount().getId());
            if (!account.isEmpty()) {
                found.setNewHireAccount(account.get());
            }
        }

        return found;
    }

    public WorkingEnvironment buildNewWorkingEnvironmentEntity(NewHireAccountEntity newHire, WorkingEnvironmentCreateDTO workingEnvironmentCreateDTO) {
        return new WorkingEnvironment( workingEnvironmentCreateDTO.getWorkLocation(),
                workingEnvironmentCreateDTO.getKeyType(), workingEnvironmentCreateDTO.getOsType(),
                workingEnvironmentCreateDTO.getAccessories(), workingEnvironmentCreateDTO.getOtherAccessories(),
                newHire);
    }

    @Override
    public WorkingEnvironment updateWorkingEnvironment(WorkingEnvironmentDTO workingEnvironmentDTO) {
        NewHireAccountEntity newHire = newHireAccountRepository.findByEmailAddress(workingEnvironmentDTO.getEmailAddress()).get();
        WorkingEnvironment workingEnvironment = buildWorkingEnvironmentEntity (newHire,workingEnvironmentDTO);
        return workingEnvironmentRepository.update(workingEnvironment);
    }

    private WorkingEnvironment buildWorkingEnvironmentEntity(NewHireAccountEntity newHire,
            WorkingEnvironmentDTO workingEnvironmentDTO) {
        return new WorkingEnvironment(workingEnvironmentDTO.getId(), workingEnvironmentDTO.getWorkLocation(),
                workingEnvironmentDTO.getKeyType(), workingEnvironmentDTO.getOsType(),
                workingEnvironmentDTO.getAccessories(), workingEnvironmentDTO.getOtherAccessories(),
                newHire);
    }

    @Override
    public Boolean deleteWorkingEnvironment(UUID id) {
        workingEnvironmentRepository.deleteById(id);
        return true;
    }

    public List<WorkingEnvironment> findAll() { return (List<WorkingEnvironment>) workingEnvironmentRepository.findAll();}
}
