package com.objectcomputing.checkins.security.authentication.token.jwt.validator;

import com.objectcomputing.geoai.security.authentication.AuthenticationException;
import com.objectcomputing.geoai.security.token.TokenValidator;
import com.objectcomputing.geoai.security.token.jwt.SignedJsonWebToken;
import com.objectcomputing.geoai.security.token.jwt.signature.SignedJsonWebTokenSignatureVerifier;
import io.micronaut.http.HttpRequest;
import jakarta.inject.Singleton;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;

import java.text.ParseException;
import java.util.Collection;

@Singleton
public class SignedJsonWebTokenValidator implements TokenValidator<SignedJsonWebToken> {

    private final Collection<SignedJsonWebTokenSignatureVerifier> signatureVerifiers;

    public SignedJsonWebTokenValidator(Collection<SignedJsonWebTokenSignatureVerifier> signatureVerifiers) {
        this.signatureVerifiers = signatureVerifiers;
    }

    @Override
    public Publisher<SignedJsonWebToken> validateToken(String token, HttpRequest<?> request) {
        return Mono.just(token)
                .flatMap(this::parse);
    }

    private Mono<SignedJsonWebToken> parse(String token) {
        SignedJsonWebToken parsedToken;
        try {
            parsedToken = SignedJsonWebToken.parse(token);
        } catch (ParseException e) {
            return Mono.error(new AuthenticationException("cannot parse Signed JWT Token", e));
        }

        if(signatureVerifiers.stream().anyMatch(v->v.verify(parsedToken))) {
            return Mono.just(parsedToken);
        } else {
            return Mono.empty();
        }


    }
}
