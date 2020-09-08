package com.objectcomputing.checkins.services.fixture;

import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.team.Team;
import com.objectcomputing.checkins.services.team.member.TeamMember;


public interface TeamMemberFixture extends RepositoryFixture{
    default TeamMember createDeafultTeamMember(Team team, MemberProfile memberProfile) {
        return getTeamMemberRepository().save(new TeamMember(team.getId(),memberProfile.getId(),false));
    }
}
