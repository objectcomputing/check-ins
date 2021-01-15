package com.objectcomputing.checkins.services.guild.member;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Error;
import io.micronaut.http.annotation.*;
import io.micronaut.http.hateoas.JsonError;
import io.micronaut.http.hateoas.Link;
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
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

@Controller("/services/guild/member")
@Secured(SecurityRule.IS_AUTHENTICATED)
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "guild-member")
public class GuildMemberController {

    private GuildMemberServices guildMemberServices;
    private EventLoopGroup eventLoopGroup;
    private ExecutorService ioExecutorService;

    public GuildMemberController(GuildMemberServices guildMemberServices,
                                 EventLoopGroup eventLoopGroup,
                                 @Named(TaskExecutors.IO) ExecutorService ioExecutorService) {
        this.guildMemberServices = guildMemberServices;
        this.eventLoopGroup = eventLoopGroup;
        this.ioExecutorService = ioExecutorService;
    }

    @Error(exception = GuildMemberNotFoundException.class)
    public HttpResponse<?> handleNotFound(HttpRequest<?> request, GuildMemberNotFoundException e) {
        JsonError error = new JsonError(e.getMessage())
                .link(Link.SELF, Link.of(request.getUri()));

        return HttpResponse.<JsonError>notFound()
                .body(error);
    }

    /**
     * Create and save a new guildMember.
     *
     * @param guildMember, {@link GuildMemberCreateDTO}
     * @return {@link HttpResponse <GuildMember>}
     */
    @Post()
    public Single<HttpResponse<GuildMember>> createMembers(@Body @Valid GuildMemberCreateDTO guildMember,
                                                           HttpRequest<GuildMemberCreateDTO> request) {
        return Single.fromCallable(() -> guildMemberServices.save(new GuildMember(guildMember.getGuildid(), guildMember.getMemberid(), guildMember.isLead())))
                .observeOn(Schedulers.from(eventLoopGroup))
                .map(newGuildMember -> {
                    //Using code block rather than lambda so we can log what thread we're in
                    return (HttpResponse<GuildMember>) HttpResponse
                            .created(newGuildMember)
                            .headers(headers -> headers.location(
                                    URI.create(String.format("%s/%s", request.getPath(), newGuildMember.getId()))));
                }).subscribeOn(Schedulers.from(ioExecutorService));

    }

    /**
     * Update guildMember.
     *
     * @param guildMember, {@link GuildMember}
     * @return {@link HttpResponse<GuildMember>}
     */
    @Put()
    public Single<HttpResponse<GuildMember>> update(@Body @Valid GuildMember guildMember, HttpRequest<GuildMember> request) {
        if (guildMember == null) {
            return Single.just(HttpResponse.ok());
        }
        return Single.fromCallable(() -> guildMemberServices.update(guildMember))
                .observeOn(Schedulers.from(eventLoopGroup))
                .map(updatedGuildMember ->
                        (HttpResponse<GuildMember>) HttpResponse
                                .ok()
                                .headers(headers -> headers.location(
                                        URI.create(String.format("%s/%s", request.getPath(), updatedGuildMember.getId()))))
                                .body(updatedGuildMember))
                .subscribeOn(Schedulers.from(ioExecutorService));
    }

    /**
     * Get GuildMember based off id
     *
     * @param id {@link UUID} of the guild member entry
     * @return {@link GuildMember}
     */
    @Get("/{id}")
    public Single<HttpResponse<GuildMember>> readGuildMember(UUID id) {
        return Single.fromCallable(() -> {
            GuildMember result = guildMemberServices.read(id);
            if (result == null) {
                throw new GuildMemberNotFoundException("No guild member for UUID");
            }
            return result;
        })
                .observeOn(Schedulers.from(eventLoopGroup))
                .map(guildmembers -> {
                    return (HttpResponse<GuildMember>) HttpResponse.ok(guildmembers);
                }).subscribeOn(Schedulers.from(ioExecutorService));

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
    public Single<HttpResponse<Set<GuildMember>>> findGuildMembers(@Nullable UUID guildid, @Nullable UUID memberid, @Nullable Boolean lead) {
        return Single.fromCallable(() -> guildMemberServices.findByFields(guildid, memberid, lead))
                .observeOn(Schedulers.from(eventLoopGroup))
                .map(guildmembers -> {
                    return (HttpResponse<Set<GuildMember>>) HttpResponse.ok(guildmembers);
                }).subscribeOn(Schedulers.from(ioExecutorService));
    }

    /**
     * Delete Guild Member
     *
     * @param id guild member unique id
     * @return
     */
//    @Delete("/{id}")
    @Delete("/{id}")
    public HttpResponse<?> deleteMemberSkill(@NotNull UUID id) {
        guildMemberServices.delete(id);
        return HttpResponse
                .ok();
    }
//    public Single<HttpResponse> deleteGuildMember(@NotNull UUID id) {
//        return Single.fromCallable(() -> guildMemberServices.delete(id))
//                .observeOn(Schedulers.from(eventLoopGroup))
//                .map(success -> (HttpResponse) HttpResponse.ok())
//                .subscribeOn(Schedulers.from(ioExecutorService));
//    }
}
