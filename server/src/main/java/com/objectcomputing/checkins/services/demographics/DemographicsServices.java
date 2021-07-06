package com.objectcomputing.checkins.services.demographics;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface DemographicsServices {

    Demographics getById(UUID id);

    Set<Demographics> findByValues(UUID memberId,
                                   String gender,
                                   String degreeLevel,
                                   Integer industryTenure,
                                   Boolean personOfColor,
                                   Boolean veteran,
                                   Integer militaryTenure,
                                   String militaryBranch);

    Demographics updateDemographics(Demographics demographics);

    Demographics saveDemographics(Demographics demographics);

    List<Demographics> findAll();

    Boolean deleteDemographics(UUID id);
}
