package com.objectcomputing.checkins.security.authentication.token;

import com.objectcomputing.geoai.core.accessor.Accessor;
import com.objectcomputing.geoai.core.text.DateTimeUtils;
import com.objectcomputing.geoai.core.time.TimeToLive;
import com.objectcomputing.geoai.platform.token.endpoint.TokenConfig;
import com.objectcomputing.geoai.platform.token.model.*;
import jakarta.inject.Singleton;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.objectcomputing.geoai.platform.token.model.Token.DEFAULT_TIME_TO_LIVE;

@Singleton
public class TokenService {

    public static final String DEFAULT_AUTHENTICATION_ROLE_NAME = "Authentication";
    private final TokenRepository tokenRepository;
    private final TokenAuthorizationRepository tokenAuthorizationRepository;
    private final TokenPolicyRepository tokenPolicyRepository;
    private final TokenMetadataRepository tokenMetadataRepository;

    public TokenService(
            TokenRepository tokenRepository,
            TokenAuthorizationRepository tokenAuthorizationRepository,
            TokenPolicyRepository tokenPolicyRepository,
            TokenMetadataRepository tokenMetadataRepository) {
        this.tokenRepository = tokenRepository;
        this.tokenAuthorizationRepository = tokenAuthorizationRepository;
        this.tokenPolicyRepository = tokenPolicyRepository;
        this.tokenMetadataRepository = tokenMetadataRepository;
    }

    public Mono<TokenAuthorization> getOrCreateTokenAuthorizationFor(Accessor accessor) {
        return tokenRepository.findByAccessorIdAndAccessorSourceAndType(accessor.getId(), accessor.getSource(), TokenType.Authentication)
                .flatMap(token -> {
                    if (token.hasReachedMaxUsage()) {
                        return createTokenAndAuthorizationFrom(accessor, token);
                    } else {
                        return renewTokenAuthorization(token);
                    }
                })
                .switchIfEmpty(createTokenAndAuthorization(accessor));
    }

    private Mono<TokenAuthorization> createTokenAndAuthorizationFrom(Accessor accessor, Token token) {
        final Instant now = Instant.now();

        return invalidateToken(token)
                .flatMap(success -> tokenRepository.save(
                        new Token(accessor.getId(), accessor.getSource(), token.getRoleName(), token.getDisplayName(),
                                TokenType.Authentication, token.getTimeToLive(), token.isRenewable(), null, null,
                                token.getMaxNumberOfUses(), token.getMaxTimeToLive(), now)))
                .flatMap(newToken -> {
                    return Mono.just(newToken)
                            .then(transferPolicies(token, newToken))
                            .then(transferMetas(token, newToken))
                            .flatMap(v ->
                                    tokenAuthorizationRepository.save(
                                            new TokenAuthorization(
                                                    newToken, now, now, newToken.getTimeToLive().getTime(), newToken.getNotBeforeInstant())));
                });
    }

    private Mono<TokenAuthorization> createTokenAndAuthorization(Accessor accessor) {
        final Instant now = Instant.now();

        return tokenRepository.save(
                        new Token(accessor.getId(), accessor.getSource(), DEFAULT_AUTHENTICATION_ROLE_NAME,
                                TokenType.Authentication, DEFAULT_TIME_TO_LIVE, true, now))
                .flatMap(token ->
                        tokenAuthorizationRepository.save(new TokenAuthorization(
                                token, now, now, token.getTimeToLive().getTime(), token.getNotBeforeInstant())));
    }

    private Mono<Void> transferPolicies(Token token, Token newToken) {
        if (null != token.getMeta()) {
            return Mono.empty();
        }

        return Flux.fromIterable(token.getPolicies())
                .map(policy -> new TokenPolicy(token, policy.getName()))
                .collectList()
                .flatMapMany(tokenPolicyRepository::saveAll)
                .then();
   }

    private Mono<Void> transferMetas(Token token, Token newToken) {
        if (null != token.getMeta()) {
            return Mono.empty();
        }

        return Flux.fromIterable(token.getMeta())
                .map(meta -> new TokenMetadata(newToken, meta.getKey(), meta.getValue()))
                .collectList()
                .flatMapMany(tokenMetadataRepository::saveAll)
                .then();
    }

    public Mono<TokenAuthorization> renewTokenAuthorization(Token token) {
        final Instant now = Instant.now();

        return tokenAuthorizationRepository.findActiveAuthorizationTokenId(token.getId())
                .flatMap(tokenAuthorization -> {

                    token.touch();
                    tokenAuthorization.setIssuedInstant(now);

                    return tokenRepository.updateTouches(token.getId(), token.getTouches())
                            .flatMap(tokenCount -> tokenAuthorizationRepository.updateIssuedInstant(tokenAuthorization.getId(), tokenAuthorization.getIssuedInstant()))
                            .flatMap(authorizationCount -> {
                                tokenAuthorization.setToken(token);
                                return Mono.just(tokenAuthorization);
                            });
                });
    }

    private Mono<Object> invalidateToken(Token token) {
        return Mono.just(Boolean.TRUE);
    }

    public Mono<TokenAuthorization> touchTokenAuthorization(Token token) {
        return tokenAuthorizationRepository.findActiveAuthorizationTokenId(token.getId())
                .flatMap(tokenAuthorization -> {

                    token.touch();

                    return tokenRepository.updateTouches(token.getId(), token.getTouches())
                            .thenReturn(tokenAuthorization);
                });
    }

    public Mono<TokenAuthorization> create(Accessor accessor, TokenConfig tokenConfig, List<String> warnings) {
        final Instant now = Instant.now();

        final TimeToLive timeToLive = parseTimeToLive(tokenConfig.getTimeToLive()).orElse(DEFAULT_TIME_TO_LIVE);
        final TimeToLive maxTimeToLive = parseTimeToLive(tokenConfig.getMaxTimeToLive()).orElse(null);
        final TokenType type = Optional.ofNullable(tokenConfig.getType()).orElse(TokenType.AuthorizationPolicy);

        return tokenRepository.save(
                new Token(accessor.getId(), accessor.getSource(), tokenConfig.getRoleName(), tokenConfig.getDisplayName(),
                        type, timeToLive, tokenConfig.isRenewable(), null, null,
                        tokenConfig.getMaxNumberOfUses(), maxTimeToLive, now))
                .flatMap(token -> {
                    return Mono.just(token)
                            .then(createPolicies(token, tokenConfig.getPolicies()))
                            .then(createMetas(token, tokenConfig.getMeta()))
                            .flatMap(v ->
                                    tokenAuthorizationRepository.save(
                                            new TokenAuthorization(
                                                    token, now, now, token.getTimeToLive().getTime(), token.getNotBeforeInstant())));
                });
    }

    private Mono<Void> createPolicies(Token token, List<String> policies) {
        return Flux.fromIterable(policies)
                .map(policy -> new TokenPolicy(token, policy))
                .collectList()
                .flatMapMany(tokenPolicyRepository::saveAll)
                .then();
    }

    private Mono<Void> createMetas(Token token, Map<String, String> meta) {
        return Flux.fromIterable(meta.entrySet())
                .map(entry -> new TokenMetadata(token, entry.getKey(), entry.getValue()))
                .collectList()
                .flatMapMany(tokenMetadataRepository::saveAll)
                .then();
    }

    // TODO Remove dependency on DateTimeUtils 3/4/2022
    private Optional<TimeToLive> parseTimeToLive(String ttlText) {
        try {
            return Optional.ofNullable(DateTimeUtils.parseTimeToLive(ttlText));
        } catch (Throwable ignore) {
        }
        return Optional.empty();
    }
}