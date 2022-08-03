package com.objectcomputing.checkins.security.authentication.token.endpoint;

import com.objectcomputing.geoai.platform.auth.exceptions.UnauthorizedError;
import com.objectcomputing.geoai.platform.auth.operations.AuthSessionHandler;
import com.objectcomputing.geoai.platform.security.config.StatefulSynchronizationConfiguration;
import com.objectcomputing.geoai.platform.security.rules.RequiresStatefulDomain;
import com.objectcomputing.geoai.platform.token.commons.AuthorizationToken;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.session.Session;
import reactor.core.publisher.Mono;


@Controller("/auth/api/synchronize")
public class StatefulSynchronizationController {

    AuthSessionHandler sessionHandler;
    StatefulSynchronizationConfiguration statefulSynchronizationConfiguration;

    public StatefulSynchronizationController(AuthSessionHandler sessionHandler, StatefulSynchronizationConfiguration statefulSynchronizationConfiguration) {
        this.sessionHandler = sessionHandler;
        this.statefulSynchronizationConfiguration = statefulSynchronizationConfiguration;
    }

    /**
     *
     * Pull the AuthorizationToken from the session store and return it to the browser.
     *
     * @apiNote The primary purpose of this endpoint is to provide a mechanism for a user
     * to recover their JWT access token when transitioning between applications.
     *
     * @apiNote Calls to this endpoint MUST come from a domain listed in the `security.stateful.domain` configuration poperty
     *
     * @param session the Session
     * @return Authorization token
     */
    @Post("/token")
    @RequiresStatefulDomain
    Mono<AuthorizationToken> restoreAuthorizationTokenFromSession(Session session) {
        return Mono.justOrEmpty(sessionHandler.getAuthToken(session))
                .switchIfEmpty(Mono.error(new UnauthorizedError("Unauthorized")));
    }
}
