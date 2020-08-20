package com.objectcomputing.checkins.services.pulseresponse;

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
import io.micronaut.data.model.DataType;
import io.swagger.v3.oas.annotations.media.Schema;

@Entity
@Table(name="pulse_response")
public class PulseResponse {

    @Id
    @Column(name="id")
    @AutoPopulated
    @TypeDef(type=DataType.STRING)
    @Schema(description = "the id of the pulse_response", required = true)
    private UUID id;

    @Column(name="submissionDate")
    @Schema(description = "id of the submissionDate", required = true)
    private LocalDate submissionDate;

    @Column(name="updatedDate")
    @Schema(description = "id of the updatedDate", required = true)
    private LocalDate updatedDate;

    @Column(name="teamMemberId")
    @NotNull
    @TypeDef(type=DataType.STRING)
    @Schema(description = "id of the teamMember this entry is associated with", required = true)
    private UUID teamMemberId;

    @Column(name="internalFeelings", unique = true)
    @NotNull
    @Schema(description = "id of the internalfeelings", required = true)
    private String internalFeelings;

    @Column(name="externalFeelings", unique = true)
    @NotNull
    @Schema(description = "id of the externalfeelings", required = true)
    private String externalFeelings;

    public PulseResponse(LocalDate submissionDate,LocalDate updatedDate, UUID teamMemberId, String internalFeelings, String externalFeelings) {
        this(null,submissionDate, updatedDate, teamMemberId, internalFeelings, externalFeelings);
    }

    public PulseResponse(UUID id,LocalDate submissionDate,LocalDate updatedDate, UUID teamMemberId, String internalFeelings, String externalFeelings) {
        this.id = id;
        this.submissionDate = submissionDate;
        this.updatedDate = updatedDate;
        this.teamMemberId = teamMemberId;
        this.internalFeelings = internalFeelings;
        this.externalFeelings = externalFeelings;
    }

    public UUID getId() {
        return id;
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
        return teamMemberId;
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
}

