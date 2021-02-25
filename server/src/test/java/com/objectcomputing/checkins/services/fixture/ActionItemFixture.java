package com.objectcomputing.checkins.services.fixture;

import com.objectcomputing.checkins.services.action_item.ActionItem;
import com.objectcomputing.checkins.services.checkins.CheckIn;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;

public interface ActionItemFixture extends RepositoryFixture {

    default ActionItem createADefaultActionItem(CheckIn checkIn, MemberProfile memberProfile) {
        return getActionItemRepository().save(new ActionItem(checkIn.getId(), memberProfile.getId(), "tests"));
    }
}

