package com.objectcomputing.checkins.services.fixture;

import com.objectcomputing.checkins.services.tags.Tag;

public interface TagFixture extends RepositoryFixture {

    default Tag createADefaultTag() {
        return getTagRepository().save(new Tag("test tag"));
    }

    default Tag createASecondaryTag() {
        return getTagRepository().save(new Tag("test tag 2"));
    }
}
