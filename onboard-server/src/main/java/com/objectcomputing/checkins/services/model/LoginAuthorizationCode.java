package com.objectcomputing.checkins.services.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.objectcomputing.checkins.services.time.TimeToLive;
import com.objectcomputing.checkins.services.time.TimeToLiveConverter;
import io.micronaut.data.annotation.*;
import io.micronaut.data.model.DataType;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

import static io.micronaut.data.annotation.Relation.Kind.ONE_TO_ONE;

@MappedEntity("user_authorization_code")
public class LoginAuthorizationCode {
    public static final TimeToLive DEFAULT_TIME_TO_LIVE = new TimeToLive(1000L * 60L * 15L); // 15 minutes

    @Id
    @Column(name="user_authorization_code_id")
    @AutoPopulated
    @GeneratedValue(GeneratedValue.Type.AUTO)
    private UUID id;

    @Column(name="salt")
    private String salt;

    @Column(name="verifier")
    private String verifier;

    @Column(name="purpose")
    @Enumerated(EnumType.STRING)
    private LoginAuthorizationPurpose purpose;

    @Column(name="source")
    @Enumerated(EnumType.STRING)
    private LoginAuthorizationSource source;

    @Column(name="issued_instant")
    private Instant issuedInstant;

    @Column(name="time_to_live")
    @TypeDef(type = DataType.LONG, converter = TimeToLiveConverter.class)
    private TimeToLive timeToLive;

    @Column(name="consumed_instant")
    private Instant consumedInstant;

    @Relation(value = ONE_TO_ONE)
    @Column(name="user_account_id")
    @JsonIgnore
    private LoginAccount loginAccount;

    public LoginAuthorizationCode() {
    }

    public LoginAuthorizationCode(LoginAccount loginAccount, String salt, String verifier, LoginAuthorizationPurpose purpose, LoginAuthorizationSource source, Instant issuedInstant, TimeToLive timeToLive) {
        this();

        this.loginAccount = loginAccount;
        this.salt = salt;
        this.verifier = verifier;
        this.purpose = purpose;
        this.source = source;
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

    public LoginAuthorizationPurpose getPurpose() {
        return purpose;
    }

    public void setPurpose(LoginAuthorizationPurpose purpose) {
        this.purpose = purpose;
    }

    public LoginAuthorizationSource getSource() {
        return source;
    }

    public void setSource(LoginAuthorizationSource source) {
        this.source = source;
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

    public LoginAccount getLoginAccount() {
        return loginAccount;
    }

    public void setLoginAccount(LoginAccount loginAccount) {
        this.loginAccount = loginAccount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LoginAuthorizationCode that = (LoginAuthorizationCode) o;
        return Objects.equals(id, that.id) && Objects.equals(salt, that.salt) && Objects.equals(verifier, that.verifier) && purpose == that.purpose && source == that.source && Objects.equals(issuedInstant, that.issuedInstant) && Objects.equals(timeToLive, that.timeToLive) && Objects.equals(consumedInstant, that.consumedInstant);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, salt, verifier, purpose, source, issuedInstant, timeToLive, consumedInstant);
    }

    @Override
    public String toString() {
        return "LoginAuthorizationCode{" +
                "id=" + id +
                ", salt='" + salt + '\'' +
                ", verifier='" + verifier + '\'' +
                ", purpose=" + purpose +
                ", source=" + source +
                ", issuedInstant=" + issuedInstant +
                ", timeToLive=" + timeToLive +
                ", consumedInstant=" + consumedInstant +
                '}';
    }
}
