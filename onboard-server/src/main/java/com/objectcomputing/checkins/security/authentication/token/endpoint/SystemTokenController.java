package com.objectcomputing.checkins.security.authentication.token.endpoint;

import com.objectcomputing.checkins.security.authentication.token.TokenService;
import com.objectcomputing.checkins.security.authentication.token.commons.AuthorizationToken;
import com.objectcomputing.checkins.security.authentication.token.commons.AuthorizationTokenBuilder;
import com.objectcomputing.checkins.services.commons.accessor.Accessor;
import com.objectcomputing.geoai.core.accessor.Accessor;
import com.objectcomputing.geoai.platform.security.authentication.PlatformAuthenticatedActor;
import com.objectcomputing.geoai.platform.token.TokenService;
import com.objectcomputing.geoai.platform.token.commons.AuthorizationToken;
import com.objectcomputing.geoai.platform.token.commons.AuthorizationTokenBuilder;
import com.objectcomputing.geoai.platform.token.commons.AuthorizationTokenSpec;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@Controller("/auth/api/system/token")
public class SystemTokenController {

    private final TokenService tokenService;
    private final AuthorizationTokenBuilder authorizationTokenBuilder;

    public SystemTokenController(TokenService tokenService, AuthorizationTokenBuilder authorizationTokenBuilder) {
        this.tokenService = tokenService;
        this.authorizationTokenBuilder = authorizationTokenBuilder;
    }

    @Post("/generate")
    @Produces(MediaType.APPLICATION_JSON)
    public Mono<AuthorizationToken> generate(PlatformAuthenticatedActor<?> actor, @Body TokenConfig tokenConfig) {
        // TODO compute Warnings
        List<String> warnings = null;

        Accessor accessor = actor.getAccount().asAccessor();

        return tokenService.create(accessor, tokenConfig, warnings)
                .map(tokenAuthorization -> authorizationTokenBuilder.build(actor.getAccount(), tokenAuthorization));
    }

    @Get("/lookup")
    @Produces(MediaType.APPLICATION_JSON)
    public Mono<AuthorizationTokenSpec> lookup() {
        return Mono.empty();
    }

    @Get("/lookup-self")
    @Produces(MediaType.APPLICATION_JSON)
    public Mono<AuthorizationTokenSpec> lookupSelf(PlatformAuthenticatedActor<?> actor) {
        return Mono.just(new AuthorizationTokenSpec(actor.getToken(), actor.getTokenValue()));
    }
}
