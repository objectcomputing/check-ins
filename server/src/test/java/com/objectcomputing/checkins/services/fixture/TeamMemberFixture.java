package com.objectcomputing.checkins.services.fixture;

import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.team.Team;
import com.objectcomputing.checkins.services.team.TeamCreateDTO;
import com.objectcomputing.checkins.services.team.TeamUpdateDTO;
import com.objectcomputing.checkins.services.team.member.TeamMember;
import com.objectcomputing.checkins.services.team.member.TeamMemberCreateDTO;
import com.objectcomputing.checkins.services.team.member.TeamMemberResponseDTO;


public interface TeamMemberFixture extends RepositoryFixture{
    default TeamMember createDefaultTeamMember(Team teamEntity, MemberProfile memberProfile) {
        return getTeamMemberRepository().save(new TeamMember(null, teamEntity.getId(), memberProfile.getId(), false));
    }

    default TeamMember createLeadTeamMember(Team teamEntity, MemberProfile memberProfile) {
        return getTeamMemberRepository().save(new TeamMember(null, teamEntity.getId(), memberProfile.getId(), true));
    }

    default TeamCreateDTO.TeamMemberCreateDTO createDefaultTeamMemberDto(MemberProfile memberProfile, Boolean lead) {
        return new TeamCreateDTO.TeamMemberCreateDTO(memberProfile.getId(), lead);
    }

    default TeamUpdateDTO.TeamMemberUpdateDTO updateDefaultTeamMemberDto(Team entity, MemberProfile memberProfile, Boolean lead) {
        return new TeamUpdateDTO.TeamMemberUpdateDTO(null, entity.getId(), memberProfile.getId(), lead);
    }

    default TeamMemberCreateDTO createDefaultTeamMemberDto(Team teamEntity, MemberProfile memberProfile, Boolean lead) {
        return new TeamMemberCreateDTO(teamEntity.getId(), memberProfile.getId(), true);
    }

    default TeamMemberResponseDTO dtoFromEntity(TeamMember memberEntity, MemberProfile memberProfile) {
        return new TeamMemberResponseDTO(memberEntity.getId(), memberProfile.getFirstName(), memberProfile.getLastName(),
                memberProfile.getId(), memberEntity.isLead());
    }
}
