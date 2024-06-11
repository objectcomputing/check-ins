package com.objectcomputing.checkins.services.volunteering;

import io.micronaut.data.annotation.Query;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface VolunteeringRelationshipRepository extends CrudRepository<VolunteeringRelationship, UUID> {

    @Query("""
            SELECT rel.*
                FROM volunteering_relationship AS rel
                    LEFT JOIN volunteering_organization AS org USING(organization_id)
            WHERE rel.organization_id = :organizationId
              AND rel.member_id = :memberId
              AND (rel.is_active = TRUE OR :includeDeactivated = TRUE)
              AND (org.is_active = TRUE OR :includeDeactivated = TRUE)
            ORDER BY rel.start_date, org.name""")
    List<VolunteeringRelationship> findByMemberIdAndOrganizationId(UUID memberId, UUID organizationId, boolean includeDeactivated);

    @Query("""
            SELECT rel.*
                FROM volunteering_relationship AS rel
                    LEFT JOIN volunteering_organization AS org USING(organization_id)
            WHERE rel.member_id = :memberId
              AND (rel.is_active = TRUE OR :includeDeactivated = TRUE)
              AND (org.is_active = TRUE OR :includeDeactivated = TRUE)
            ORDER BY rel.start_date, org.name""")
    List<VolunteeringRelationship> findByMemberId(UUID memberId, boolean includeDeactivated);

    @Query("""
            SELECT rel.*
                FROM volunteering_relationship AS rel
                    LEFT JOIN volunteering_organization AS org USING(organization_id)
            WHERE rel.organization_id = :organizationId
              AND (rel.is_active = TRUE OR :includeDeactivated = TRUE)
              AND (org.is_active = TRUE OR :includeDeactivated = TRUE)
            ORDER BY rel.start_date, org.name""")
    List<VolunteeringRelationship> findByOrganizationId(UUID organizationId, boolean includeDeactivated);

    @Query("""
            SELECT rel.*
                FROM volunteering_relationship AS rel
                    LEFT JOIN volunteering_organization AS org USING(organization_id)
            WHERE (rel.is_active = TRUE OR :includeDeactivated = TRUE)
              AND (org.is_active = TRUE OR :includeDeactivated = TRUE)
            ORDER BY rel.start_date, org.name""")
    List<VolunteeringRelationship> findAll(boolean includeDeactivated);
}
