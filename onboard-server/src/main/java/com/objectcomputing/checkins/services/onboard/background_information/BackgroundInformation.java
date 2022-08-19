package com.objectcomputing.checkins.services.onboard.background_information;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.objectcomputing.checkins.newhire.model.NewHireAccountEntity;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.data.annotation.AutoPopulated;
import io.micronaut.data.annotation.Relation;
import io.micronaut.data.annotation.TypeDef;
import io.micronaut.data.jdbc.annotation.ColumnTransformer;
import io.micronaut.data.model.DataType;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import java.util.Objects;
import java.util.UUID;

import static io.micronaut.data.annotation.Relation.Kind.ONE_TO_ONE;

@Entity
@Introspected
@Table(name = "background_information")
public class BackgroundInformation {

    @Id
    @Column(name = "background_information_id")
    @AutoPopulated
    @TypeDef(type = DataType.STRING)
    @Schema(description = "id of the new background information class")
    private UUID id;

    @NotBlank
    @Column(name = "step_complete")
    @Schema(description = "indication of step being complete")
    private Boolean stepComplete;

    @Relation(value = ONE_TO_ONE)
    @Column(name="new_hire_account_id")
    @JsonIgnore
    private NewHireAccountEntity newHireAccount;

    public BackgroundInformation() {}

    public BackgroundInformation(NewHireAccountEntity newHireAccount, UUID id, Boolean stepComplete){
        this.id = id;
        this.stepComplete = stepComplete;
        this.newHireAccount = newHireAccount;
    }

    public BackgroundInformation(NewHireAccountEntity newHireAccount, Boolean stepComplete){
        this.stepComplete = stepComplete;
        this.newHireAccount = newHireAccount;
    }

    public void setId(UUID id) { this.id = id;}

    public UUID getId() {return id;}

    public void setStepComplete(Boolean stepComplete){this.stepComplete = stepComplete;}

    public Boolean getStepComplete(){return stepComplete;}

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
        BackgroundInformation that = (BackgroundInformation) o;
        return Objects.equals(id, that.id) && Objects.equals(stepComplete, that.stepComplete);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, stepComplete);
    }
}
