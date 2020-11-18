package com.objectcomputing.checkins.services.team;

import com.objectcomputing.checkins.services.agenda_item.AgendaItem;
import com.objectcomputing.checkins.services.agenda_item.AgendaItemBadArgException;
import com.objectcomputing.checkins.services.agenda_item.AgendaItemNotFoundException;
import com.objectcomputing.checkins.services.agenda_item.AgendaItemsBulkLoadException;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Error;
import io.micronaut.http.annotation.*;
import io.micronaut.http.hateoas.JsonError;
import io.micronaut.http.hateoas.Link;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.netty.channel.EventLoopGroup;
import io.reactivex.Single;
import io.reactivex.exceptions.CompositeException;
import io.reactivex.schedulers.Schedulers;
import io.swagger.v3.oas.annotations.tags.Tag;

import javax.annotation.Nullable;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

@Controller("/services/team")
@Secured(SecurityRule.IS_AUTHENTICATED)
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "team")
public class TeamController {

    private final TeamServices teamService;
    private final EventLoopGroup eventLoopGroup;
    private final ExecutorService ioExecutorService;


    public TeamController(TeamServices teamService, EventLoopGroup eventLoopGroup, ExecutorService ioExecutorService) {
        this.teamService = teamService;
        this.eventLoopGroup = eventLoopGroup;
        this.ioExecutorService = ioExecutorService;
    }




    @Error(exception = TeamBadArgException.class)
    public HttpResponse<?> handleBadArgs(HttpRequest<?> request, TeamBadArgException e) {
        JsonError error = new JsonError(e.getMessage())
                .link(Link.SELF, Link.of(request.getUri()));

        return HttpResponse.<JsonError>badRequest()
                .body(error);
    }

    @Error(exception = TeamNotFoundException.class)
    public HttpResponse<?> handleNotFound(HttpRequest<?> request, TeamNotFoundException e) {
        JsonError error = new JsonError(e.getMessage())
                .link(Link.SELF, Link.of(request.getUri()));

        return HttpResponse.<JsonError>notFound()
                .body(error);
    }

    @Error(exception = TeamBulkLoadException.class)
    public HttpResponse<?> handleBulkLoadException(HttpRequest<?> request, TeamNotFoundException e) {
        JsonError error = new JsonError(e.getMessage())
                .link(Link.SELF, Link.of(request.getUri()));

        return HttpResponse.<JsonError>badRequest()
                .body(error);
    }

    @Error(exception = CompositeException.class)
    public HttpResponse<?> handleRxException(HttpRequest<?> request, CompositeException e) {

        for (Throwable t : e.getExceptions()) {
            if (t instanceof TeamBadArgException) {
                return handleBadArgs(request, (TeamBadArgException) t);
            }
            else if (t instanceof TeamNotFoundException) {
                return handleNotFound(request, (TeamNotFoundException) t);
            }
        }

        return HttpResponse.<JsonError>serverError();
    }

    /**
     * Create and save a new team
     *
     * @param team, {@link TeamCreateDTO}
     * @return {@link HttpResponse<Team>}
     */

    @Post(value = "/")
    public Single<HttpResponse<Team>> createATeam(@Body @Valid TeamCreateDTO team, HttpRequest<TeamCreateDTO> request) {

        return Single.fromCallable(() -> teamService.save(new Team(team.getName(),team.getDescription()))).observeOn(Schedulers.from(eventLoopGroup))
                .map(createdTeam -> {
                    return (HttpResponse<Team>) HttpResponse
                            .created(createdTeam)
                            .headers(headers -> headers.location(URI.create(String.format("%s/%s", request.getPath(), createdTeam.getId()))));
                }). subscribeOn(Schedulers.from(ioExecutorService));
    }

    /**
     * Create and save multiple teams
     *
     * @param teamsList, array of {@link TeamCreateDTO team create dto} to load {@link Team team(s)}
     * @return
     */

    @Post("/teams")
    public Single<HttpResponse<?>> loadTeams(@Body @NotNull @Valid List<TeamCreateDTO> teamsList, HttpRequest<List<TeamCreateDTO>> request) {

        return Single.fromCallable(() -> {
            List<String> errors = new ArrayList<>();
            List<Team> teamsCreated = new ArrayList<>();
            for (TeamCreateDTO teamDTO : teamsList) {
                Team team = new Team(teamDTO.getName(), teamDTO.getDescription());
                try {
                    teamService.save(team);
                    teamsCreated.add(team);
                } catch (CompositeException e) {
                    errors.add(String.format("Team %s was not added because: %s", team.getName(), e.getMessage()));
                }
            }   if (errors.isEmpty()) {
                return teamsCreated;
            }
            throw new TeamBulkLoadException(errors);
        }).map(teamsCreated -> HttpResponse.created(teamsCreated)
                .headers(headers -> headers.location(request.getUri())));
    }

    /**
     * Get team based on id
     *
     * @param id of team
     * @return {@link Team team matching id}
     */

    @Get("/{id}")
    public Single<HttpResponse<Team>> readTeam(UUID id) {
        return Single.fromCallable(() -> {
            Team result = teamService.read(id);
            if (result == null) {
                throw new TeamNotFoundException("No team for UUID");
            }
            return result;
        })
                .observeOn(Schedulers.from(eventLoopGroup))
                .map(team -> {
                    return (HttpResponse<Team>)HttpResponse.ok(team);
                }).subscribeOn(Schedulers.from(ioExecutorService));

    }

    /**
     * Find team(s) given a combination of the following parameters
     *
     * @param name,     name of the team
     * @param memberid, {@link UUID} of the member you wish to inquire in to which teams they are a part of
     * @return {@link List<Team> list of teams}, return all teams when no parameters filled in else
     * return all teams that match all of the filled in params
     */

    @Get("/{?name,memberid}")
    public Single<HttpResponse<Set<Team>>> findTeams(@Nullable String name, @Nullable UUID memberid) {
        return Single.fromCallable(() -> teamService.findByFields(name, memberid))
                .observeOn(Schedulers.from(eventLoopGroup))
                .map(teams -> {
                    return (HttpResponse<Set<Team>>) HttpResponse.ok(teams);
                }).subscribeOn(Schedulers.from(ioExecutorService));

    }

    /**
     * Update team.
     *
     * @param team, {@link Team}
     * @return {@link HttpResponse<Team>}
     */
    @Put("/")
    public Single<HttpResponse<Team>> update(@Body @Valid Team team, HttpRequest<Team> request) {
        if (team == null) {
            return Single.just(HttpResponse.ok());
        }
        return Single.fromCallable(() -> teamService.update(team))
                .observeOn(Schedulers.from(eventLoopGroup))
                .map(updatedTeam ->
                        (HttpResponse<Team>) HttpResponse
                                .ok()
                                .headers(headers -> headers.location(
                                        URI.create(String.format("%s/%s", request.getPath(), updatedTeam.getId()))))
                                .body(updatedTeam))
                .subscribeOn(Schedulers.from(ioExecutorService));

    }

    /**
     * Delete Team
     *
     * @param id, id of {@link Team} to delete
     */
    @Delete("/{id}")
    public HttpResponse<?> deleteTeam(@NotNull UUID id) {
        teamService.delete(id);
        return HttpResponse
                .ok();
    }
}