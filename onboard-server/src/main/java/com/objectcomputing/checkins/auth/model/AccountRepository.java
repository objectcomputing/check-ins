package com.objectcomputing.checkins.auth.model;

import com.objectcomputing.geoai.core.account.Account;
import io.micronaut.data.repository.CrudRepository;
import io.micronaut.data.repository.PageableRepository;
import io.micronaut.data.repository.reactive.ReactorCrudRepository;

import java.util.UUID;

public interface AccountRepository<T extends Account> extends ReactorCrudRepository<T, UUID> {
}