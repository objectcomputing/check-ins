package com.objectcomputing.checkins.services.guild;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.netty.channel.EventLoopGroup;
import io.swagger.v3.oas.annotations.tags.Tag;

import io.micronaut.core.annotation.Nullable;
import jakarta.inject.Named;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.net.URI;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

@Controller("/services/guilds")
@Secured(SecurityRule.IS_AUTHENTICATED)
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "guilds")
public class GuildController {

    private GuildServices guildService;
    private EventLoopGroup eventLoopGroup;
    private final Scheduler scheduler;

    public GuildController(GuildServices guildService,
                           EventLoopGroup eventLoopGroup,
                           @Named(TaskExecutors.IO) ExecutorService ioExecutorService) {
        this.guildService = guildService;
        this.eventLoopGroup = eventLoopGroup;
        this.scheduler = Schedulers.fromExecutorService(ioExecutorService);
    }

    /**
     * Create and save a new guild
     *
     * @param guild, {@link GuildCreateDTO}
     * @return {@link HttpResponse<GuildResponseDTO>}
     */
    @Post()
    public Mono<HttpResponse<GuildResponseDTO>> createAGuild(@Body @Valid GuildCreateDTO guild, HttpRequest<GuildCreateDTO> request) {

        return Mono.fromCallable(() -> guildService.save(guild))
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .map(createdGuild -> (HttpResponse<GuildResponseDTO>) HttpResponse
                        .created(createdGuild)
                        .headers(headers -> headers.location(URI.create(String.format("%s/%s", request.getPath(), createdGuild.getId())))))
                .subscribeOn(scheduler);
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
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .map(guild -> (HttpResponse<GuildResponseDTO>) HttpResponse.ok(guild))
                .subscribeOn(scheduler);
    }

    /**
     * Find guild(s) given a combination of the following parameters
     *
     * @param name,     name of the guild
     * @param memberid, {@link UUID} of the member you wish to inquire in to which guilds they are a part of
     * @return {@link List < GuildResponseDTO > list of guilds}, return all guilds when no parameters filled in else
     * return all guilds that match all of the filled in params
     */

    @Get("/{?name,memberid}")
    public Mono<HttpResponse<Set<GuildResponseDTO>>> findGuilds(@Nullable String name, @Nullable UUID memberid) {
        return Mono.fromCallable(() -> guildService.findByFields(name, memberid))
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .map(guilds -> (HttpResponse<Set<GuildResponseDTO>>) HttpResponse.ok(guilds))
                .subscribeOn(scheduler);
    }

    /**
     * Update guild.
     *
     * @param guild, {@link GuildUpdateDTO}
     * @return {@link HttpResponse< GuildResponseDTO >}
     */
    @Put()
    public Mono<HttpResponse<GuildResponseDTO>> update(@Body @Valid GuildUpdateDTO guild, HttpRequest<GuildUpdateDTO> request) {
        return Mono.fromCallable(() -> guildService.update(guild))
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .map(updated -> (HttpResponse<GuildResponseDTO>) HttpResponse
                        .ok()
                        .headers(headers -> headers.location(URI.create(String.format("%s/%s", request.getUri(), guild.getId()))))
                        .body(updated))
                .subscribeOn(scheduler);

    }

    /**
     * Delete Guild
     *
     * @param id, id of {@link GuildUpdateDTO} to delete
     * @return
     */
    @Delete("/{id}")
    public Mono<HttpResponse> deleteGuild(@NotNull UUID id) {
        return Mono.fromCallable(() -> guildService.delete(id))
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .map(success -> (HttpResponse) HttpResponse.ok())
                .subscribeOn(scheduler);
    }

}