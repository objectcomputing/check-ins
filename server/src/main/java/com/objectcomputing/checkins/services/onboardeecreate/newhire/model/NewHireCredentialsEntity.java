package com.objectcomputing.checkins.services.onboardeecreate.newhire.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.micronaut.data.annotation.*;
import io.micronaut.data.model.DataType;

import javax.persistence.Column;
import java.util.Objects;
import java.util.UUID;

import static io.micronaut.data.annotation.Relation.Kind.ONE_TO_ONE;

@MappedEntity("new_hire_credentials")
public class NewHireCredentialsEntity {
    @Id
    @Column(name="new_hire_credentials_id")
    @AutoPopulated
    @TypeDef(type= DataType.STRING)
    @GeneratedValue(GeneratedValue.Type.AUTO)
    private UUID id;

    @Column(name="salt")
    private String salt;

    @Column(name="verifier")
    private String verifier;

    @Relation(value = ONE_TO_ONE)
    @Column(name="new_hire_account_id")
    @JsonIgnore
    private NewHireAccountEntity newHireAccount;

    public NewHireCredentialsEntity() {
    }

    public NewHireCredentialsEntity(NewHireAccountEntity newHireAccount, String salt, String verifier) {
        this();

        this.newHireAccount = newHireAccount;
        this.salt = salt;
        this.verifier = verifier;
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
        NewHireCredentialsEntity that = (NewHireCredentialsEntity) o;
        return Objects.equals(id, that.id) && Objects.equals(salt, that.salt) && Objects.equals(verifier, that.verifier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, salt, verifier);
    }
}
