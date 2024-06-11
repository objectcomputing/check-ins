package com.objectcomputing.checkins.services.guild;

import com.objectcomputing.checkins.services.guild.member.GuildMemberResponseDTO;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileServices;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.*;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Controller("/services/guilds")
@ExecuteOn(TaskExecutors.BLOCKING)
@Secured(SecurityRule.IS_AUTHENTICATED)
@Tag(name = "guilds")
public class GuildController {

    private final GuildServices guildService;
    private final MemberProfileServices profileServices;

    public GuildController(GuildServices guildService, MemberProfileServices profileServices) {
        this.guildService = guildService;
        this.profileServices = profileServices;
    }

    /**
     * Create and save a new guild
     *
     * @param guild, {@link GuildCreateDTO}
     * @return {@link HttpResponse<GuildResponseDTO>}
     */
    @Post
    public Mono<HttpResponse<GuildResponseDTO>> createAGuild(@Body @Valid GuildCreateDTO guild, HttpRequest<?> request) {
        return Mono.fromCallable(() -> guildService.save(guild))
                .map(createdGuild -> HttpResponse.created(createdGuild)
                        .headers(headers -> headers.location(URI.create(String.format("%s/%s", request.getPath(), createdGuild.getId())))));
    }

    /**
     * Get guild based on id
     *
     * @param id of guild
     * @return {@link GuildResponseDTO guild matching id}
     */

    @Get("/{id}")
    public Mono<HttpResponse<GuildResponseDTO>> readGuild(@NotNull UUID id) {
        return Mono.fromCallable(() -> guildService.read(id))
                .map(HttpResponse::ok);
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
    public Mono<HttpResponse<Set<GuildResponseDTO>>> findGuilds(@Nullable String name, @Nullable UUID memberId) {
        return Mono.fromCallable(() -> guildService.findByFields(name, memberId))
                .map(HttpResponse::ok);
    }

    /**
     * Update guild.
     *
     * @param guild, {@link GuildUpdateDTO}
     * @return {@link HttpResponse<GuildResponseDTO>}
     */
    @Put
    public Mono<HttpResponse<GuildResponseDTO>> update(@Body @Valid GuildUpdateDTO guild, HttpRequest<?> request) {
        return Mono.fromCallable(() -> guildService.update(guild))
                .map(updated -> HttpResponse.ok(updated)
                        .headers(headers -> headers.location(URI.create(String.format("%s/%s", request.getUri(), guild.getId())))));

    }

    /**
     * Delete Guild
     *
     * @param id, id of {@link GuildUpdateDTO} to delete
     * @return http ok response
     */
    @Delete("/{id}")
    public Mono<HttpResponse<Object>> deleteGuild(@NotNull UUID id) {
        return Mono.fromCallable(() -> guildService.delete(id))
                .map(success -> HttpResponse.ok());
    }

}