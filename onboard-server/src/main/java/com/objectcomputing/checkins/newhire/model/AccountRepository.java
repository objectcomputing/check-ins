package com.objectcomputing.checkins.newhire.model;

import io.micronaut.data.repository.reactive.ReactorCrudRepository;

import java.util.UUID;

public interface AccountRepository extends ReactorCrudRepository<LoginAccount, UUID> {
}