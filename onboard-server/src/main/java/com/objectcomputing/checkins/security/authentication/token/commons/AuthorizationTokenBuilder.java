package com.objectcomputing.checkins.security.authentication.token.commons;

import com.objectcomputing.checkins.services.commons.account.Account;
import com.objectcomputing.checkins.auth.AuthSettings;
import com.objectcomputing.checkins.security.authentication.token.model.TokenAuthorization;
import com.objectcomputing.checkins.security.authentication.token.model.TokenMetadata;
import com.objectcomputing.checkins.security.authentication.token.model.TokenPolicy;
import com.objectcomputing.geoai.security.token.jwt.JsonWebTokenPayload;
import com.objectcomputing.geoai.security.token.jwt.SignedJsonWebToken;
import com.objectcomputing.geoai.security.token.jwt.config.JsonWebTokenConfiguration;
import com.objectcomputing.geoai.security.token.jwt.signature.SignedJsonWebTokenSignatureGenerator;
import jakarta.inject.Singleton;
import org.slf4j.Logger;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Singleton
public class AuthorizationTokenBuilder {
    private static final Logger LOG = AuthSettings.AUTH_LOG;

    private final JsonWebTokenConfiguration tokenConfiguration;
    private final SignedJsonWebTokenSignatureGenerator tokenGenerator;

    public AuthorizationTokenBuilder(JsonWebTokenConfiguration tokenConfiguration,
                                     SignedJsonWebTokenSignatureGenerator tokenGenerator) {

        this.tokenConfiguration = tokenConfiguration;
        this.tokenGenerator = tokenGenerator;
    }

    public AuthorizationToken build(Account account, TokenAuthorization tokenAuthorization) {
        JsonWebTokenPayload payload = buildJsonWebTokenPayloadFor(account.getIdentity(), tokenAuthorization);

        SignedJsonWebToken token = tokenGenerator.sign(payload);

        String tokenText = token.serialize();

        List<String> policies = tokenAuthorization.getToken().getPolicies().stream()
                .map(TokenPolicy::getName).toList();

        Map<String,String> meta = tokenAuthorization.getToken().getMeta().stream()
                .collect(Collectors.toMap(TokenMetadata::getKey, TokenMetadata::getValue));

        return new AuthorizationToken(
                tokenText, tokenAuthorization.getCreatedInstant().toEpochMilli(), tokenAuthorization.getIssuedInstant().toEpochMilli(),
                tokenAuthorization.getLease(), policies, meta, tokenAuthorization.getToken().isRenewable());

    }

    // TODO 2/12/2022 Add Claims Configuration Here for public claim keys
    public JsonWebTokenPayload buildJsonWebTokenPayloadFor(String identity, TokenAuthorization tokenAuthorization) {
        return JsonWebTokenPayload.build(tokenConfiguration.getIssuer())
                .withSubject(tokenAuthorization.getToken().getRoleName())
                .withAudience(identity)
                .withExpirationTime(tokenAuthorization.getExpirationInstant().toEpochMilli())
                .withIssuedAt(tokenAuthorization.getIssuedInstant().toEpochMilli())
                .withIdentifier(tokenAuthorization.getId().toString())
                .withPublicClaim("accessorId", tokenAuthorization.getToken().getAccessorId().toString())
                .withPublicClaim("accessorSource", tokenAuthorization.getToken().getAccessorSource().toString())
                .build();

    }
}
