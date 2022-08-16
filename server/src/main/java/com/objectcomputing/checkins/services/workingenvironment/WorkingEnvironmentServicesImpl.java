package com.objectcomputing.checkins.services.workingenvironment;

import com.objectcomputing.checkins.exceptions.NotFoundException;
import jakarta.inject.Singleton;

import java.util.*;

@Singleton
public class WorkingEnvironmentServicesImpl implements WorkingEnvironmentServices {
    private final WorkingEnvironmentRepository workingEnvironmentRepository;

    public WorkingEnvironmentServicesImpl(WorkingEnvironmentRepository workingEnvironmentRepository) {
        this.workingEnvironmentRepository = workingEnvironmentRepository;
    }

    @Override
    public WorkingEnvironment getById(UUID id) {
        Optional<WorkingEnvironment> workingEnvironment = workingEnvironmentRepository.findById(id);
        if (workingEnvironment.isEmpty()) {
            throw new NotFoundException("No new working environment info for id " + id);
        }
        return workingEnvironment.get();
    }

    @Override
    public Set<WorkingEnvironment> findByValues(UUID id, String workLocation, String keyType, String osType, String accessories, String otherAccessories) {
        HashSet<WorkingEnvironment> working_environment = new HashSet<>(workingEnvironmentRepository.search(id, workLocation, keyType, osType, accessories, otherAccessories));
        return working_environment;
    }

    @Override
    public WorkingEnvironment saveWorkingEnvironment(WorkingEnvironment workingEnvironment) {
        if(workingEnvironment.getId() == null) {
            return workingEnvironmentRepository.save(workingEnvironment);
        }
        return workingEnvironmentRepository.update(workingEnvironment);
    }

    @Override
    public Boolean deleteWorkingEnvironment(UUID id) {
        workingEnvironmentRepository.deleteById(id);
        return true;
    }
}
