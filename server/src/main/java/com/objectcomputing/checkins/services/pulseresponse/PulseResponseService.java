package com.objectcomputing.checkins.services.pulseresponse;

import java.util.UUID;
import java.util.Set;

public interface PulseResponseService {

    Set<PulseResponse> read(UUID checkinsId);

    PulseResponse save(PulseResponse pulseResponse);

    PulseResponse update(PulseResponse pulseResponse);

    void delete(UUID checkinsId);
}