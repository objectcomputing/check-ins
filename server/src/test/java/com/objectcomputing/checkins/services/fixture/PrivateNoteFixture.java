package com.objectcomputing.checkins.services.fixture;

import com.objectcomputing.checkins.services.private_notes.PrivateNote;
import com.objectcomputing.checkins.services.checkins.CheckIn;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;

public interface PrivateNoteFixture extends RepositoryFixture {

    default PrivateNote createADefaultPrivateNote(CheckIn checkIn, MemberProfile memberProfile) {
        return getPrivateNoteRepository().save(new PrivateNote(checkIn.getId(), memberProfile.getId(), "tests"));
    }
}
