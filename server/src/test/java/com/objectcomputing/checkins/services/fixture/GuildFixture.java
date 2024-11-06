package com.objectcomputing.checkins.services.fixture;

import com.objectcomputing.checkins.services.guild.Guild;
import com.objectcomputing.checkins.services.guild.GuildCreateDTO;
import com.objectcomputing.checkins.services.guild.GuildResponseDTO;
import com.objectcomputing.checkins.services.guild.GuildUpdateDTO;

import java.util.UUID;

public interface GuildFixture extends MemberProfileFixture, RepositoryFixture{

    String COMPASS_ADDRESS = "https://www.compass.objectcomputing.com/";

    default Guild createDefaultGuild() {
        return getGuildRepository().save(new Guild(UUID.randomUUID(), "Ninja", "Warriors", COMPASS_ADDRESS+"ninja_warriors/", false, true));
    }

    default Guild createAnotherDefaultGuild() {
        return getGuildRepository().save(new Guild(UUID.randomUUID(), "Coding", "Warriors", COMPASS_ADDRESS+"coding_warriors/", false, true));
    }

    default GuildCreateDTO createFromEntity(Guild entity) {
        return new GuildCreateDTO(entity.getName(), entity.getDescription(), entity.getLink(), false, true);
    }

    default GuildUpdateDTO updateFromEntity(Guild entity) {
        return new GuildUpdateDTO(entity.getId(), entity.getName(), entity.getDescription(),entity.getLink(), entity.isCommunity(), entity.isActive());
    }

    default GuildResponseDTO responseFromEntity(Guild entity) {
        return new GuildResponseDTO(entity.getId(), entity.getName(), entity.getDescription(),entity.getLink(), entity.isCommunity(), entity.isActive());
    }

    default Guild entityFromDTO(GuildUpdateDTO dto) {
        return new Guild(dto.getId(), dto.getName(), dto.getDescription(),dto.getLink(), dto.isCommunity(), dto.isActive());
    }

}
