package com.objectcomputing.checkins.services.system.model;

import com.objectcomputing.geoai.core.accessor.Accessor;
import com.objectcomputing.geoai.core.accessor.AccessorSource;
import com.objectcomputing.geoai.core.account.Account;
import com.objectcomputing.geoai.core.account.AccountRole;
import com.objectcomputing.geoai.core.account.AccountState;
import com.objectcomputing.geoai.core.identity.Identifiable;
import io.micronaut.data.annotation.*;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.time.Instant;
import java.util.UUID;

@Data
@MappedEntity("system_account")
public class SystemAccount implements Identifiable, Account {
    @Id
    @Column(name="system_account_id")
    @AutoPopulated
    @GeneratedValue(GeneratedValue.Type.AUTO)
    private UUID id;

    @Column(name="identity")
    private String identity;

    @Column(name="salt")
    private String salt;

    @Column(name="verifier")
    private String verifier;

    @Column(name="requester")
    private String requester;

    @Column(name="created_instant")
    private Instant createdInstant;

    @Column(name="changed_instant")
    private Instant changedInstant;

    @Column(name="state")
    @Enumerated(EnumType.STRING)
    private AccountState state;

    @Column(name="role")
    @Enumerated(EnumType.STRING)
    private AccountRole role;

    public SystemAccount() {
    }

    public SystemAccount(String identity, String salt, String verifier, String requester, AccountRole role, AccountState state, Instant createdInstant) {
        this();

        this.identity = identity;
        this.salt = salt;
        this.verifier = verifier;
        this.requester = requester;
        this.role = role;
        this.state = state;
        this.createdInstant = createdInstant;
        this.changedInstant = createdInstant;
    }

    @Transient
    @Override
    public Accessor asAccessor() {
        return new Accessor(getId(), AccessorSource.System);
    }
}
