package com.objectcomputing.checkins.security.authentication.token.model;

import io.micronaut.data.annotation.*;
import lombok.Data;

import javax.persistence.Column;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static io.micronaut.data.annotation.Relation.Kind.MANY_TO_ONE;

@Data
@MappedEntity("token_authorization")
public class TokenAuthorization {
    @Id
    @Column(name="token_authorization_id")
    @AutoPopulated
    @GeneratedValue(GeneratedValue.Type.AUTO)
    private UUID id;

    @Relation(MANY_TO_ONE)
    @Column(name = "token_id")
    private Token token;

    @Column(name="created_instant")
    private Instant createdInstant;

    @Column(name="issued_instant")
    private Instant issuedInstant;

    @Column(name="lease")
    private long lease;

    @Column(name="not_before_time")
    private Instant notBeforeTime;

    public TokenAuthorization() {
    }

    public TokenAuthorization(Token token, Instant createdInstant, Instant issuedInstant, long lease, Instant notBeforeTime) {
        this();

        this.token = token;
        this.createdInstant = createdInstant;
        this.issuedInstant = issuedInstant;
        this.lease = lease;
        this.notBeforeTime = notBeforeTime;
    }

    @Transient
    public boolean hasExpired() {
        return getExpirationInstant().isBefore(Instant.now());
    }

    @Transient
    public boolean isAvailable() {
        return null == getNotBeforeTime() || getNotBeforeTime().isBefore(Instant.now());
    }

    @Transient
    public Instant getExpirationInstant() {
        return this.getIssuedInstant().plus(getLease(), ChronoUnit.MILLIS);
    }
}
