package com.objectcomputing.checkins.services.fixture;

import com.objectcomputing.checkins.services.checkin_notes.CheckinNote;
import com.objectcomputing.checkins.services.checkins.CheckIn;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileEntity;

public interface CheckInNoteFixture extends RepositoryFixture {

    default CheckinNote createADeafultCheckInNote(CheckIn checkIn, MemberProfileEntity memberProfileEntity) {
        return getCheckInNoteRepository().save(new CheckinNote(checkIn.getId(), memberProfileEntity.getId(),"tests"));
    }


}
