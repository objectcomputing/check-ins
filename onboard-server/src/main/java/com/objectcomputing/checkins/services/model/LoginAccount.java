package com.objectcomputing.checkins.services.model;

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
import java.util.UUID;

import static io.micronaut.data.annotation.Relation.Kind.*;

@MappedEntity("user_account")
public class LoginAccount implements Identifiable, Account {
    @Id
    @Column(name="user_account_id")
    @AutoPopulated
    @GeneratedValue(GeneratedValue.Type.AUTO)
    private UUID id;

    @Column(name="email_address")
    private String emailAddress;

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

    @Relation(value = ONE_TO_ONE, mappedBy = "loginAccount")
    private LocalUserCredentials localUserCredentials;

    public LoginAccount() {
    }

    public LoginAccount(String emailAddress, AccountState state, AccountRole role, Instant currentInstant, LocalUserCredentials localUserCredentials) {
        this();

        this.emailAddress = emailAddress;
        this.state = state;
        this.role = role;
        this.createdInstant = currentInstant;
        this.changedInstant = currentInstant;
        this.localUserCredentials = localUserCredentials;
    }

    public LoginAccount(String emailAddress, AccountState state, AccountRole role, Instant currentInstant) {
        this(emailAddress, state, role, currentInstant, null);
    }

    @Transient
    @Override
    public Accessor asAccessor() {
        return new Accessor(getId(), AccessorSource.User);
    }

    @Transient
    @Override
    public String getIdentity() {
        return getEmailAddress();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
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

    public AccountState getState() {
        return state;
    }

    public void setState(AccountState state) {
        this.state = state;
    }

    public AccountRole getRole() {
        return role;
    }

    public void setRole(AccountRole role) {
        this.role = role;
    }

    public LocalUserCredentials getLocalUserCredentials() {
        return localUserCredentials;
    }

    public void setLocalUserCredentials(LocalUserCredentials localUserCredentials) {
        this.localUserCredentials = localUserCredentials;
    }

    @Override
    public String toString() {
        return "LoginAccount{" +
                "id=" + id +
                ", emailAddress='" + emailAddress + '\'' +
                ", createdInstant=" + createdInstant +
                ", changedInstant=" + changedInstant +
                ", state=" + state +
                ", role=" + role +
                ", localUserCredentials=" + localUserCredentials +
                '}';
    }
}
