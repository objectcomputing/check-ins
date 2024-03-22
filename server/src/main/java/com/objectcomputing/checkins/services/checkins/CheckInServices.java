package com.objectcomputing.checkins.services.checkins;

import java.util.Set;
import java.util.UUID;

public interface CheckInServices {

    CheckIn save(CheckIn checkIn);

    CheckIn read(UUID id);

    CheckIn update(CheckIn checkinNote);

    Set<CheckIn> findByFields(UUID teamMemberId, UUID pdlId, Boolean completed);

    Boolean hasElevatedAccessPermission(UUID memberId);

    Boolean accessGranted(UUID checkinId, UUID memberId);
}