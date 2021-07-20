package com.objectcomputing.checkins.services.frozen_template;


import java.util.UUID;

public interface FrozenTemplateServices {

    FrozenTemplate save(FrozenTemplate ft);

    FrozenTemplate getById(UUID id);

    FrozenTemplate findByValues(UUID requestId);
}
