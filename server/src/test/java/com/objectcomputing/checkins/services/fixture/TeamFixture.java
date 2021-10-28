package com.objectcomputing.checkins.services.fixture;

import com.objectcomputing.checkins.services.team.Team;
import com.objectcomputing.checkins.services.team.TeamCreateDTO;
import com.objectcomputing.checkins.services.team.TeamResponseDTO;
import com.objectcomputing.checkins.services.team.TeamUpdateDTO;

import java.util.UUID;

public interface TeamFixture extends MemberProfileFixture, RepositoryFixture {

    default Team createDefaultTeam() {
        return getTeamRepository().save(new Team(UUID.randomUUID(), "Ninja", "Warriors"));
    }

    default Team createAnotherDefaultTeam() {
        return getTeamRepository().save(new Team(UUID.randomUUID(), "Coding", "Warriors"));
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
        return new Team(dto.getId(), dto.getName(), dto.getDescription());
    }
}
