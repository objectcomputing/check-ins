package com.objectcomputing.checkins.services.team;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;
import javax.validation.Valid;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Put;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.micronaut.http.annotation.Produces;

import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;


@Controller(TeamController.TEAM_CONTROLLER_PATH)
@Secured({SecurityRule.IS_AUTHENTICATED})
@Produces(MediaType.APPLICATION_JSON)
@Tag(name="team")
public class TeamController {

    protected final TeamRepository teamRepository;

    public static final String TEAM_CONTROLLER_PATH = "/services/team";

    public TeamController(TeamRepository teamRepository){
        this.teamRepository = teamRepository;
    }
    /**
     * Find Team by name or find all.
     * @param name
     * @return
     */
    @Get("/{?name}")
    public List<Team> findByValue(@Nullable String name) {

        if(name != null) {
            return teamRepository.findByName(name);
        } else {
            return teamRepository.findAll();
        }
    }

    /**
     * Save a new team.
     * @param team
     * @return
     */
    @Post("/")
    public HttpResponse<Team> save(@Body @Valid Team team) {
        Team newTeam = teamRepository.save(team);
        
        return HttpResponse
                .created(newTeam)
                .headers(headers -> headers.location(location(newTeam.getUuid())));
    }

    /**
     * Update a Team.
     * @param team
     * @return
     */
    @Put("/")
    public HttpResponse<?> update(@Body @Valid Team team) {

        if(team.getUuid()!= null) {
            Team updatedTeam = teamRepository.update(team);
            return HttpResponse
                    .ok()
                    .headers(headers -> headers.location(location(updatedTeam.getUuid())))
                    .body(updatedTeam);
                    
        }
        
        return HttpResponse.badRequest();
    }

    protected URI location(UUID uuid) {
        return URI.create(TEAM_CONTROLLER_PATH + uuid);
    }
}





