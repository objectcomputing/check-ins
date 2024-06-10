package com.objectcomputing.checkins.services.volunteering;

import io.micronaut.core.annotation.AnnotationMetadata;
import io.micronaut.data.annotation.Query;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface VolunteeringOrganizationRepository extends CrudRepository<VolunteeringOrganization, UUID> {

    Optional<VolunteeringOrganization> getByName(String name);

    @Query("""
           SELECT org.*
             FROM volunteering_organization AS org
               WHERE org.is_active = TRUE OR :includeDeactivated = TRUE
             ORDER BY org.name""")
    List<VolunteeringOrganization> findAll(boolean includeDeactivated);
}