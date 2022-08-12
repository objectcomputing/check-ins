package com.objectcomputing.checkins.auth.endpoint;

import com.objectcomputing.checkins.security.authentication.AuthenticatedActor;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Produces;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import reactor.core.publisher.Mono;

import java.util.Map;

@Secured(SecurityRule.IS_AUTHENTICATED)
@Controller("/api/token")
public class AuthenticatedActorInterpreterController {

    public AuthenticatedActorInterpreterController() {

    }

    @Get("/interpreter")
    @Produces(MediaType.APPLICATION_JSON)
    public Mono<Map<String,Object>> get(AuthenticatedActor authenticatedActor) {
        return Mono.just(Map.of("actor", authenticatedActor));
    }
}
