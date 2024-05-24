package com.objectcomputing.checkins.services.guild.member;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
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

@Controller("/services/guilds/members")
@ExecuteOn(TaskExecutors.BLOCKING)
@Secured(SecurityRule.IS_AUTHENTICATED)
@Tag(name = "guild-member")
public class GuildMemberController {

    private final GuildMemberServices guildMemberServices;

    public GuildMemberController(GuildMemberServices guildMemberServices) {
        this.guildMemberServices = guildMemberServices;
    }

    /**
     * Create and save a new guildMember.
     *
     * @param guildMember, {@link GuildMemberResponseDTO}
     * @return {@link HttpResponse <GuildMember>}
     */
    @Post
    public HttpResponse<GuildMember> createMembers(@Body @Valid GuildMemberCreateDTO guildMember,
                                                  HttpRequest<?> request) {
        GuildMember newGuildMember = guildMemberServices.save(new GuildMember(guildMember.getGuildId(),
                guildMember.getMemberId(), guildMember.getLead()));
        return HttpResponse
                .created(newGuildMember)
                .headers(headers -> headers.location(
                        URI.create(String.format("%s/%s", request.getPath(), newGuildMember.getId()))));
    }

    /**
     * Update guildMember.
     *
     * @param guildMember, {@link GuildMember}
     * @return {@link HttpResponse<GuildMember>}
     */
    @Put
    public HttpResponse<?> updateMembers(@Body @Valid GuildMemberUpdateDTO guildMember, HttpRequest<?> request) {
        GuildMember updatedGuildMember = guildMemberServices.update(new GuildMember(guildMember.getId(), guildMember.getGuildId(), guildMember.getMemberId(), guildMember.getLead()));
        return HttpResponse
                .ok()
                .headers(headers -> headers.location(
                        URI.create(String.format("%s/%s", request.getPath(), updatedGuildMember.getId()))))
                .body(updatedGuildMember);

    }

    /**
     * Get GuildMember based off id
     *
     * @param id {@link UUID} of the guild member entry
     * @return {@link GuildMember}
     */
    @Get("/{id}")
    public GuildMember readGuildMember(UUID id) {
        return guildMemberServices.read(id);
    }

    /**
     * Find guild members that match all filled in parameters, return all results when given no params
     *
     * @param guildid   {@link UUID} of guild
     * @param memberid {@link UUID} of member
     * @param lead,    is lead of the guild
     * @return {@link List <Guild > list of guilds
     */
    @Get("/{?guildid,memberid,lead}")
    public Set<GuildMember> findGuildMembers(@Nullable UUID guildid,
                                           @Nullable UUID memberid,
                                           @Nullable Boolean lead) {
        return guildMemberServices.findByFields(guildid, memberid, lead);
    }

    /**
     * Delete A GuildMember
     *
     * @param id, id of {@link UUID} to delete
     */
    @Delete("/{id}")
    public HttpResponse<?> deleteGuildMember(@NotNull UUID id) {
        guildMemberServices.delete(id);
        return HttpResponse
                .ok();
    }
}
