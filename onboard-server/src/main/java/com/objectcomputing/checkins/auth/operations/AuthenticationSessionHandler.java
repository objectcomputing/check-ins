package com.objectcomputing.checkins.auth.operations;

import com.nimbusds.srp6.SRP6ServerSession;
import com.objectcomputing.checkins.commons.Account;
import com.objectcomputing.checkins.commons.Identifiable;
import com.objectcomputing.checkins.security.authorization.AuthorizationToken;
import io.micronaut.session.Session;
import jakarta.inject.Singleton;
import java.time.Duration;
import java.util.Optional;

@Singleton
public class AuthenticationSessionHandler {
    public static final int LOGIN_TO_AUTH_SESSION_TIMEOUT_IN_SECONDS = 60*10; // Ten Min

    public static final String SECURITY_SERVER_SESSION_KEY = "security_server_session_key";
    public static final String IDENTIFIABLE_SESSION_KEY = "accessor_key";
    public static final String JWT_TOKEN_KEY = "jwt_token_key";

    public void initialize(Session session, SRP6ServerSession srp6session, Identifiable identifiable) {
        session.put(SECURITY_SERVER_SESSION_KEY, srp6session);
        session.put(IDENTIFIABLE_SESSION_KEY, identifiable);
        session.setMaxInactiveInterval(Duration.ofSeconds(LOGIN_TO_AUTH_SESSION_TIMEOUT_IN_SECONDS));
    }

    public void cleanup(Session session) {
        session.remove(SECURITY_SERVER_SESSION_KEY);
        session.remove(IDENTIFIABLE_SESSION_KEY);
    }

    public Optional<SRP6ServerSession> getSrp6Session(Session session) {
        return session.get(SECURITY_SERVER_SESSION_KEY, SRP6ServerSession.class);
    }

    public Optional<Identifiable> getNewHireIdentifiable(Session session) {
        return session.get(IDENTIFIABLE_SESSION_KEY, Identifiable.class);
    }


    public void registerAuthToken(Session session, AuthorizationToken token) {
        session.put(JWT_TOKEN_KEY, token);
    }

    public Optional<AuthorizationToken> getAuthToken(Session session){
        return session.get(JWT_TOKEN_KEY, AuthorizationToken.class);
    }

    public void destroyAuthToken(Session session) {
        session.remove(JWT_TOKEN_KEY);
    }
}
