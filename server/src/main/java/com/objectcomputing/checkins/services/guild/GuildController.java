package com.objectcomputing.checkins.services.guild;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Put;
import io.micronaut.http.annotation.Status;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.net.URI;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Controller("/services/guilds")
@ExecuteOn(TaskExecutors.BLOCKING)
@Secured(SecurityRule.IS_AUTHENTICATED)
@Tag(name = "guilds")
public class GuildController {

    private final GuildServices guildService;

    public GuildController(GuildServices guildService) {
        this.guildService = guildService;
    }

    /**
     * Create and save a new guild
     *
     * @param guild, {@link GuildCreateDTO}
     * @return {@link HttpResponse<GuildResponseDTO>}
     */
    @Post
    public HttpResponse<GuildResponseDTO> createAGuild(@Body @Valid GuildCreateDTO guild, HttpRequest<?> request) {
        GuildResponseDTO createdGuild = guildService.save(guild);
        return HttpResponse.created(createdGuild)
                .headers(headers -> headers.location(URI.create(String.format("%s/%s", request.getPath(), createdGuild.getId()))));
    }

    /**
     * Get guild based on id
     *
     * @param id of guild
     * @return {@link GuildResponseDTO guild matching id}
     */
    @Get("/{id}")
    public GuildResponseDTO readGuild(@NotNull UUID id) {
        return guildService.read(id);
    }

    /**
     * Find guild(s) given a combination of the following parameters
     *
     * @param name,     name of the guild
     * @param memberId, {@link UUID} of the member you wish to inquire in to which guilds they are a part of
     * @return {@link List < GuildResponseDTO > list of guilds}, return all guilds when no parameters filled in else
     * return all guilds that match the filled in params
     */
    @Get("/{?name,memberid}")
    public Set<GuildResponseDTO> findGuilds(@Nullable String name, @Nullable UUID memberId) {
        return guildService.findByFields(name, memberId);
    }

    /**
     * Update guild.
     *
     * @param guild, {@link GuildUpdateDTO}
     * @return {@link HttpResponse<GuildResponseDTO>}
     */
    @Put
    public HttpResponse<GuildResponseDTO> update(@Body @Valid GuildUpdateDTO guild, HttpRequest<?> request) {
        GuildResponseDTO updated = guildService.update(guild);
        return HttpResponse.ok(updated)
                .headers(headers -> headers.location(URI.create(String.format("%s/%s", request.getUri(), guild.getId()))));

    }

    /**
     * Delete Guild
     *
     * @param id, id of {@link GuildUpdateDTO} to delete
     */
    @Delete("/{id}")
    @Status(HttpStatus.OK)
    public void deleteGuild(@NotNull UUID id) {
        guildService.delete(id);
    }
}