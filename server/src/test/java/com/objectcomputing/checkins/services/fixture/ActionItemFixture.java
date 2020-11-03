package com.objectcomputing.checkins.services.fixture;

import com.objectcomputing.checkins.services.action_item.ActionItem;
import com.objectcomputing.checkins.services.checkins.CheckIn;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileEntity;

public interface ActionItemFixture extends RepositoryFixture {

    default ActionItem createADeafultActionItem(CheckIn checkIn, MemberProfileEntity memberProfileEntity) {
        return getActionItemRepository().save(new ActionItem(checkIn.getId(), memberProfileEntity.getId(),"tests"));
    }


}

