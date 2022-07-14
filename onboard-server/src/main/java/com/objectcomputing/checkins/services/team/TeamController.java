package com.objectcomputing.checkins.services.team;


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
import reactor.core.scheduler.Schedulers;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.net.URI;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

@Controller("/services/teams")
@Secured(SecurityRule.IS_AUTHENTICATED)
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "teams")
public class TeamController {

    private final TeamServices teamService;
    private final EventLoopGroup eventLoopGroup;
    private final ExecutorService ioExecutorService;

    public TeamController(TeamServices teamService,
                          EventLoopGroup eventLoopGroup,
                          @Named(TaskExecutors.IO) ExecutorService ioExecutorService) {
        this.teamService = teamService;
        this.eventLoopGroup = eventLoopGroup;
        this.ioExecutorService = ioExecutorService;
    }

    /**
     * Create and save a new team
     *
     * @param team, {@link TeamCreateDTO}
     * @return {@link HttpResponse<TeamResponseDTO>}
     */
    @Post()
    public Mono<HttpResponse<TeamResponseDTO>> createATeam(@Body @Valid TeamCreateDTO team, HttpRequest<TeamCreateDTO> request) {

        return Mono.fromCallable(() -> teamService.save(team))
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .map(createdTeam -> (HttpResponse<TeamResponseDTO>) HttpResponse
                        .created(createdTeam)
                        .headers(headers -> headers.location(URI.create(String.format("%s/%s", request.getPath(), createdTeam.getId())))))
                .subscribeOn(Schedulers.fromExecutor(ioExecutorService));
    }

    /**
     * Get team based on id
     *
     * @param id of team
     * @return {@link TeamResponseDTO team matching id}
     */

    @Get("/{id}")
    public Mono<HttpResponse<TeamResponseDTO>> readTeam(@NotNull UUID id) {
        return Mono.fromCallable(() -> teamService.read(id))
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .map(team -> (HttpResponse<TeamResponseDTO>) HttpResponse.ok(team))
                .subscribeOn(Schedulers.fromExecutor(ioExecutorService));
    }

    /**
     * Find team(s) given a combination of the following parameters
     *
     * @param name,     name of the team
     * @param memberId, {@link UUID} of the member you wish to inquire in to which teams they are a part of
     * @return {@link List<TeamResponseDTO> list of teams}, return all teams when no parameters filled in else
     * return all teams that match all of the filled in params
     */
    @Get("/{?name,memberId}")
    public Mono<HttpResponse<Set<TeamResponseDTO>>> findTeams(@Nullable String name, @Nullable UUID memberId) {
        return Mono.fromCallable(() -> teamService.findByFields(name, memberId))
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .map(teams -> (HttpResponse<Set<TeamResponseDTO>>) HttpResponse.ok(teams))
                .subscribeOn(Schedulers.fromExecutor(ioExecutorService));
    }

    /**
     * Update team.
     *
     * @param team, {@link TeamUpdateDTO}
     * @return {@link HttpResponse<TeamResponseDTO>}
     */
    @Put()
    public Mono<HttpResponse<TeamResponseDTO>> update(@Body @Valid TeamUpdateDTO team, HttpRequest<TeamUpdateDTO> request) {
        return Mono.fromCallable(() -> teamService.update(team))
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .map(updated -> (HttpResponse<TeamResponseDTO>) HttpResponse
                        .ok()
                        .headers(headers -> headers.location(URI.create(String.format("%s/%s", request.getUri(), team.getId()))))
                        .body(updated))
                .subscribeOn(Schedulers.fromExecutor(ioExecutorService));

    }

    /**
     * Delete Team
     *
     * @param id, id of {@link TeamUpdateDTO} to delete
     * @return
     */
    @Delete("/{id}")
    public Mono<HttpResponse> deleteTeam(@NotNull UUID id) {
        return Mono.fromCallable(() -> teamService.delete(id))
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .map(success -> (HttpResponse) HttpResponse.ok())
                .subscribeOn(Schedulers.fromExecutor(ioExecutorService));
    }
}