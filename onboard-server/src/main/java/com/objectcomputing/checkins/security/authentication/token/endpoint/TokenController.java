package com.objectcomputing.checkins.security.authentication.token.endpoint;

import com.objectcomputing.geoai.platform.security.authentication.PlatformAuthenticatedActor;
import com.objectcomputing.geoai.platform.token.TokenService;
import com.objectcomputing.geoai.platform.token.commons.AuthorizationToken;
import com.objectcomputing.geoai.platform.token.commons.AuthorizationTokenBuilder;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Produces;
import reactor.core.publisher.Mono;

import java.util.Map;

@Controller("/auth/api/token")
public class TokenController {

    private final TokenService tokenService;
    private final AuthorizationTokenBuilder authorizationTokenBuilder;

    public TokenController(TokenService tokenService, AuthorizationTokenBuilder authorizationTokenBuilder) {
        this.tokenService = tokenService;
        this.authorizationTokenBuilder = authorizationTokenBuilder;
    }

    @Get("/renew")
    @Produces(MediaType.APPLICATION_JSON)
    public Mono<AuthorizationToken> renew(PlatformAuthenticatedActor<?> actor) {
        return tokenService.renewTokenAuthorization(actor.getToken())
                .flatMap(tokenAuthorization ->
                        Mono.just(authorizationTokenBuilder.build(actor.getAccount(), tokenAuthorization)));
    }

    @Get("/touch")
    @Produces(MediaType.APPLICATION_JSON)
    public Mono<Map<String, Boolean>> touch(PlatformAuthenticatedActor<?> actor) {
        return tokenService.touchTokenAuthorization(actor.getToken())
                .flatMap(tokenAuthorization -> Mono.just(Map.of("success", Boolean.TRUE)));
    }

}
