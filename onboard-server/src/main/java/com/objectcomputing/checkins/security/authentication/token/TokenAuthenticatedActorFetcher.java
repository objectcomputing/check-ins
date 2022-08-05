package com.objectcomputing.checkins.security.authentication.token;

import com.objectcomputing.geoai.security.authentication.AuthenticatedActor;
import com.objectcomputing.geoai.security.authentication.AuthenticatedActorFetcher;
import com.objectcomputing.geoai.security.filters.SecurityFilter;
import com.objectcomputing.geoai.security.token.jwt.JsonWebToken;
import com.objectcomputing.geoai.security.token.jwt.JsonWebTokenClaims;
import io.micronaut.context.event.ApplicationEventPublisher;
import io.micronaut.http.HttpRequest;
import io.micronaut.security.event.TokenValidatedEvent;
import io.micronaut.security.token.reader.TokenResolver;
import jakarta.inject.Singleton;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;

import java.util.Collection;
import java.util.Optional;

@Singleton
public class TokenAuthenticatedActorFetcher implements AuthenticatedActorFetcher {

    private final TokenValidator<JsonWebToken> tokenValidator;
    private final TokenResolver tokenResolver;
    private final Collection<TokenAuthenticatedActorFactory<?,?>> authenticatedActorFactories;
    private final ApplicationEventPublisher<TokenValidatedEvent> eventPublisher;

    TokenAuthenticatedActorFetcher(TokenValidator<JsonWebToken> tokenValidator,
                                   TokenResolver tokenResolver,
                                   Collection<TokenAuthenticatedActorFactory<?,?>> authenticatedActorFactories,
                                   ApplicationEventPublisher<TokenValidatedEvent> eventPublisher) {
        this.tokenValidator = tokenValidator;
        this.tokenResolver = tokenResolver;
        this.authenticatedActorFactories = authenticatedActorFactories;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public Publisher<AuthenticatedActor> fetchAuthenticatedActor(HttpRequest<?> request) {
        Optional<String> token = tokenResolver.resolveToken(request);

        if (!token.isPresent()) {
            return Flux.empty();
        }

        String tokenValue = token.get();

        return Flux.from(tokenValidator.validateToken(tokenValue, request))
                .map(jsonWebToken -> {
                    request.setAttribute(SecurityFilter.TOKEN, tokenValue);
                    request.setAttribute(SecurityFilter.JWT_TOKEN, jsonWebToken);

                    eventPublisher.publishEvent(new TokenValidatedEvent(tokenValue));

                    final JsonWebTokenClaims claims = jsonWebToken.getClaims();

                    AuthenticatedActor authenticatedActor = null;
                    for(TokenAuthenticatedActorFactory factory : authenticatedActorFactories) {
                        if(factory.supports(claims)) {
                            Optional<AuthenticatedActor> authenticatedActorOptional = factory.createOptionalAuthenticatedActor(claims, tokenValue);

                            authenticatedActor = authenticatedActorOptional.orElse(null);
                            break;
                        }
                    }

                    request.setAttribute(SecurityFilter.AUTHENTICATED_ACTOR, authenticatedActor);

                    return authenticatedActor;
                });
    }
}
