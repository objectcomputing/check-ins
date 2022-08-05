package com.objectcomputing.checkins.auth.endpoint;

import com.objectcomputing.checkins.security.authentication.token.jwt.validator.SignedJsonWebTokenValidator;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Produces;
import reactor.core.publisher.Mono;

import java.util.Map;

@Controller("/auth/api/interpreter")
public class SecretsInterpreterController {

    private final SignedJsonWebTokenValidator tokenValidator;

    SecretsInterpreterController(SignedJsonWebTokenValidator tokenValidator) {
        this.tokenValidator = tokenValidator;
    }

    @Get("/user-token")
    @Produces(MediaType.APPLICATION_JSON)
    public Mono<Map<String,Object>> get(UserAccountAuthenticatedActor authenticatedActor) {
        return Mono.from(tokenValidator.validateToken(authenticatedActor.getTokenValue()))
                .flatMap(token -> Mono.just(token.getPayload().toJsonMap()));
    }
}
