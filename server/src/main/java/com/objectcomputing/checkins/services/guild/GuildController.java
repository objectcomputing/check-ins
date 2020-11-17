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
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import io.micronaut.scheduling.TaskExecutors;
import io.netty.channel.EventLoopGroup;
import io.reactivex.Single;
import io.reactivex.exceptions.CompositeException;
import io.reactivex.schedulers.Schedulers;

import javax.inject.Named;
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
  
    @Error(exception = GuildBadArgException.class)
    public HttpResponse<?> handleBadArgs(HttpRequest<?> request, GuildBadArgException e) {
        JsonError error = new JsonError(e.getMessage())
                .link(Link.SELF, Link.of(request.getUri()));

        return HttpResponse.<JsonError>badRequest()
                .body(error);
    }

    @Error(exception = GuildNotFoundException.class)
    public HttpResponse<?> handleNotFound(HttpRequest<?> request, GuildNotFoundException e) {
        JsonError error = new JsonError(e.getMessage())
                .link(Link.SELF, Link.of(request.getUri()));

        return HttpResponse.<JsonError>notFound()
                .body(error);
    }

    @Error(exception = GuildBulkLoadException.class)
    public HttpResponse<?> handleBulkLoadException(HttpRequest<?> request, GuildBulkLoadException e) {
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
    @Post("/")
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
      * Load the current guilds into checkinsdb.
      *
      * @param guildsList, array of {@link GuildCreateDTO guild create dto} to load {@link Guild guild(s)}
      */
     @Post("/guilds")
     public Single<HttpResponse<?>> loadGuilds(@Body @NotNull @Valid List<GuildCreateDTO> guildsList, HttpRequest<List<GuildCreateDTO>> request) {

         return Single.fromCallable(() -> {
             List<String> errors = new ArrayList<>();
             List<Guild> guildCreated = new ArrayList<>();
             for (GuildCreateDTO guildDTO : guildsList) {
                 Guild guild = new Guild(guildDTO.getName(),
                         guildDTO.getDescription());
                 try {
                     guildService.save(guild);
                     guildCreated.add(guild);
                 } catch (CompositeException e) {
                     errors.add(String.format("Guild %s was not added because: %s", guild.getName(),
                             guild.getDescription(), e.getMessage()));
                 }
             }
             if (errors.isEmpty()) {
                 return guildCreated;
             }
             throw new GuildBulkLoadException(errors);
         }).map(guildCreated -> HttpResponse.created(guildCreated)
                 .headers(headers -> headers.location(request.getUri())));
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
                throw new GuildNotFoundException("No guild for UUID");
            }
            return result;
        })
        .observeOn(Schedulers.from(eventLoopGroup))
        .map(guild -> {
            return (HttpResponse<Guild>)HttpResponse.ok(guild);
        }).subscribeOn(Schedulers.from(ioExecutorService));

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
    @Put("/")
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

}