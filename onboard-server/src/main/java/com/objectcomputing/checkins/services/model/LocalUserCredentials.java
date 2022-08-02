package com.objectcomputing.checkins.services.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.micronaut.data.annotation.*;

import javax.persistence.Column;
import java.util.Objects;
import java.util.UUID;

import static io.micronaut.data.annotation.Relation.Kind.ONE_TO_ONE;

@MappedEntity("local_user_credentials")
public class LocalUserCredentials {
    @Id
    @Column(name="local_user_credentials_id")
    @AutoPopulated
    @GeneratedValue(GeneratedValue.Type.AUTO)
    private UUID id;

    @Column(name="salt")
    private String salt;

    @Column(name="primary_verifier")
    private String primaryVerifier;

    @Column(name="secondary_verifier")
    private String secondaryVerifier;

    @Relation(value = ONE_TO_ONE)
    @Column(name="user_account_id")
    @JsonIgnore
    private LoginAccount loginAccount;

    public LocalUserCredentials() {
    }

    public LocalUserCredentials(LoginAccount loginAccount, String salt, String primaryVerifier, String secondaryVerifier) {
        this();

        this.loginAccount = loginAccount;
        this.salt = salt;
        this.primaryVerifier = primaryVerifier;
        this.secondaryVerifier = secondaryVerifier;
    }

    public LocalUserCredentials(LoginAccount loginAccount, String salt, String primaryVerifier) {
        this(loginAccount, salt, primaryVerifier, null);
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

    public String getPrimaryVerifier() {
        return primaryVerifier;
    }

    public void setPrimaryVerifier(String primaryVerifier) {
        this.primaryVerifier = primaryVerifier;
    }

    public String getSecondaryVerifier() {
        return secondaryVerifier;
    }

    public void setSecondaryVerifier(String secondaryVerifier) {
        this.secondaryVerifier = secondaryVerifier;
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
        LocalUserCredentials that = (LocalUserCredentials) o;
        return Objects.equals(id, that.id) && Objects.equals(salt, that.salt) && Objects.equals(primaryVerifier, that.primaryVerifier) && Objects.equals(secondaryVerifier, that.secondaryVerifier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, salt, primaryVerifier, secondaryVerifier);
    }

    @Override
    public String toString() {
        return "LocalUserCredentials{" +
                "id=" + id +
                ", salt='" + salt + '\'' +
                ", primaryVerifier='" + primaryVerifier + '\'' +
                ", secondaryVerifier='" + secondaryVerifier + '\'' +
                '}';
    }
}
