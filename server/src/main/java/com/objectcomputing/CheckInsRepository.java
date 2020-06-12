package com.objectcomputing;

import java.util.List;
import java.util.UUID;

import com.objectcomputing.checkins.CheckIns;

import io.micronaut.data.repository.CrudRepository;

public interface CheckInsRepository extends CrudRepository<CheckIns,UUID>{

    List<CheckIns> findByName(UUID teamMemberId);
    List<CheckIns> findByPdlId(UUID pdlId);
    List<CheckIns> findByTargetQuarter(String targetYear,String targetQtr);
    List<CheckIns> findAll();

}