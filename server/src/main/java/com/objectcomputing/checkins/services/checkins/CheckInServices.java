package com.objectcomputing.checkins.services.checkins;

import com.objectcomputing.checkins.services.permissions.Permission;

import java.util.Set;
import java.util.UUID;


public interface CheckInServices {

    CheckIn save(CheckIn checkIn);

    CheckIn read(UUID id);

    CheckIn update(CheckIn checkinNote);

    Set<CheckIn> findByFields(UUID teamMemberId, UUID pdlId, Boolean completed);

    boolean hasPermission(UUID memberId, Permission permission);

    boolean accessGranted(UUID checkinId, UUID memberId);

    boolean doesUserHaveViewAccess(UUID currentUserId, UUID checkinId, UUID createdById);

    boolean canViewAllCheckins(UUID memberId);

    boolean canUpdateAllCheckins(UUID memberId);
}