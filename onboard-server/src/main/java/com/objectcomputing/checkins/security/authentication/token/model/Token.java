package com.objectcomputing.checkins.security.authentication.token.model;

import com.objectcomputing.geoai.core.accessor.AccessorSource;
import com.objectcomputing.geoai.core.time.TimeToLive;
import com.objectcomputing.geoai.core.time.TimeToLiveConverter;
import com.objectcomputing.geoai.security.token.TokenRoot;
import io.micronaut.data.annotation.*;
import io.micronaut.data.model.DataType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static io.micronaut.data.annotation.Relation.Kind.ONE_TO_MANY;

@Data
@MappedEntity("token")
public class Token implements TokenRoot {
    public static final TimeToLive DEFAULT_TIME_TO_LIVE = new TimeToLive(1000L * 60L * 60L);

    @Id
    @Column(name="token_id")
    @AutoPopulated
    @GeneratedValue(GeneratedValue.Type.AUTO)
    private UUID id;

    @Column(name="parent_token_id")
    private UUID parentTokenId;

    @Column(name="accessor_id")
    private UUID accessorId;

    @Column(name="accessor_source")
    @Enumerated(EnumType.STRING)
    private AccessorSource accessorSource;

    @Column(name="role_name")
    private String roleName;

    @Column(name="display_name")
    private String displayName;

    @Column(name="renewable")
    private boolean renewable = true;

    @Column(name="time_to_live")
    @TypeDef(type = DataType.LONG, converter = TimeToLiveConverter.class)
    private TimeToLive timeToLive;

    @Column(name="type")
    @Enumerated(EnumType.STRING)
    private TokenType type;

    @Column(name="created_instant")
    private Instant createdInstant;

    @Column(name="not_before_instant")
    private Instant notBeforeInstant;

    @Column(name="max_number_of_uses")
    private Integer maxNumberOfUses;

    @Column(name="max_time_to_live")
    @TypeDef(type = DataType.LONG, converter = TimeToLiveConverter.class)
    private TimeToLive maxTimeToLive;

    @Column(name="touches")
    private long touches = 0;

    @Relation(value = ONE_TO_MANY, mappedBy = "token")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<TokenPolicy> policies = new ArrayList<>();

    @Relation(value = ONE_TO_MANY, mappedBy = "token")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<TokenMetadata> meta = new ArrayList<>();

    public Token() {
    }

    public Token(UUID parentTokenId, UUID accessorId, AccessorSource accessorSource, String roleName, String displayName, TokenType type, TimeToLive timeToLive, boolean renewable, List<TokenPolicy> policies, List<TokenMetadata> meta, Integer maxNumberOfUses, TimeToLive maxTimeToLive, Instant createdInstant) {
        this();

        this.parentTokenId = parentTokenId;
        this.accessorId = accessorId;
        this.accessorSource = accessorSource;
        this.roleName = roleName;
        this.displayName = displayName != null ? displayName : roleName;
        this.type = type;
        this.timeToLive = timeToLive;
        this.renewable = renewable;
        this.policies = policies;
        this.meta = meta;
        this.maxNumberOfUses = maxNumberOfUses;
        this.maxTimeToLive = maxTimeToLive;
        this.createdInstant = createdInstant;
    }

    public Token(UUID accessorId, AccessorSource accessorSource, String roleName, String displayName, TokenType type, TimeToLive timeToLive, boolean renewable, List<TokenPolicy> policies, List<TokenMetadata> meta, Integer maxNumberOfUses, TimeToLive maxTimeToLive, Instant createdInstant) {
        this(null, accessorId, accessorSource, roleName, displayName, type, timeToLive, renewable, policies, meta, maxNumberOfUses, maxTimeToLive, createdInstant);
    }

    public Token(UUID parentTokenId, UUID accessorId, AccessorSource accessorSource, String roleName, TokenType type, TimeToLive timeToLive, boolean renewable, Instant createdInstant) {
        this(parentTokenId, accessorId, accessorSource, roleName, null, type, timeToLive, renewable, new LinkedList<>(), new LinkedList<>(), null, null, createdInstant);
    }

    public Token(UUID accessorId, AccessorSource accessorSource, String roleName, TokenType type, TimeToLive timeToLive, boolean renewable, Instant createdInstant) {
        this(null, accessorId, accessorSource, roleName, type, timeToLive, renewable, createdInstant);
    }

    @Transient
    public boolean isAvailable() {
        return null == getNotBeforeInstant() || getNotBeforeInstant().isBefore(Instant.now());
    }

    @Transient
    public void touch() {
        touches++;
    }

    @Transient
    public boolean hasReachedMaxUsage() {
        if (null != getMaxNumberOfUses() && getMaxNumberOfUses() < getTouches()) {
            return true;
        }

        if (null != getMaxTimeToLive()) {
            if (getExpiredTimeFrom(getCreatedInstant(), getMaxTimeToLive()).isBefore(Instant.now())) {
                return true;
            }
        }
        return false;
    }

   private Instant getExpiredTimeFrom(Instant instant, TimeToLive timeToLive) {
        return instant.plus(timeToLive.getTime(), ChronoUnit.MILLIS);
    }
}