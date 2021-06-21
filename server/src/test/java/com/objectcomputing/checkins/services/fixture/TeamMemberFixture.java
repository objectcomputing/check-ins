package com.objectcomputing.checkins.services.fixture;

import com.objectcomputing.checkins.services.guild.GuildCreateDTO;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.team.Team;
import com.objectcomputing.checkins.services.team.TeamCreateDTO;
import com.objectcomputing.checkins.services.team.TeamUpdateDTO;
import com.objectcomputing.checkins.services.team.member.TeamMember;
import com.objectcomputing.checkins.services.team.member.TeamMemberCreateDTO;
import com.objectcomputing.checkins.services.team.member.TeamMemberResponseDTO;
import com.objectcomputing.checkins.services.team.member.TeamMemberUpdateDTO;


public interface TeamMemberFixture extends RepositoryFixture{
    default TeamMember createDefaultTeamMember(Team teamEntity, MemberProfile memberProfile) {
        return getTeamMemberRepository().save(new TeamMember(null, teamEntity.getId(), memberProfile.getId(), false));
    }

    default TeamMember createLeadTeamMember(Team teamEntity, MemberProfile memberProfile) {
        return getTeamMemberRepository().save(new TeamMember(null, teamEntity.getId(), memberProfile.getId(), true));
    }

    default TeamMemberResponseDTO createDefaultTeamMemberDto(Team teamEntity, MemberProfile memberProfile) {
        return dtoFromEntity(createDefaultTeamMember(teamEntity, memberProfile), memberProfile);
    }

    default TeamMemberUpdateDTO updateDefaultTeamMemberDto(Team teamEntity, MemberProfile memberProfile, Boolean lead) {
        return new TeamMemberUpdateDTO(null, teamEntity.getId(), memberProfile.getId(), true);
    }

    default TeamMemberCreateDTO createDefaultTeamMemberDto(Team teamEntity, MemberProfile memberProfile, Boolean lead) {
        return new TeamMemberCreateDTO(teamEntity.getId(), memberProfile.getId(), true);
    }

    default TeamMemberResponseDTO dtoFromEntity(TeamMember memberEntity, MemberProfile memberProfile) {
        return new TeamMemberResponseDTO(memberEntity.getId(), memberProfile.getFirstName(), memberProfile.getLastName(),
                memberProfile.getId(), memberEntity.isLead());
    }
}
