package com.objectcomputing.checkins.services.guilds;

import io.micronaut.data.annotation.TypeDef;
import io.micronaut.data.model.DataType;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Error;
import io.micronaut.http.annotation.*;
import io.micronaut.http.hateoas.JsonError;
import io.micronaut.http.hateoas.Link;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.swagger.v3.oas.annotations.tags.Tag;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Controller("/guild")
@Secured(SecurityRule.IS_ANONYMOUS)
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "guild")
public class GuildController {

    @Inject
    private GuildServices guildsService;
    @Inject
    private GuildMemberServices guildMemberServices;

    @Error(exception = GuildBadArgException.class)
    public HttpResponse<?> handleBadArgs(HttpRequest<?> request, GuildBadArgException e) {
        JsonError error = new JsonError(e.getMessage())
                .link(Link.SELF, Link.of(request.getUri()));

        return HttpResponse.<JsonError>badRequest()
                .body(error);
    }

    /*
        GUILD
     */

    /**
     * Create and save a new guild.
     *
     * @param guild, {@link Guild}
     * @return {@link HttpResponse<Guild>}
     */

    @Post(value = "/")
    public HttpResponse<Guild> createAGuild(@Body @Valid Guild guild) {
        Guild newGuild = guildsService.save(guild);
        return HttpResponse
                .created(newGuild)
                .headers(headers -> headers.location(location(newGuild.getGuildid())));
    }

    /**
     * Load the current guilds into checkinsdb.
     *
     * @param guildslist, array of {@link Guild guilds} to load
     */

    @Post("/load")
    @Consumes(MediaType.APPLICATION_JSON)
    public HttpResponse<?> loadGuilds(@Body @NotNull List<Guild> guildslist) {
        List<String> errors = new ArrayList<>();
        List<Guild> guildsCreated = new ArrayList<>();
        for (Guild guild : guildslist) {
            try {
                guildsService.save(guild);
                guildsCreated.add(guild);
            } catch (GuildBadArgException e) {
                errors.add(String.format("Guild %s was not added because: %s", guild.getGuildid(), e.getMessage()));
            }
        }
        if (errors.isEmpty()) {
            return HttpResponse.created(guildsCreated);
        } else {
            return HttpResponse.badRequest(errors);
        }
    }

    /**
     * Find and read a guild or guilds given its id, or name
     *
     * @param guildid {@link UUID} of guild
     * @param name,   name of the guild
     * @return {@link List<Guild> list of guilds}
     */

    @Get("/{?guildid,name}")
    public List<Guild> findGuilds(@Nullable UUID guildid, @Nullable String name) {
        return guildsService.findByIdOrLikeName(guildid, name);
    }

    /**
     * Update guild.
     *
     * @param guild, {@link Guild}
     * @return {@link HttpResponse<Guild>}
     */
    @Put("/")
    public HttpResponse<?> update(@Body @Valid Guild guild) {
        Guild updatedGuild = guildsService.update(guild);
        return HttpResponse
                .ok()
                .headers(headers -> headers.location(location(updatedGuild.getGuildid())))
                .body(updatedGuild);

    }

    /*
        GUILD MEMBERS
     */

    /**
     * Create and save a new guildMember.
     *
     * @param guildMember, {@link GuildMember}
     * @return {@link HttpResponse<GuildMember>}
     */

    @Post(value = "/member")
    public HttpResponse<GuildMember> createMembers(@Body @Valid GuildMember guildMember) {
        GuildMember newGuild = guildMemberServices.save(guildMember);
        return HttpResponse
                .created(newGuild)
                .headers(headers -> headers.location(location(newGuild.getGuildid())));
    }

    /**
     * Update guildMember.
     *
     * @param guildMember, {@link GuildMember}
     * @return {@link HttpResponse<GuildMember>}
     */
    @Put("/member")
    public HttpResponse<?> updateMembers(@Body @Valid GuildMember guildMember) {
        GuildMember updatedGuild = guildMemberServices.update(guildMember);
        return HttpResponse
                .ok()
                .headers(headers -> headers.location(location(updatedGuild.getGuildid())))
                .body(updatedGuild);

    }

    /**
     * Find guild members that match all filled in parameters, return all results when given no params
     *
     * @param id       {@link UUID}
     * @param guildid  {@link UUID} of guild
     * @param memberid {@link UUID} of member
     * @param lead,    is lead of the guild
     * @return {@link List<Guild> list of guilds}
     */

    @Get("/member{?id,guildid,memberid,lead}")
    public Set<GuildMember> findGuildMembers(@Nullable @TypeDef(type = DataType.STRING) UUID id,
                                             @Nullable @TypeDef(type = DataType.STRING) UUID guildid,
                                             @Nullable @TypeDef(type = DataType.STRING) UUID memberid,
                                             @Nullable Boolean lead) {
        return guildMemberServices.findByFields(id, guildid, memberid, lead);
    }

    /**
     * Load members
     *
     * @param guildMembers, {@link List<GuildMember>}
     * @return {@link HttpResponse<List<GuildMember>}
     */
    @Post("/member/load")
    public HttpResponse<?> loadGuildMembers(@Body @Valid @NotNull List<GuildMember> guildMembers) {
        List<String> errors = new ArrayList<>();
        List<GuildMember> membersCreated = new ArrayList<>();
        for (GuildMember guildMember : guildMembers) {
            try {
                guildMemberServices.save(guildMember);
                membersCreated.add(guildMember);
            } catch (GuildBadArgException e) {
                errors.add(String.format("Member %s was not added to Guild %s because: %s", guildMember.getMemberid(),
                        guildMember.getGuildid(), e.getMessage()));
            }
        }
        if (errors.isEmpty()) {
            return HttpResponse.created(membersCreated);
        } else {
            return HttpResponse.badRequest(errors);
        }
    }


    protected URI location(UUID uuid) {
        return URI.create("/guild/" + uuid);
    }
}