package com.objectcomputing.checkins.services.fixture;

import nu.studer.sample.tables.pojos.Team;

import java.util.UUID;

public interface TeamFixture extends RepositoryFixture{
    default Team createDeafultTeam() {
        return getTeamRepository().save(new Team(UUID.randomUUID().toString(),"Ninja","Warriors"));
    }

    default Team createAnotherDeafultTeam() {
        return getTeamRepository().save(new Team(UUID.randomUUID().toString(),"Coding","Warriors"));
    }
}
