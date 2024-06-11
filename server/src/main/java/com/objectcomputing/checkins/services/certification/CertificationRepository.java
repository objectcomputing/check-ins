package com.objectcomputing.checkins.services.certification;

import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface CertificationRepository extends CrudRepository<Certification, UUID> {

    Optional<Certification> getByName(String name);

    List<Certification> findAllOrderByNameAsc();
}
