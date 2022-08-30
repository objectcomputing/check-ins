package com.objectcomputing.checkins.services.kudos.kudos_recipient;

import io.micronaut.data.repository.CrudRepository;

import java.util.UUID;

public interface KudosRecipientRepository extends CrudRepository<KudosRecipient, UUID> {

}
