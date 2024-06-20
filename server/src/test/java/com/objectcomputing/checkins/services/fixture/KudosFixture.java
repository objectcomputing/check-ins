package com.objectcomputing.checkins.services.fixture;

import com.objectcomputing.checkins.services.kudos.Kudos;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;

import java.time.LocalDate;
import java.util.Map;

public interface KudosFixture extends RepositoryFixture {

    default Kudos createADefaultKudos() {
        return getKudosRepository().save(new Kudos());
    }
}
