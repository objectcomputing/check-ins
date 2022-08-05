package com.objectcomputing.checkins.security.authentication;

import com.objectcomputing.geoai.core.accessor.AccessorSource;
import com.objectcomputing.geoai.platform.account.model.UserAccount;
import com.objectcomputing.geoai.platform.account.model.UserAccountRepository;
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
public class LocalUserAccountTokenAuthenticatedActorFactory extends AbstractLocalPlatformAccountTokenAuthenticatedActorFactory<UserAccount> {
    private static final Logger LOG = LoggerFactory.getLogger(TokenAuthenticatedActorFactory.class);

    private final UserAccountRepository accountRepository;

    public LocalUserAccountTokenAuthenticatedActorFactory(ClaimsConfiguration claimsConfiguration,
                                                          UserAccountRepository accountRepository,
                                                          TokenRepository tokenRepository,
                                                          TokenAuthorizationRepository tokenAuthorizationRepository) {
        super(claimsConfiguration, tokenRepository, tokenAuthorizationRepository);
        this.accountRepository = accountRepository;

    }
    @Override
    public boolean supports(JsonWebTokenClaims claims) {
        Optional<String> accessorSource = claims.getPublicClaim(claimsConfiguration.getAccessorSource(), String.class);
        return accessorSource.filter(s -> AccessorSource.User == AccessorSource.valueOf(s)).isPresent();
    }

    @Override
    protected Mono<PlatformAuthenticatedActor<UserAccount>> createAuthenticatedActor(JsonWebTokenClaims claims, String tokenValue, Token token) {
        return accountRepository.findById(token.getAccessorId())
                .flatMap(account ->
                        Mono.just(new UserAccountAuthenticatedActor(claims, account, tokenValue, token)));
    }
}
