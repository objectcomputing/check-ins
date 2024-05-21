package com.objectcomputing.checkins.services.team;


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
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Controller("/services/teams")
@ExecuteOn(TaskExecutors.IO)
@Secured(SecurityRule.IS_AUTHENTICATED)
@Tag(name = "teams")
public class TeamController {

    private final TeamServices teamService;

    public TeamController(TeamServices teamService) {
        this.teamService = teamService;
    }

    /**
     * Create and save a new team
     *
     * @param team, {@link TeamCreateDTO}
     * @return {@link HttpResponse<TeamResponseDTO>}
     */
    @Post()
    public Mono<HttpResponse<TeamResponseDTO>> createATeam(@Body @Valid TeamCreateDTO team, HttpRequest<?> request) {
        return Mono.fromCallable(() -> teamService.save(team))
                .map(createdTeam -> HttpResponse.created(createdTeam)
                        .headers(headers -> headers.location(URI.create(String.format("%s/%s", request.getPath(), createdTeam.getId())))));
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
                .map(HttpResponse::ok);
    }

    /**
     * Find team(s) given a combination of the following parameters
     *
     * @param name,     name of the team
     * @param memberId, {@link UUID} of the member you wish to inquire in to which teams they are a part of
     * @return {@link List<TeamResponseDTO> list of teams}, return all teams when no parameters filled in else
     * return all teams that match all the filled in params
     */
    @Get("/{?name,memberId}")
    public Mono<HttpResponse<Set<TeamResponseDTO>>> findTeams(@Nullable String name, @Nullable UUID memberId) {
        return Mono.fromCallable(() -> teamService.findByFields(name, memberId))
                .map(HttpResponse::ok);
    }

    /**
     * Update team.
     *
     * @param team, {@link TeamUpdateDTO}
     * @return {@link HttpResponse<TeamResponseDTO>}
     */
    @Put()
    public Mono<HttpResponse<TeamResponseDTO>> update(@Body @Valid TeamUpdateDTO team, HttpRequest<?> request) {
        return Mono.fromCallable(() -> teamService.update(team))
                .map(updated -> HttpResponse.ok(updated)
                        .headers(headers -> headers.location(URI.create(String.format("%s/%s", request.getUri(), team.getId())))));

    }

    /**
     * Delete Team
     *
     * @param id, id of {@link TeamUpdateDTO} to delete
     * @return
     */
    @Delete("/{id}")
    public Mono<HttpResponse<Object>> deleteTeam(@NotNull UUID id) {
        return Mono.fromCallable(() -> teamService.delete(id))
                .map(success -> HttpResponse.ok());
    }
}