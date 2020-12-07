package com.objectcomputing.checkins.services.fixture;

import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.team.Team;
import com.objectcomputing.checkins.services.team.member.TeamMember;
import com.objectcomputing.checkins.services.team.member.TeamMemberResponseDTO;


public interface TeamMemberFixture extends RepositoryFixture{
    default TeamMember createDeafultTeamMember(Team teamEntity, MemberProfile memberProfile) {
        return getTeamMemberRepository().save(new TeamMember(null, teamEntity.getId(), memberProfile.getId(),false));
    }

    default TeamMember createLeadTeamMember(Team teamEntity, MemberProfile memberProfile) {
        return getTeamMemberRepository().save(new TeamMember(null, teamEntity.getId(), memberProfile.getId(),true));
    }

    default TeamMemberResponseDTO createDefaultTeamMemberDto(Team teamEntity, MemberProfile memberProfile) {
        return dtoFromEntity(createDeafultTeamMember(teamEntity, memberProfile), memberProfile);
    }

    default TeamMemberResponseDTO dtoFromEntity(TeamMember memberEntity, MemberProfile memberProfile) {
        return new TeamMemberResponseDTO(memberEntity.getId(), memberProfile.getName(), memberProfile.getId(), memberEntity.isLead());
    }
}
