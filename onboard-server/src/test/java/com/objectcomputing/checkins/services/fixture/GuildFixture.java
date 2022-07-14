package com.objectcomputing.checkins.services.fixture;

import com.objectcomputing.checkins.services.guild.Guild;
import com.objectcomputing.checkins.services.guild.GuildCreateDTO;
import com.objectcomputing.checkins.services.guild.GuildResponseDTO;
import com.objectcomputing.checkins.services.guild.GuildUpdateDTO;

import java.util.UUID;

public interface GuildFixture extends MemberProfileFixture, RepositoryFixture{

    String COMPASS_ADDRESS = "https://www.compass.objectcomputing.com/";

    default Guild createDefaultGuild() {
        return getGuildRepository().save(new Guild(UUID.randomUUID(), "Ninja", "Warriors", COMPASS_ADDRESS+"ninja_warriors/"));
    }

    default Guild createAnotherDefaultGuild() {
        return getGuildRepository().save(new Guild(UUID.randomUUID(), "Coding", "Warriors", COMPASS_ADDRESS+"coding_warriors/"));
    }

    default GuildCreateDTO createFromEntity(Guild entity) {
        return new GuildCreateDTO(entity.getName(), entity.getDescription(), entity.getLink());
    }

    default GuildUpdateDTO updateFromEntity(Guild entity) {
        return new GuildUpdateDTO(entity.getId(), entity.getName(), entity.getDescription(),entity.getLink());
    }

    default GuildResponseDTO responseFromEntity(Guild entity) {
        return new GuildResponseDTO(entity.getId(), entity.getName(), entity.getDescription(),entity.getLink());
    }

    default Guild entityFromDTO(GuildUpdateDTO dto) {
        return new Guild(dto.getId(), dto.getName(), dto.getDescription(),dto.getLink());
    }

}
