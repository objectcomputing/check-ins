package com.objectcomputing.checkins.services.guild;

import com.objectcomputing.checkins.services.exceptions.NotFoundException;
import com.objectcomputing.checkins.services.team.TeamUpdateDTO;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.netty.channel.EventLoopGroup;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import io.swagger.v3.oas.annotations.tags.Tag;

import javax.annotation.Nullable;
import javax.inject.Named;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.net.URI;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

@Controller("/services/guild")
@Secured(SecurityRule.IS_AUTHENTICATED)
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "guild")
public class GuildController {

    private GuildServices guildService;
    private EventLoopGroup eventLoopGroup;
    private ExecutorService ioExecutorService;

    public GuildController(GuildServices guildService,
                           EventLoopGroup eventLoopGroup,
                           @Named(TaskExecutors.IO) ExecutorService ioExecutorService) {
        this.guildService = guildService;
        this.eventLoopGroup = eventLoopGroup;
        this.ioExecutorService = ioExecutorService;
    }

    /**
     * Create and save a new guild.
     *
     * @param guild, {@link GuildCreateDTO}
     * @return {@link HttpResponse<Guild>}
     */
    @Post()
    public Single<HttpResponse<Guild>> createAGuild(@Body @Valid GuildCreateDTO guild,
                                                    HttpRequest<GuildCreateDTO> request) {
        return Single.fromCallable(() -> guildService.save(new Guild(guild.getName(), guild.getDescription())))
                .observeOn(Schedulers.from(eventLoopGroup))
                .map(createdGuild -> {
                    //Using code block rather than lambda so we can log what thread we're in
                    return (HttpResponse<Guild>) HttpResponse
                            .created(createdGuild)
                            .headers(headers -> headers.location(
                                    URI.create(String.format("%s/%s", request.getUri(), createdGuild.getId()))));
                }).subscribeOn(Schedulers.from(ioExecutorService));

    }

    /**
     * Get guild based on id
     *
     * @param id {@link UUID} of guild
     * @return {@link Guild guild matching id}
     */
    @Get("/{id}")
    public Single<HttpResponse<Guild>> readGuild(UUID id) {
        return Single.fromCallable(() -> {
            Guild result = guildService.read(id);
            if (result == null) {
                throw new NotFoundException("No guild for UUID");
            }
            return result;
        })
                .observeOn(Schedulers.from(eventLoopGroup))
                .map(guild -> {
                    return (HttpResponse<Guild>) HttpResponse.ok(guild);
                }).subscribeOn(Schedulers.from(ioExecutorService));

    }

    /**
     * Find guild(s) given a combination of the following parameters
     *
     * @param name,     name of the guild
     * @param memberid, {@link UUID} of the member you wish to inquire in to which guilds they are a part of
     * @return {@link Set<Guild> set of guilds}, return all guilds when no parameters filled in else
     * return all guilds that match all of the filled in params
     */
    @Get("/{?name,memberid}")
    public Single<HttpResponse<Set<Guild>>> findGuilds(@Nullable String name, @Nullable UUID memberid) {
        return Single.fromCallable(() -> guildService.findByFields(name, memberid))
                .observeOn(Schedulers.from(eventLoopGroup))
                .map(guilds -> {
                    return (HttpResponse<Set<Guild>>) HttpResponse.ok(guilds);
                }).subscribeOn(Schedulers.from(ioExecutorService));
    }

    /**
     * Update guild.
     *
     * @param guild, {@link Guild}
     * @return {@link HttpResponse<Guild>}
     */
    @Put()
    public Single<HttpResponse<Guild>> update(@Body @Valid Guild guild, HttpRequest<Guild> request) {
        if (guild == null) {
            return Single.just(HttpResponse.ok());
        }
        return Single.fromCallable(() -> guildService.update(guild))
                .observeOn(Schedulers.from(eventLoopGroup))
                .map(updatedGuild ->
                        (HttpResponse<Guild>) HttpResponse
                                .ok()
                                .headers(headers -> headers.location(
                                        URI.create(String.format("%s/%s", request.getUri(), guild.getId()))))
                                .body(updatedGuild))
                .subscribeOn(Schedulers.from(ioExecutorService));
    }


    /**
     * Delete Guild
     *
     * @param id guild unique id
     * @return
     */
    @Delete("/{id}")
    public Single<HttpResponse> deleteGuild(@NotNull UUID id) {
        return Single.fromCallable(() -> guildService.delete(id))
                .observeOn(Schedulers.from(eventLoopGroup))
                .map(success -> (HttpResponse) HttpResponse.ok())
                .subscribeOn(Schedulers.from(ioExecutorService));
    }

}