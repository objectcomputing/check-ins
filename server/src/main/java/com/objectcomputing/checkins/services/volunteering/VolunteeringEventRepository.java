package com.objectcomputing.checkins.services.volunteering;

import io.micronaut.data.annotation.Query;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface VolunteeringEventRepository extends CrudRepository<VolunteeringEvent, UUID> {

    @Query("""
            SELECT event.*
                FROM volunteering_event AS event
                    JOIN volunteering_relationship AS rel USING(relationship_id)
                    JOIN volunteering_organization AS org USING(organization_id)
                WHERE rel.member_id::uuid = :memberId
                  AND (rel.is_active = TRUE OR :includeDeactivated = TRUE)
                  AND (org.is_active = TRUE OR :includeDeactivated = TRUE)
                ORDER BY event.event_date, org.name, event.hours DESC""")
    List<VolunteeringEvent> findByMemberId(@NotNull UUID memberId, boolean includeDeactivated);

    @Query("""
            SELECT event.*
                FROM volunteering_event AS event
                    JOIN volunteering_relationship AS rel USING(relationship_id)
                    JOIN volunteering_organization AS org USING(organization_id)
                WHERE org.organization_id::uuid = :organizationId
                  AND (rel.is_active = TRUE OR :includeDeactivated = TRUE)
                  AND (org.is_active = TRUE OR :includeDeactivated = TRUE)
                ORDER BY event.event_date, org.name, event.hours DESC""")
    List<VolunteeringEvent> findByOrganizationId(@NotNull UUID organizationId, boolean includeDeactivated);

    @Query("""
            SELECT event.*
                FROM volunteering_event AS event
                    JOIN volunteering_relationship AS rel USING(relationship_id)
                    JOIN volunteering_organization AS org USING(organization_id)
                WHERE rel.member_id::uuid = :memberId
                  AND org.organization_id::uuid = :organizationId
                  AND (rel.is_active = TRUE OR :includeDeactivated = TRUE)
                  AND (org.is_active = TRUE OR :includeDeactivated = TRUE)
                ORDER BY event.event_date, org.name, event.hours DESC""")
    List<VolunteeringEvent> findByMemberIdAndOrganizationId(@NotNull UUID memberId, @NotNull UUID organizationId, boolean includeDeactivated);

    @Query("""
            SELECT event.*
                FROM volunteering_event AS event
                    JOIN volunteering_relationship AS rel USING(relationship_id)
                    JOIN volunteering_organization AS org USING(organization_id)
                WHERE (rel.is_active = TRUE OR :includeDeactivated = TRUE)
                  AND (org.is_active = TRUE OR :includeDeactivated = TRUE)
                ORDER BY event.event_date, org.name, event.hours DESC""")
    List<VolunteeringEvent> findAll(boolean includeDeactivated);
}
