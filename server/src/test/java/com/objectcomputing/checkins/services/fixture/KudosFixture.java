package com.objectcomputing.checkins.services.fixture;

import com.objectcomputing.checkins.services.kudos.Kudos;
import com.objectcomputing.checkins.services.kudos.kudos_recipient.KudosRecipient;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.team.Team;

import java.time.LocalDate;

public interface KudosFixture extends RepositoryFixture {
    default Kudos createMemberKudos(MemberProfile sender) {
        return getKudosRepository().save(new Kudos("message", sender.getId(), true));
    }

    default Kudos createSecondMemberKudos(MemberProfile sender) {
        return getKudosRepository().save(new Kudos("message 2", sender.getId(), true));
    }
    default KudosRecipient createKudosRecipient(Kudos kudos, MemberProfile member) {
        return getKudosRecipientRepository().save(new KudosRecipient(kudos.getId(), member.getId()));
    }

    default Kudos createTeamKudos(MemberProfile sender, Team kudosTeam) {
        return getKudosRepository().save(new Kudos("message", sender.getId(), kudosTeam.getId()));
    }
}
