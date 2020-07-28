package com.objectcomputing.checkins.services.guilds;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.UUID;

@Controller("/guild")
@Secured(SecurityRule.IS_ANONYMOUS)
@Produces(MediaType.APPLICATION_JSON)
@Tag(name="guild")
public class GuildController {

    private static final Logger LOG = LoggerFactory.getLogger(GuildController.class);

    @Inject
    private GuildServices guildsService;
    @Inject
    private GuildMemberServices guildMemberServices;

    /**
     * Create and save a new guild.
     *
     * @param guild, {@link Guild}
     * @return
     */

    @Post(value = "/")
    public HttpResponse<Guild> createAGuild(@Body @Valid Guild guild) {
        Guild newGuild = guildsService.save(guild);

        if (newGuild == null) {
            return HttpResponse.status(HttpStatus.valueOf(409), "already exists");
        } else {
            return HttpResponse
                    .created(newGuild)
                    .headers(headers -> headers.location(location(newGuild.getGuildId())));
        }
    }

    /**
     * Load the current guilds into checkinsdb.
     *
     * @param guildslist
     * @return
     */

    @Post("/loadguilds")
    @Consumes(MediaType.APPLICATION_JSON)
    public void loadGuilds(@Body Guild[] guildslist) {
        guildsService.load(guildslist);
    }

    /**
     * Find and read a guild or guilds given its id, or name
     *
     * @param guildId {@link UUID} of guild
     * @param name, name of the guild
     * @return
     */

    @Get("/{?guildId,name}")
    public List<Guild> findByValue(@Nullable UUID guildId, @Nullable String name) {
        return guildsService.findByIdOrLikeName(guildId, name);
    }

    /**
     * Update guild.
     * @param guild, {@link Guild}
     * @return
     */
    @Put("/")
    public HttpResponse<?> update(@Body @Part @Valid Guild guild) {

        if(null != guild.getGuildId()) {
            Guild updatedGuild = guildsService.update(guild);
            return HttpResponse
                    .ok()
                    .headers(headers -> headers.location(location(updatedGuild.getGuildId())))
                    .body(updatedGuild);
        }

        return HttpResponse.badRequest();
    }

    protected URI location(UUID uuid) {
        return URI.create("/guild/" + uuid);
    }
}