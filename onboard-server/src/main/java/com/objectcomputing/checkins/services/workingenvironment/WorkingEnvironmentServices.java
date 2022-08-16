package com.objectcomputing.checkins.services.workingenvironment;

import java.util.Set;
import java.util.UUID;

public interface WorkingEnvironmentServices {
    WorkingEnvironment getById(UUID id);

    Set<WorkingEnvironment> findByValues(UUID id, String workLocation, String keyType, String osType,
            String accessories, String otherAccessories);

    WorkingEnvironment saveWorkingEnvironment(WorkingEnvironment workingEnvironment);

    Boolean deleteWorkingEnvironment(UUID id);
}
