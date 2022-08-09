package com.objectcomputing.checkins.newhire.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.objectcomputing.checkins.util.time.TimeToLive;
import com.objectcomputing.checkins.util.time.TimeToLiveConverter;
import io.micronaut.data.annotation.*;
import io.micronaut.data.model.DataType;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

import static io.micronaut.data.annotation.Relation.Kind.ONE_TO_ONE;

@MappedEntity("new_hire_authorization_code")
public class NewHireAuthorizationCodeEntity {
    public static final TimeToLive DEFAULT_TIME_TO_LIVE = new TimeToLive(1000L * 60L * 15L); // 15 minutes

    @Id
    @Column(name="new_hire_authorization_code_id")
    @AutoPopulated
    @TypeDef(type=DataType.STRING)
    @GeneratedValue(GeneratedValue.Type.AUTO)
    private UUID id;

    @Column(name="salt")
    private String salt;

    @Column(name="verifier")
    private String verifier;

    @Column(name="purpose")
    @Enumerated(EnumType.STRING)
    private AuthorizationPurpose purpose;

    @Column(name="issued_instant")
    private Instant issuedInstant;

    @Column(name="time_to_live")
    @TypeDef(type = DataType.LONG, converter = TimeToLiveConverter.class)
    private TimeToLive timeToLive;

    @Column(name="consumed_instant")
    private Instant consumedInstant;

    @Relation(value = ONE_TO_ONE)
    @Column(name="new_hire_account_id")
    @JsonIgnore
    private NewHireAccountEntity newHireAccount;

    public NewHireAuthorizationCodeEntity() {
    }

    public NewHireAuthorizationCodeEntity(NewHireAccountEntity newHireAccount, String salt, String verifier, AuthorizationPurpose purpose, Instant issuedInstant, TimeToLive timeToLive) {
        this();

        this.newHireAccount = newHireAccount;
        this.salt = salt;
        this.verifier = verifier;
        this.purpose = purpose;
        this.issuedInstant = issuedInstant;
        this.timeToLive = timeToLive;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
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

    public AuthorizationPurpose getPurpose() {
        return purpose;
    }

    public void setPurpose(AuthorizationPurpose purpose) {
        this.purpose = purpose;
    }

    public Instant getIssuedInstant() {
        return issuedInstant;
    }

    public void setIssuedInstant(Instant issuedInstant) {
        this.issuedInstant = issuedInstant;
    }

    public TimeToLive getTimeToLive() {
        return timeToLive;
    }

    public void setTimeToLive(TimeToLive timeToLive) {
        this.timeToLive = timeToLive;
    }

    public Instant getConsumedInstant() {
        return consumedInstant;
    }

    public void setConsumedInstant(Instant consumedInstant) {
        this.consumedInstant = consumedInstant;
    }

    public NewHireAccountEntity getNewHireAccount() {
        return newHireAccount;
    }

    public void setNewHireAccount(NewHireAccountEntity newHireAccount) {
        this.newHireAccount = newHireAccount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NewHireAuthorizationCodeEntity that = (NewHireAuthorizationCodeEntity) o;
        return Objects.equals(id, that.id) && Objects.equals(salt, that.salt) && Objects.equals(verifier, that.verifier) && purpose == that.purpose && Objects.equals(issuedInstant, that.issuedInstant) && Objects.equals(timeToLive, that.timeToLive) && Objects.equals(consumedInstant, that.consumedInstant);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, salt, verifier, purpose, issuedInstant, timeToLive, consumedInstant);
    }
}
