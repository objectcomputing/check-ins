package com.objectcomputing.checkins.services.opportunities;

import java.util.ArrayList;
import java.util.UUID;
import java.util.Set;

public interface OpportunitiesService {

    Opportunities save(Opportunities opportunitiesResponse);

    Opportunities update(Opportunities opportunitiesResponse);

    void delete(UUID id);

    ArrayList<Opportunities> findByFields(String name, String description, UUID submittedBy);

}