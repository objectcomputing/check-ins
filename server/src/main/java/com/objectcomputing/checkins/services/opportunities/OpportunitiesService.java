package com.objectcomputing.checkins.services.opportunities;

import java.util.List;
import java.util.UUID;

public interface OpportunitiesService {

    Opportunities save(Opportunities opportunitiesResponse);

    Opportunities update(Opportunities opportunitiesResponse);

    void delete(UUID id);

    List<Opportunities> findByFields(String name, String description, UUID submittedBy);

}