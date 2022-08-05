package com.objectcomputing.checkins.security.authentication;

import com.objectcomputing.geoai.core.account.Account;
import com.objectcomputing.geoai.core.util.UUIDUtilities;
import com.objectcomputing.geoai.platform.token.model.Token;
import com.objectcomputing.geoai.platform.token.model.TokenAuthorization;
import com.objectcomputing.geoai.platform.token.model.TokenAuthorizationRepository;
import com.objectcomputing.geoai.platform.token.model.TokenRepository;
import com.objectcomputing.geoai.security.authentication.AuthenticationException;
import com.objectcomputing.geoai.security.authentication.config.ClaimsConfiguration;
import com.objectcomputing.geoai.security.token.TokenAuthenticatedActorFactory;
import com.objectcomputing.geoai.security.token.jwt.JsonWebTokenClaims;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.UUID;

public abstract class AbstractLocalPlatformAccountTokenAuthenticatedActorFactory<T extends Account> implements TokenAuthenticatedActorFactory<T, Token> {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractLocalPlatformAccountTokenAuthenticatedActorFactory.class);

    protected final ClaimsConfiguration claimsConfiguration;
    protected final TokenRepository tokenRepository;
    protected final TokenAuthorizationRepository tokenAuthorizationRepository;

    public AbstractLocalPlatformAccountTokenAuthenticatedActorFactory(ClaimsConfiguration claimsConfiguration,
                                                                      TokenRepository tokenRepository,
                                                                      TokenAuthorizationRepository tokenAuthorizationRepository) {
        this.claimsConfiguration = claimsConfiguration;
        this.tokenRepository = tokenRepository;
        this.tokenAuthorizationRepository = tokenAuthorizationRepository;
    }

    @Override
    public abstract boolean supports(JsonWebTokenClaims claims);

    @Override
    public PlatformAuthenticatedActor<T> createAuthenticatedActor(JsonWebTokenClaims claims, String tokenValue) {
        Optional<UUID> identifier = UUIDUtilities.fromString(claims.getIdentifier());

        //TODO 3/26/2022 Cleanup usage of optional
        if(identifier.isPresent()) {
            return tokenAuthorizationRepository.findById(identifier.get())
                    .map(TokenAuthorization::getToken)
                    .flatMap(token -> {
                        if (token.hasReachedMaxUsage()) {
                            if (LOG.isDebugEnabled()) {
                                LOG.debug(
                                        "Authentication Token for actor {} has reached its maximum usage.",
                                        claims.getAudience());
                            }

                            return Mono.error(new AuthenticationException(
                                    String.format("Authentication Token for actor %s has reached its maximum usage.", claims.getAudience())));
                        }

                        token.touch();

                        return tokenRepository.updateTouches(token.getId(), token.getTouches())
                                .flatMap(count -> createAuthenticatedActor(claims, tokenValue, token));

                    })
                    .switchIfEmpty(
                            Mono.error(new AuthenticationException(
                                    String.format("Authentication Actor %s not found.", claims.getAudience()))))
                    .block();
        }

        return null; //Optional.empty();
    }

    protected abstract Mono<PlatformAuthenticatedActor<T>> createAuthenticatedActor(JsonWebTokenClaims claims, String tokenValue, Token token);
}
