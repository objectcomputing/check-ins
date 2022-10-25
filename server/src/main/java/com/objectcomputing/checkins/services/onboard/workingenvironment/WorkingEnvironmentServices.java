package com.objectcomputing.checkins.services.onboard.workingenvironment;

import java.util.List;
import java.util.UUID;

public interface WorkingEnvironmentServices {
    WorkingEnvironment getById(UUID id);

    WorkingEnvironment saveWorkingEnvironment(WorkingEnvironmentCreateDTO workingEnvironmentCreateDTO);

    WorkingEnvironment updateWorkingEnvironment(WorkingEnvironmentDTO workingEnvironmentDTO);

    Boolean deleteWorkingEnvironment(UUID id);

    List<WorkingEnvironment> findAll();
}
