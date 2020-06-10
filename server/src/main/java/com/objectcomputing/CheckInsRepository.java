package com.objectcomputing;

import java.util.List;
import java.util.UUID;

import com.objectcomputing.checkins.CheckIns;

public interface CheckInsRepository {

    List<CheckIns> findByName(String teamMember);
    List<CheckIns> findByPdlId(UUID pdlId);
    List<CheckIns> findByTargetQuarter(String targetYear,String targetQtr);
    List<CheckIns> findAll();
    CheckIns createMemberCheckIn(CheckIns checkIns);
    CheckIns update(CheckIns checkIns);

}