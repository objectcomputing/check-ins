package com.objectcomputing.checkins.services.guild.member;

import com.objectcomputing.checkins.services.guild.GuildBadArgException;
import io.micronaut.context.annotation.Parameter;
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

@Controller("/services/guild/member")
@Secured(SecurityRule.IS_ANONYMOUS)
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "guild-member")
public class GuildMemberController {

    @Inject
    private GuildMemberServices guildMemberServices;

    @Error(exception = GuildBadArgException.class)
    public HttpResponse<?> handleBadArgs(HttpRequest<?> request, GuildBadArgException e) {
        JsonError error = new JsonError(e.getMessage())
                .link(Link.SELF, Link.of(request.getUri()));

        return HttpResponse.<JsonError>badRequest()
                .body(error);
    }

    /**
     * Create and save a new guildMember.
     *
     * @param guildMember, {@link GuildMemberCreateDTO}
     * @return {@link HttpResponse <GuildMember>}
     */
    @Post(value = "/")
    public HttpResponse<GuildMember> createMembers(@Body @Valid GuildMemberCreateDTO guildMember,
                                                   HttpRequest<GuildMemberCreateDTO> request) {
        GuildMember newGuildMember = guildMemberServices.save(new GuildMember(guildMember.getGuildid(),
                guildMember.getMemberid(), guildMember.getLead()));
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
    @Put("/")
    public HttpResponse<?> updateMembers(@Body @Valid GuildMember guildMember, HttpRequest<GuildMember> request) {
        GuildMember updatedGuildMember = guildMemberServices.update(guildMember);
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
     * @param guildid  {@link UUID} of guild
     * @param memberid {@link UUID} of member
     * @param lead,    is lead of the guild
     * @return {@link List < Guild > list of guilds}
     */
    @Get("/{?guildid,memberid,lead}")
    public Set<GuildMember> findGuildMembers(@Nullable UUID guildid,
                                             @Nullable UUID memberid,
                                             @Nullable Boolean lead) {
        return guildMemberServices.findByFields(guildid, memberid, lead);
    }

    /**
     * Load members
     *
     * @param guildMembers, {@link List<GuildMemberCreateDTO> to load {@link GuildMember guild members}}
     * @return {@link HttpResponse<List<GuildMember>}
     */
    @Post("/load")
    public HttpResponse<?> loadGuildMembers(@Body @Valid @NotNull List<GuildMemberCreateDTO> guildMembers,
                                            HttpRequest<List<GuildMember>> request) {
        List<String> errors = new ArrayList<>();
        List<GuildMember> membersCreated = new ArrayList<>();
        for (GuildMemberCreateDTO guildMemberDTO : guildMembers) {
            GuildMember guildMember = new GuildMember(guildMemberDTO.getGuildid(),
                    guildMemberDTO.getMemberid(), guildMemberDTO.getLead());
            try {
                guildMemberServices.save(guildMember);
                membersCreated.add(guildMember);
            } catch (GuildBadArgException e) {
                errors.add(String.format("Member %s was not added to Guild %s because: %s", guildMember.getMemberid(),
                        guildMember.getGuildid(), e.getMessage()));
            }
        }
        if (errors.isEmpty()) {
            return HttpResponse.created(membersCreated)
                    .headers(headers -> headers.location(request.getUri()));
        } else {
            return HttpResponse.badRequest(errors)
                    .headers(headers -> headers.location(request.getUri()));
        }
    }
}
