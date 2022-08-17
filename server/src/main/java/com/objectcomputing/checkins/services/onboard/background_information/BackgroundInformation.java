package com.objectcomputing.checkins.services.onboard.background_information;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.data.annotation.AutoPopulated;
import io.micronaut.data.annotation.TypeDef;
import io.micronaut.data.jdbc.annotation.ColumnTransformer;
import io.micronaut.data.model.DataType;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Objects;
import java.util.UUID;

@Entity
@Introspected
@Table(name = "background_information")
public class BackgroundInformation {

    @Id
    @Column(name = "id")
    @AutoPopulated
    @TypeDef(type = DataType.STRING)
    @Schema(description = "id of the new background information class")
    private UUID id;

    @NotNull
    @Column(name="userid")
    @TypeDef(type=DataType.STRING)
    @Schema(description = "id of account", required = true)
    private UUID userId;

    @NotBlank
    @Column(name = "stepcomplete")
    @Schema(description = "indication of step being complete")
    private Boolean stepComplete;

    public BackgroundInformation(UUID userId, Boolean stepComplete){
        this.userId = userId;
        this.stepComplete = stepComplete;
    }

    public BackgroundInformation(UUID id, UUID userId, Boolean stepComplete){
        this.id = id;
        this.userId = userId;
        this.stepComplete = stepComplete;
    }

    public void setId(UUID id) { this.id = id;}

    public UUID getId() {return id;}

    public void setUserId(UUID userId){this.userId = userId;}

    public UUID getUserId(){return userId;}

    public void setStepComplete(Boolean stepComplete){this.stepComplete = stepComplete;}

    public Boolean getStepComplete(){return stepComplete;}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BackgroundInformation that = (BackgroundInformation) o;
        return Objects.equals(id, that.id) && Objects.equals(userId, that.userId) && Objects.equals(stepComplete, that.stepComplete);
    }

    @Override
    public int hashCode(){
        return Objects.hash(id,userId,stepComplete);
    }
}
