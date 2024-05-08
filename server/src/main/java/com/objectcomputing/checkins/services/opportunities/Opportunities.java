package com.objectcomputing.checkins.services.opportunities;

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
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

@Entity
@Introspected
@Table(name = "opportunities")
public class Opportunities {

    @Id
    @Column(name="id")
    @AutoPopulated
    @TypeDef(type=DataType.STRING)
    @Schema(description = "the id of the opportunity", required = true)
    private UUID id;

    @Column(name="name")
    @ColumnTransformer(
            read =  "pgp_sym_decrypt(name::bytea,'${aes.key}')",
            write = "pgp_sym_encrypt(?,'${aes.key}') "
    )
    @NotNull
    @Schema(description = "Name of the opportunity", required = true)
    private String name;

    @Column(name="description")
    @ColumnTransformer(
            read =  "pgp_sym_decrypt(description::bytea,'${aes.key}')",
            write = "pgp_sym_encrypt(?,'${aes.key}') "
    )
    @NotNull
    @Schema(description = "description of the opportunity", required = true)
    private String description;

    @Column(name="url")
    @ColumnTransformer(
            read =  "pgp_sym_decrypt(url::bytea,'${aes.key}')",
            write = "pgp_sym_encrypt(?,'${aes.key}') "
    )

    @Schema(description = "description of url", required = true)
    private String url;

    @Column(name="expireson")
    @Schema(description = "date for expiresOn", required = true)
    @TypeDef(type = DataType.DATE, converter = LocalDateConverter.class)
    private LocalDate expiresOn;

    @Column(name="submittedon")
    @Schema(description = "date for submittedOn", required = true)
    @TypeDef(type = DataType.DATE, converter = LocalDateConverter.class)
    private LocalDate submittedOn;

    @Column(name="submittedby")
    @TypeDef(type=DataType.STRING)
    @Schema(description = "id of the teamMember this entry is associated with", required = true)
    private UUID submittedBy;

    @Column(name = "pending")
    @TypeDef(type = DataType.BOOLEAN)
    @Schema(description = "whether the opportunity is pending", required = true)
    private Boolean pending;

    public Opportunities(UUID id, String name, String description, String url, LocalDate expiresOn, LocalDate submittedOn, UUID submittedBy, Boolean pending) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.url = url;
        this.expiresOn = expiresOn;
        this.submittedOn = submittedOn;
        this.submittedBy = submittedBy;
        this.pending = pending;
    }

    public Opportunities(String name, String description, String url, LocalDate expiresOn, LocalDate submittedOn, UUID submittedBy, Boolean pending) {
        this(null, name, description, url, expiresOn, submittedOn, submittedBy, pending);
    }

    public Opportunities(String name, String description, String url, LocalDate expiresOn, Boolean pending) {
        this(null, name, description, url, expiresOn, null, null, pending);
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

    public String getUrl() { return url; }

    public void setUrl(String url) { this.url = url; }

    public LocalDate getExpiresOn() {
        return expiresOn;
    }

    public void setExpiresOn(LocalDate expiresOn) {
        this.expiresOn = expiresOn;
    }

    public LocalDate getSubmittedOn() {
        return submittedOn;
    }

    public void setSubmittedOn(LocalDate submittedOn) {
        this.submittedOn = submittedOn;
    }

    public UUID getSubmittedBy() {
        return this.submittedBy;
    }

    public void setSubmittedBy(UUID submittedBy) {
        this.submittedBy = submittedBy;
    }

    public Boolean getPending() { return pending; }

    public void setPending(Boolean pending) { this.pending = pending; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        com.objectcomputing.checkins.services.opportunities.Opportunities that = (com.objectcomputing.checkins.services.opportunities.Opportunities) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(name, that.name) &&
                Objects.equals(description, that.description) &&
                Objects.equals(url, that.url) &&
                Objects.equals(expiresOn, that.expiresOn) &&
                Objects.equals(submittedOn, that.submittedOn) &&
                Objects.equals(submittedBy, that.submittedBy) &&
                Objects.equals(pending, that.pending);
    }

    @Override
    public String toString() {
        return "Opportunities{" +
                "id=" + id +
                ", name=" + name +
                ", description=" + description +
                ", url=" + url +
                ", expiresOn=" + expiresOn +
                ", submittedOn=" + submittedOn +
                ", submittedBy=" + submittedBy +
                ", pending=" + pending +
                '}';
    }
    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, url, expiresOn, submittedOn, submittedBy, pending);
    }
}