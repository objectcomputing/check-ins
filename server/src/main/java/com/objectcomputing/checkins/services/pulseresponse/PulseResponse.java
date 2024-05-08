package com.objectcomputing.checkins.services.pulseresponse;

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
@Table(name = "pulse_response")
public class PulseResponse {

    @Id
    @Column(name="id")
    @AutoPopulated
    @TypeDef(type=DataType.STRING)
    @Schema(description = "the id of the pulse_response", required = true)
    private UUID id;

    @Column(name="submissiondate")
    @NotNull
    @Schema(description = "date for submissionDate", required = true)
    @TypeDef(type = DataType.DATE, converter = LocalDateConverter.class)
    private LocalDate submissionDate;

    @Column(name="updateddate")
    @NotNull
    @Schema(description = "date for updatedDate", required = true)
    @TypeDef(type = DataType.DATE, converter = LocalDateConverter.class)
    private LocalDate updatedDate;

    @Column(name="teammemberid")
    @TypeDef(type=DataType.STRING)
    @NotNull
    @Schema(description = "id of the teamMember this entry is associated with", required = true)
    private UUID teamMemberId;

    @Column(name="internalfeelings")
    @ColumnTransformer(
            read =  "pgp_sym_decrypt(internalFeelings::bytea,'${aes.key}')",
            write = "pgp_sym_encrypt(?,'${aes.key}') "
    )
    @NotNull
    @Schema(description = "description of internalfeelings", required = true)
    private String internalFeelings;

    @Column(name="externalfeelings")
    @ColumnTransformer(
            read =  "pgp_sym_decrypt(externalFeelings::bytea,'${aes.key}')",
            write = "pgp_sym_encrypt(?,'${aes.key}') "
    )
    @NotNull
    @Schema(description = "description of externalfeelings", required = true)
    private String externalFeelings;

    public PulseResponse(UUID id,LocalDate submissionDate,LocalDate updatedDate, UUID teamMemberId, String internalFeelings, String externalFeelings) {
        this.id = id;
        this.submissionDate = submissionDate;
        this.updatedDate = updatedDate;
        this.teamMemberId = teamMemberId;
        this.internalFeelings = internalFeelings;
        this.externalFeelings = externalFeelings;
    }

    public PulseResponse(LocalDate submissionDate,LocalDate updatedDate, UUID teamMemberId, String internalFeelings, String externalFeelings) {
        this(null,submissionDate, updatedDate, teamMemberId, internalFeelings, externalFeelings);
    }

    public UUID getId() {
        return this.id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public LocalDate getSubmissionDate() {
        return submissionDate;
    }

    public void setSubmissionDate(LocalDate submissionDate) {
        this.submissionDate = submissionDate;
    }

    public LocalDate getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(LocalDate updatedDate) {
        this.updatedDate = updatedDate;
    }

    public UUID getTeamMemberId() {
        return this.teamMemberId;
    }

    public void setTeamMemberId(UUID teamMemberId) {
        this.teamMemberId = teamMemberId;
    }

    public String getInternalFeelings() {
        return internalFeelings;
    }

    public void setInternalFeelings(String internalFeelings) {
        this.internalFeelings = internalFeelings;
    }

    public String getExternalFeelings() {
        return externalFeelings;
    }

    public void setExternalFeelings(String externalFeelings) {
        this.externalFeelings = externalFeelings;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PulseResponse that = (PulseResponse) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(submissionDate, that.submissionDate) &&
                Objects.equals(teamMemberId, that.teamMemberId) &&
                Objects.equals(internalFeelings, that.internalFeelings) &&
                Objects.equals(externalFeelings, that.externalFeelings);
    }

    @Override
    public String toString() {
        return "PulseResponse{" +
                "id=" + id +
                ", submissionDate=" + submissionDate +
                ", updatedDate=" + updatedDate +
                ", teamMemberId=" + teamMemberId +
                ", internalFeelings=" + internalFeelings +
                ", externalFeelings=" + externalFeelings +
                '}';
    }
    @Override
    public int hashCode() {
        return Objects.hash(id, submissionDate, updatedDate, teamMemberId, internalFeelings, externalFeelings);
    }
}

