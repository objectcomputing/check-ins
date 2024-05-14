package com.objectcomputing.checkins.services.employee_hours;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface EmployeeHoursRepository extends CrudRepository<EmployeeHours,UUID> {

    List<EmployeeHours> findByEmployeeId(@NotNull String employeeId);
    List<EmployeeHours> findAll();

    @Override
    <S extends EmployeeHours> List<S> saveAll(@Valid @NotNull Iterable<S> entities);

    @Override
    <S extends EmployeeHours> S save(@Valid @NotNull @NonNull S entity);


}


