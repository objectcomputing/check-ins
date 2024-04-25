package com.objectcomputing.checkins.services.checkins;

import java.util.Set;
import java.util.UUID;

import com.objectcomputing.checkins.services.permissions.Permission;


public interface CheckInServices {

    CheckIn save(CheckIn checkIn);

    CheckIn read(UUID id);

    CheckIn update(CheckIn checkinNote);

    Set<CheckIn> findByFields(UUID teamMemberId, UUID pdlId, Boolean completed);

    Boolean hasPermission(UUID memberId, Permission permission);

    Boolean accessGranted(UUID checkinId, UUID memberId);

    Boolean canViewAllCheckins(UUID memberId);

    Boolean canUpdateAllCheckins(UUID memberId);
}