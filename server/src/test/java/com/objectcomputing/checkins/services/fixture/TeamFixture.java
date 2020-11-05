package com.objectcomputing.checkins.services.fixture;

import com.objectcomputing.checkins.services.team.TeamCreateDTO;
import com.objectcomputing.checkins.services.team.TeamResponseDTO;
import com.objectcomputing.checkins.services.team.TeamUpdateDTO;
import nu.studer.sample.tables.pojos.Team;

import java.util.UUID;

public interface TeamFixture extends MemberProfileFixture, RepositoryFixture{

    default Team createDeafultTeam() {
        return getTeamRepository().save(new Team(UUID.randomUUID().toString(),"Ninja","Warriors"));
    }

    default Team createAnotherDeafultTeam() {
        return getTeamRepository().save(new Team(UUID.randomUUID().toString(),"Coding","Warriors"));
    }

    default TeamCreateDTO createFromEntity(Team entity) {
        return new TeamCreateDTO(entity.getName(), entity.getDescription());
    }

    default TeamUpdateDTO updateFromEntity(Team entity) {
        return new TeamUpdateDTO(entity.getId(), entity.getName(), entity.getDescription());
    }

    default TeamResponseDTO responseFromEntity(Team entity) {
        return new TeamResponseDTO(entity.getId(), entity.getName(), entity.getDescription());
    }

    default Team entityFromDTO(TeamUpdateDTO dto) {
        return new Team(dto.getId().toString(), dto.getName(), dto.getDescription());
    }
}
