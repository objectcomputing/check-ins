package com.objectcomputing.checkins.security.authentication;

import io.micronaut.core.order.Ordered;
import io.micronaut.http.HttpRequest;
import org.reactivestreams.Publisher;

public interface AuthenticatedActorFetcher extends Ordered {
    /**
     * Attempts to read an {@link AbstractAuthenticatedActor} from a {@link HttpRequest} being executed.
     *
     * @param request {@link HttpRequest} being executed.
     * @return {@link AbstractAuthenticatedActor} if found
     */
    Publisher<AuthenticatedActor> fetchAuthenticatedActor(HttpRequest<?> request);


}