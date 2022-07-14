package com.objectcomputing.checkins.services.fixture;

import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.tags.entityTag.EntityTag;
import com.objectcomputing.checkins.services.tags.Tag;
import com.objectcomputing.checkins.services.tags.entityTag.EntityTag.EntityType;

public interface EntityTagFixture extends RepositoryFixture{

    default EntityTag createADefaultEntityTag(MemberProfile memberProfile, Tag tag, EntityType type) {
        return getEntityTagRepository().save(new EntityTag(memberProfile.getId(), tag.getId(), type));
    }
}
