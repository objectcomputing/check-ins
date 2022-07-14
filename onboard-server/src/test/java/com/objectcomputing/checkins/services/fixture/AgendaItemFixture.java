package com.objectcomputing.checkins.services.fixture;

import com.objectcomputing.checkins.services.agenda_item.AgendaItem;
import com.objectcomputing.checkins.services.checkins.CheckIn;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;

public interface AgendaItemFixture extends RepositoryFixture {

    default AgendaItem createADefaultAgendaItem(CheckIn checkIn, MemberProfile memberProfile) {
        return getAgendaItemRepository().save(new AgendaItem(checkIn.getId(), memberProfile.getId(), "tests"));
    }
}

