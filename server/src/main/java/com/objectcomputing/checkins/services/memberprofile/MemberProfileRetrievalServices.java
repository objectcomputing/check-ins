package com.objectcomputing.checkins.services.memberprofile;

import jakarta.validation.constraints.NotNull;
import java.util.Optional;
import java.util.UUID;

/**
 * This acts as a microservice for retrieving MemberProfile data
 */
public interface MemberProfileRetrievalServices {

    Optional<MemberProfile> getById(UUID id);

    Optional<MemberProfile> findByWorkEmail(@NotNull String workEmail);

    boolean existsById(UUID id);
}