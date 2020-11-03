package com.objectcomputing.checkins.services.fixture;

import com.objectcomputing.checkins.services.agenda_item.AgendaItem;
import com.objectcomputing.checkins.services.checkins.CheckIn;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileEntity;

public interface AgendaItemFixture extends RepositoryFixture {

    default AgendaItem createADeafultAgendaItem(CheckIn checkIn, MemberProfileEntity memberProfileEntity) {
        return getAgendaItemRepository().save(new AgendaItem(checkIn.getId(), memberProfileEntity.getId(),"tests"));
    }


}

