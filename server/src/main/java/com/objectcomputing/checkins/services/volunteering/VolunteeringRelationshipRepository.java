package com.objectcomputing.checkins.services.volunteering;

import io.micronaut.data.annotation.Query;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;
import jakarta.annotation.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface VolunteeringRelationshipRepository extends CrudRepository<VolunteeringRelationship, UUID> {

    @Query(value = """
            SELECT rel.*
                FROM volunteering_relationship AS rel
                    JOIN volunteering_organization AS org USING(organization_id)
            WHERE rel.organization_id = :organizationId
              AND rel.member_id = :memberId
              AND (rel.is_active = TRUE OR :includeDeactivated = TRUE)
              AND (org.is_active = TRUE OR :includeDeactivated = TRUE)
            ORDER BY org.name""", nativeQuery = true)
    List<VolunteeringRelationship> findByMemberIdAndOrganizationId(UUID memberId, UUID organizationId, boolean includeDeactivated);

    @Query(value = """
            SELECT rel.*
                FROM volunteering_relationship AS rel
                    JOIN volunteering_organization AS org USING(organization_id)
            WHERE rel.member_id = :memberId
              AND (rel.is_active = TRUE OR :includeDeactivated = TRUE)
              AND (org.is_active = TRUE OR :includeDeactivated = TRUE)
            ORDER BY org.name""", nativeQuery = true)
    List<VolunteeringRelationship> findByMemberId(UUID memberId, boolean includeDeactivated);

    @Query(value = """
            SELECT rel.*
                FROM volunteering_relationship AS rel
                    JOIN volunteering_organization AS org USING(organization_id)
            WHERE rel.organization_id = :organizationId
              AND (rel.is_active = TRUE OR :includeDeactivated = TRUE)
              AND (org.is_active = TRUE OR :includeDeactivated = TRUE)
            ORDER BY org.name""", nativeQuery = true)
    List<VolunteeringRelationship> findByOrganizationId(UUID organizationId, boolean includeDeactivated);

    @Query(value = """
            SELECT rel.*
                FROM volunteering_relationship AS rel
                    JOIN volunteering_organization AS org USING(organization_id)
            WHERE (rel.is_active = TRUE OR :includeDeactivated = TRUE)
              AND (org.is_active = TRUE OR :includeDeactivated = TRUE)
            ORDER BY org.name""", nativeQuery = true)
    List<VolunteeringRelationship> findAll(boolean includeDeactivated);

    @Query(value = """
            SELECT rel.*
                FROM volunteering_event AS event
                    JOIN volunteering_relationship AS rel USING(relationship_id)
                WHERE event.event_id::uuid = :eventId""", nativeQuery = true)
    Optional<VolunteeringRelationship> getRelationshipForEvent(@Nullable UUID eventId);

    Optional<VolunteeringRelationship> findById(@Nullable UUID eventId);
}
