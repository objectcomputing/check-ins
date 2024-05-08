package com.objectcomputing.checkins.services.survey;

import com.objectcomputing.checkins.converter.LocalDateConverter;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.data.annotation.AutoPopulated;
import io.micronaut.data.annotation.TypeDef;
import io.micronaut.data.annotation.sql.ColumnTransformer;
import io.micronaut.data.model.DataType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

@Entity
@Introspected
@Table(name = "surveys")
public class Survey {

    @Id
    @Column(name="id")
    @AutoPopulated
    @TypeDef(type=DataType.STRING)
    @Schema(description = "the id of the survey")
    private UUID id;

    @ColumnTransformer(
            read =  "pgp_sym_decrypt(name::bytea,'${aes.key}')",
            write = "pgp_sym_encrypt(?,'${aes.key}') "
    )
    @NotBlank
    @Schema(description = "description of Name")
    @Column(name="name")
    private String name;

    @Column(name="description")
    @ColumnTransformer(
            read =  "pgp_sym_decrypt(description::bytea,'${aes.key}')",
            write = "pgp_sym_encrypt(?,'${aes.key}') "
    )
    @NotBlank
    @Schema(description = "description of Description")
    private String description;

    @Column(name="createdon")
    @NotNull
    @Schema(description = "date for createdOn")
    @TypeDef(type = DataType.DATE, converter = LocalDateConverter.class)
    private LocalDate createdOn;

    @Column(name="createdby")
    @TypeDef(type=DataType.STRING)
    @NotNull
    @Schema(description = "id of the teamMember this entry is associated with")
    private UUID createdBy;

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

    public Survey() {

    }

    public UUID getId() {
        return this.id;
    }

    public void setId(UUID id) {
        this.id = id;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        com.objectcomputing.checkins.services.survey.Survey that = (com.objectcomputing.checkins.services.survey.Survey) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(name, that.name) &&
                Objects.equals(description, that.description) &&
                Objects.equals(createdOn, that.createdOn) &&
                Objects.equals(createdBy, that.createdBy);
    }

    @Override
    public String toString() {
        return "Survey{" +
                "id=" + id +
                ", name=" + name +
                ", description=" + description +
                ", createdOn=" + createdOn +
                ", createdBy=" + createdBy +
                '}';
    }
    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, createdOn, createdBy);
    }
}


