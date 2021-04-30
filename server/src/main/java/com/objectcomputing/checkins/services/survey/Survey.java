package com.objectcomputing.checkins.services.survey;

import java.util.Objects;
import java.util.UUID;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import io.micronaut.data.annotation.AutoPopulated;
import io.micronaut.data.annotation.TypeDef;
import io.micronaut.data.jdbc.annotation.ColumnTransformer;
import io.micronaut.data.model.DataType;
import io.swagger.v3.oas.annotations.media.Schema;

@Entity
@Table(name="surveys")
public class Survey {

    @Id
    @Column(name="id")
    @AutoPopulated
    @TypeDef(type=DataType.STRING)
    @Schema(description = "the id of the survey", required = true)
    private UUID id;

    @Column(name="createdOn")
    @NotNull
    @Schema(description = "date for createdOn", required = true)
    private LocalDate createdOn;

    @Column(name="createdBy")
    @TypeDef(type=DataType.STRING)
    @NotNull
    @Schema(description = "id of the teamMember this entry is associated with", required = true)
    private UUID createdBy;

    @Column(name="name")
    @ColumnTransformer(
            read =  "pgp_sym_decrypt(name::bytea,'${aes.key}')",
            write = "pgp_sym_encrypt(?,'${aes.key}') "
    )
    @NotNull
    @Schema(description = "description of Name", required = true)
    private String name;

    @Column(name="description")
    @ColumnTransformer(
            read =  "pgp_sym_decrypt(description::bytea,'${aes.key}')",
            write = "pgp_sym_encrypt(?,'${aes.key}') "
    )
    @NotNull
    @Schema(description = "description of Description", required = true)
    private String description;

    public Survey(UUID id,LocalDate createdOn, UUID createdBy, String name, String description) {
        this.id = id;
        this.createdOn = createdOn;
        this.createdBy = createdBy;
        this.name = name;
        this.description = description;
    }

    public Survey(LocalDate createdOn, UUID createdBy, String name, String description) {
        this(null,createdOn, createdBy, name, description);
    }

    public UUID getId() {
        return this.id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public LocalDate getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(LocalDate createdOn) {
        this.createdOn = createdOn;
    }

    public UUID getCreatedBy() {
        return this.createdBy;
    }

    public void setCreatedBy(UUID createdBy) {
        this.createdBy = createdBy;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        com.objectcomputing.checkins.services.survey.Survey that = (com.objectcomputing.checkins.services.survey.Survey) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(createdOn, that.createdOn) &&
                Objects.equals(createdBy, that.createdBy) &&
                Objects.equals(name, that.name) &&
                Objects.equals(description, that.description);
    }

    @Override
    public String toString() {
        return "Survey{" +
                "id=" + id +
                ", createdOn=" + createdOn +
                ", createdBy=" + createdBy +
                ", name=" + name +
                ", description=" + description +
                '}';
    }
    @Override
    public int hashCode() {
        return Objects.hash(id, createdOn, createdBy, name, description);
    }
}


