package com.objectcomputing.checkins.services.fixture;

import com.objectcomputing.checkins.services.checkin_notes.CheckinNote;
import com.objectcomputing.checkins.services.checkins.CheckIn;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;

public interface CheckInNoteFixture extends RepositoryFixture {

    default CheckinNote createADefaultCheckInNote(CheckIn checkIn, MemberProfile memberProfile) {
        return getCheckInNoteRepository().save(new CheckinNote(checkIn.getId(), memberProfile.getId(), "tests"));
    }
}
