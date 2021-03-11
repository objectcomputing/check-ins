package com.objectcomputing.checkins.services.tags;

import java.util.Set;
import java.util.UUID;

public interface TagServices {

    Tag read(UUID uuid);

    Tag save(Tag tag);

    Tag update(Tag tag);

    Set<Tag> findByFields(String name);

    void delete(UUID id);
}
