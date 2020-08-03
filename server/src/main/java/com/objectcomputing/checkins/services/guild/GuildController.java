package com.objectcomputing.checkins.services.guild;

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

@Controller("/services/guild")
@Secured(SecurityRule.IS_ANONYMOUS)
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "guild")
public class GuildController {

    @Inject
    private GuildServices guildService;

    @Error(exception = GuildBadArgException.class)
    public HttpResponse<?> handleBadArgs(HttpRequest<?> request, GuildBadArgException e) {
        JsonError error = new JsonError(e.getMessage())
                .link(Link.SELF, Link.of(request.getUri()));

        return HttpResponse.<JsonError>badRequest()
                .body(error);
    }

    /**
     * Create and save a new guild.
     *
     * @param guild, {@link GuildCreateDTO}
     * @return {@link HttpResponse<Guild>}
     */

    @Post(value = "/")
    public HttpResponse<Guild> createAGuild(@Body @Valid GuildCreateDTO guild, HttpRequest<GuildCreateDTO> request) {
        Guild newGuild = guildService.save(new Guild(guild.getName(), guild.getDescription()));
        return HttpResponse
                .created(newGuild)
                .headers(headers -> headers.location(URI.create(String.format("%s/%s", request.getUri(), newGuild.getGuildid()))));
    }

    /**
     * Load the current guilds into checkinsdb.
     *
     * @param guildsList, array of {@link GuildCreateDTO guild create dto} to load {@link Guild guild(s)}
     */

    @Post("/guilds")
    public HttpResponse<?> loadGuilds(@Body @NotNull @Valid List<GuildCreateDTO> guildsList, HttpRequest<List<GuildCreateDTO>> request) {
        List<String> errors = new ArrayList<>();
        List<Guild> guildsCreated = new ArrayList<>();
        for (GuildCreateDTO guildDTO : guildsList) {
            Guild guild = new Guild(guildDTO.getName(), guildDTO.getDescription());
            try {
                guildService.save(guild);
                guildsCreated.add(guild);
            } catch (GuildBadArgException e) {
                errors.add(String.format("Guild %s was not added because: %s", guild.getName(), e.getMessage()));
            }
        }
        if (errors.isEmpty()) {
            return HttpResponse.created(guildsCreated).headers(headers ->
                    headers.location(request.getUri()));
        } else {
            return HttpResponse.badRequest(errors).headers(headers ->
                    headers.location(request.getUri()));
        }
    }

    /**
     * Get guild based on id
     *
     * @param guildid {@link UUID} of guild
     * @return {@link Guild guild matching id}
     */

    @Get("/{guildid}")
    public Guild readGuild(UUID guildid) {
        return guildService.read(guildid);
    }

    /**
     * Find guild(s) given a combination of the following parameters
     *
     * @param name,     name of the guild
     * @param memberid, {@link UUID} of the member you wish to inquire in to which guilds they are a part of
     * @return {@link List<Guild> list of guilds}, return all guilds when no parameters filled in else
     * return all guilds that match all of the filled in params
     */

    @Get("/{?name,memberid}")
    public Set<Guild> findGuilds(@Nullable String name, @Nullable UUID memberid) {
        return guildService.findByFields(name, memberid);
    }

    /**
     * Update guild.
     *
     * @param guild, {@link Guild}
     * @return {@link HttpResponse<Guild>}
     */
    @Put("/")
    public HttpResponse<?> update(@Body @Valid Guild guild, HttpRequest<Guild> request) {
        Guild updatedGuild = guildService.update(guild);
        return HttpResponse
                .ok()
                .headers(headers -> headers.location(URI.create(String.format("%s/%s", request.getUri(), guild.getGuildid()))))
                .body(updatedGuild);

    }
}