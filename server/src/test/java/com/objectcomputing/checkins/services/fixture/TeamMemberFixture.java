package com.objectcomputing.checkins.services.fixture;

import com.objectcomputing.checkins.services.memberprofile.MemberProfileEntity;
import com.objectcomputing.checkins.services.team.member.TeamMemberDTO;
import nu.studer.sample.tables.pojos.Team;
import nu.studer.sample.tables.pojos.TeamMember;


public interface TeamMemberFixture extends RepositoryFixture{
    default TeamMember createDeafultTeamMember(Team teamEntity, MemberProfileEntity memberProfileEntity) {
        return getTeamMemberRepository().save(new TeamMember(null, teamEntity.getId(), memberProfileEntity.getId().toString(),false));
    }

    default TeamMember createLeadTeamMember(Team teamEntity, MemberProfileEntity memberProfileEntity) {
        return getTeamMemberRepository().save(new TeamMember(null, teamEntity.getId(), memberProfileEntity.getId().toString(),true));
    }

    default TeamMemberDTO createDefaultTeamMemberDto(Team teamEntity, MemberProfileEntity memberProfileEntity) {
        return dtoFromEntity(createDeafultTeamMember(teamEntity, memberProfileEntity), memberProfileEntity);
    }

    default TeamMemberDTO dtoFromEntity(TeamMember memberEntity, MemberProfileEntity memberProfileEntity) {
        return new TeamMemberDTO(memberEntity.getId(), memberProfileEntity.getName(), memberEntity.getLead());
    }
}
