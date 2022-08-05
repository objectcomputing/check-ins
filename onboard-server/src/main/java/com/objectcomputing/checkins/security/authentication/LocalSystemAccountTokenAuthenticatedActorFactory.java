package com.objectcomputing.checkins.security.authentication;

import com.objectcomputing.checkins.security.authentication.token.model.TokenAuthorizationRepository;
import com.objectcomputing.checkins.security.authentication.token.model.TokenRepository;
import com.objectcomputing.checkins.security.config.ClaimsConfiguration;
import com.objectcomputing.checkins.services.system.model.SystemAccountRepository;
import com.objectcomputing.geoai.core.accessor.AccessorSource;
import com.objectcomputing.geoai.platform.system.model.SystemAccount;
import com.objectcomputing.geoai.platform.system.model.SystemAccountRepository;
import com.objectcomputing.geoai.platform.token.model.Token;
import com.objectcomputing.geoai.platform.token.model.TokenAuthorizationRepository;
import com.objectcomputing.geoai.platform.token.model.TokenRepository;
import com.objectcomputing.geoai.security.authentication.config.ClaimsConfiguration;
import com.objectcomputing.geoai.security.token.TokenAuthenticatedActorFactory;
import com.objectcomputing.geoai.security.token.jwt.JsonWebTokenClaims;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.util.Optional;


@Singleton
public class LocalSystemAccountTokenAuthenticatedActorFactory extends AbstractLocalPlatformAccountTokenAuthenticatedActorFactory<SystemAccount> {
    private static final Logger LOG = LoggerFactory.getLogger(TokenAuthenticatedActorFactory.class);

    private final SystemAccountRepository accountRepository;

    public LocalSystemAccountTokenAuthenticatedActorFactory(ClaimsConfiguration claimsConfiguration,
                                                            SystemAccountRepository accountRepository,
                                                            TokenRepository tokenRepository,
                                                            TokenAuthorizationRepository tokenAuthorizationRepository) {
        super(claimsConfiguration, tokenRepository, tokenAuthorizationRepository);
        this.accountRepository = accountRepository;

    }
    @Override
    public boolean supports(JsonWebTokenClaims claims) {
        Optional<String> accessorSource = claims.getPublicClaim(claimsConfiguration.getAccessorSource(), String.class);
        return accessorSource.filter(s -> AccessorSource.System == AccessorSource.valueOf(s)).isPresent();
    }

    @Override
    protected Mono<PlatformAuthenticatedActor<SystemAccount>> createAuthenticatedActor(JsonWebTokenClaims claims, String tokenValue, Token token) {
        return accountRepository.findById(token.getAccessorId())
                .flatMap(account ->
                        Mono.just(new SystemAccountAuthenticatedActor(claims, account, tokenValue, token)));
    }
}
