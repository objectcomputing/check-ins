package com.objectcomputing.checkins.services.opportunities;

import java.util.UUID;
import java.util.Set;

public interface OpportunitiesService {

    Set<Opportunities> readAll();

    Opportunities save(Opportunities opportunitiesResponse);

    Opportunities update(Opportunities opportunitiesResponse);

    void delete(UUID id);

    Set<Opportunities> findByFields(String name, String description);

}