package com.objectcomputing.checkins.services.system.model;


import com.objectcomputing.checkins.services.commons.accessor.Accessor;
import com.objectcomputing.checkins.services.commons.accessor.AccessorSource;
import com.objectcomputing.checkins.services.commons.account.Account;
import com.objectcomputing.checkins.services.commons.account.AccountRole;
import com.objectcomputing.checkins.services.commons.account.AccountState;
import com.objectcomputing.checkins.services.commons.account.Identifiable;

import io.micronaut.data.annotation.*;


import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;


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

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    @Override
    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getVerifier() {
        return verifier;
    }

    public void setVerifier(String verifier) {
        this.verifier = verifier;
    }

    public String getRequester() {
        return requester;
    }

    public void setRequester(String requester) {
        this.requester = requester;
    }

    public Instant getCreatedInstant() {
        return createdInstant;
    }

    public void setCreatedInstant(Instant createdInstant) {
        this.createdInstant = createdInstant;
    }

    public Instant getChangedInstant() {
        return changedInstant;
    }

    public void setChangedInstant(Instant changedInstant) {
        this.changedInstant = changedInstant;
    }

    @Override
    public AccountState getState() {
        return state;
    }

    public void setState(AccountState state) {
        this.state = state;
    }

    @Override
    public AccountRole getRole() {
        return role;
    }

    public void setRole(AccountRole role) {
        this.role = role;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SystemAccount that = (SystemAccount) o;
        return Objects.equals(id, that.id) && Objects.equals(identity, that.identity) && Objects.equals(salt, that.salt) && Objects.equals(verifier, that.verifier) && Objects.equals(requester, that.requester) && Objects.equals(createdInstant, that.createdInstant) && Objects.equals(changedInstant, that.changedInstant) && state == that.state && role == that.role;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, identity, salt, verifier, requester, createdInstant, changedInstant, state, role);
    }

    @Override
    public String toString() {
        return "SystemAccount{" +
                "id=" + id +
                ", identity='" + identity + '\'' +
                ", salt='" + salt + '\'' +
                ", verifier='" + verifier + '\'' +
                ", requester='" + requester + '\'' +
                ", createdInstant=" + createdInstant +
                ", changedInstant=" + changedInstant +
                ", state=" + state +
                ", role=" + role +
                '}';
    }
}
