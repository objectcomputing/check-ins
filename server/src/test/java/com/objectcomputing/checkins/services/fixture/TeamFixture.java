package com.objectcomputing.checkins.services.fixture;

import com.objectcomputing.checkins.services.team.Team;

import java.util.UUID;

public interface TeamFixture extends RepositoryFixture{
    default Team createDeafultTeam() {
        return getTeamRepository().save(new Team(UUID.randomUUID(),"Ninja","Warriors"));
    }

    default Team createAnotherDeafultTeam() {
        return getTeamRepository().save(new Team(UUID.randomUUID(),"Coding","Warriors"));
    }
}
