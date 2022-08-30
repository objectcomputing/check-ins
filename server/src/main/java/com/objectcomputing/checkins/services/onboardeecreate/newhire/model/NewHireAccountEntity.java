package com.objectcomputing.checkins.services.onboardeecreate.newhire.model;

import com.objectcomputing.checkins.services.onboardeecreate.commons.Account;
import com.objectcomputing.checkins.services.onboardeecreate.commons.AccountState;
import com.objectcomputing.checkins.services.onboardeecreate.commons.Identifiable;
import io.micronaut.data.annotation.*;
import io.micronaut.data.model.DataType;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.time.Instant;
import java.util.UUID;

import static io.micronaut.data.annotation.Relation.Kind.ONE_TO_ONE;

@MappedEntity("new_hire_account")
public class NewHireAccountEntity implements Identifiable, Account {
    @Id
    @Column(name="new_hire_account_id")
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

    @Relation(value = ONE_TO_ONE, mappedBy = "newHireAccount")
    private NewHireCredentialsEntity newHireCredentials;

    public NewHireAccountEntity() {
    }

    public NewHireAccountEntity(String emailAddress, AccountState state, Instant currentInstant, NewHireCredentialsEntity newHireCredentials) {
        this();

        this.emailAddress = emailAddress;
        this.state = state;
        this.createdInstant = currentInstant;
        this.changedInstant = currentInstant;
        this.newHireCredentials = newHireCredentials;
    }
    public NewHireAccountEntity(UUID id, String emailAddress, AccountState state, Instant currentInstant, NewHireCredentialsEntity newHireCredentials) {
        this(emailAddress, state, currentInstant, newHireCredentials);

        this.id = id;
    }

    public NewHireAccountEntity(String emailAddress, AccountState state, Instant currentInstant) {
        this(emailAddress, state, currentInstant, null);
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

    public NewHireCredentialsEntity getNewHireCredentials() {
        return newHireCredentials;
    }

    public void setNewHireCredentials(NewHireCredentialsEntity newHireCredentials) {
        this.newHireCredentials = newHireCredentials;
    }
}
