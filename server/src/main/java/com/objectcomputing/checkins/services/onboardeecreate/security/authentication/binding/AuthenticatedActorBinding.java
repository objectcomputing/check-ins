package com.objectcomputing.checkins.services.onboardeecreate.security.authentication.binding;

import com.objectcomputing.checkins.services.onboardeecreate.security.authentication.AuthenticatedActor;
import io.micronaut.context.annotation.Requires;
import io.micronaut.core.convert.ArgumentConversionContext;
import io.micronaut.core.convert.value.MutableConvertibleValues;
import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.bind.binders.TypedRequestArgumentBinder;
import io.micronaut.http.server.HttpServerConfiguration;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.filters.SecurityFilter;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

@Singleton
@Requires(classes = HttpServerConfiguration.class)
public class AuthenticatedActorBinding implements TypedRequestArgumentBinder<AuthenticatedActor> {
    private static final Logger LOG = LoggerFactory.getLogger(AuthenticatedActorBinding.class);

    private final Argument<AuthenticatedActor> argumentType = Argument.of(AuthenticatedActor.class);

    public AuthenticatedActorBinding() {
    }

    @SuppressWarnings("unchecked")
    @Override
    public BindingResult<AuthenticatedActor> bind(ArgumentConversionContext<AuthenticatedActor> context, HttpRequest<?> source) {
        if (!source.getAttributes().contains(SecurityFilter.KEY)) {
            // the filter hasn't been executed
            //noinspection unchecked
            return BindingResult.EMPTY;
        }

        MutableConvertibleValues<Object> attrs = source.getAttributes();
        Optional<Authentication> existing = attrs.get(SecurityFilter.AUTHENTICATION, Authentication.class);
        if (existing.isPresent()) {
            return () -> Optional.of(new AuthenticatedActor()); //existing;
        } else {
            if (!context.getArgument().isNullable()) {
                if (LOG.isDebugEnabled()) {
                    final String method = source.getMethod().toString();
                    final String path = source.getPath();

                    LOG.debug(
                            "Authenticated Actor required but none found for {} {}.",
                            method,
                            path);
                }
            }
            return BindingResult.EMPTY;
        }
    }

    @Override
    public Argument<AuthenticatedActor> argumentType() {
        return argumentType;
    }
}
