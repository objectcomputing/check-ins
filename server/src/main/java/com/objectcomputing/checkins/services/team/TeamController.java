package com.objectcomputing.checkins.services.team;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Put;
import io.micronaut.http.annotation.Status;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.net.URI;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Controller("/services/teams")
@ExecuteOn(TaskExecutors.BLOCKING)
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
    @Post
    public HttpResponse<TeamResponseDTO> createATeam(@Body @Valid TeamCreateDTO team, HttpRequest<?> request) {
        TeamResponseDTO createdTeam = teamService.save(team);
        return HttpResponse.created(createdTeam)
                .headers(headers -> headers.location(URI.create(String.format("%s/%s", request.getPath(), createdTeam.getId()))));
    }

    /**
     * Get team based on id
     *
     * @param id of team
     * @return {@link TeamResponseDTO team matching id}
     */
    @Get("/{id}")
    public TeamResponseDTO readTeam(@NotNull UUID id) {
        return teamService.read(id);
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
    public Set<TeamResponseDTO> findTeams(@Nullable String name, @Nullable UUID memberId) {
        return teamService.findByFields(name, memberId);
    }

    /**
     * Update team.
     *
     * @param team, {@link TeamUpdateDTO}
     * @return {@link HttpResponse<TeamResponseDTO>}
     */
    @Put
    public HttpResponse<TeamResponseDTO> update(@Body @Valid TeamUpdateDTO team, HttpRequest<?> request) {
        TeamResponseDTO updated = teamService.update(team);
        return HttpResponse.ok(updated)
                .headers(headers -> headers.location(URI.create(String.format("%s/%s", request.getUri(), team.getId()))));
    }

    /**
     * Delete Team
     *
     * @param id, id of {@link TeamUpdateDTO} to delete
     */
    @Delete("/{id}")
    @Status(HttpStatus.OK)
    public void deleteTeam(@NotNull UUID id) {
        teamService.delete(id);
    }
}