package com.objectcomputing.checkins.services.fixture;

import com.objectcomputing.checkins.services.guild.Guild;

import java.util.UUID;

public interface GuildFixture extends RepositoryFixture{
    default Guild createDefaultGuild() {
        return getGuildRepository().save(new Guild(UUID.randomUUID(), "Java", "Java Developers"));
    }

    default Guild createAnotherDefaultGuild() {
        return getGuildRepository().save(new Guild(UUID.randomUUID(), "JavaScript", "JavaScript Developers"));
    }
}
