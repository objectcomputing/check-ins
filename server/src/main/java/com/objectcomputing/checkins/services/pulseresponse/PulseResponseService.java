package com.objectcomputing.checkins.services.pulseresponse;

import java.util.UUID;
import java.util.Set;
import java.time.LocalDate;

public interface PulseResponseService {

    PulseResponse read(UUID id);

    PulseResponse save(PulseResponse pulseResponse);
    PulseResponse unsecureSave(PulseResponse pulseResponse);

    PulseResponse update(PulseResponse pulseResponse);

    Set<PulseResponse> findByFields(UUID teamMemberId, LocalDate dateFrom, LocalDate dateTo);
}
