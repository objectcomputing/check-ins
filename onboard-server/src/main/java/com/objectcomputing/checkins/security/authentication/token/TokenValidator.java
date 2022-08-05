package com.objectcomputing.checkins.security.authentication.token;

import com.objectcomputing.geoai.security.token.jwt.validator.SignedJsonWebTokenValidator;
import io.micronaut.context.annotation.DefaultImplementation;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.core.order.Ordered;
import io.micronaut.http.HttpRequest;
import org.reactivestreams.Publisher;

@DefaultImplementation(SignedJsonWebTokenValidator.class)
public interface TokenValidator<T extends TokenRoot> extends Ordered {
    Publisher<T> validateToken(String token, @Nullable HttpRequest<?> request);

    default Publisher<T> validateToken(String token) {
        return validateToken(token, null);
    }
}
