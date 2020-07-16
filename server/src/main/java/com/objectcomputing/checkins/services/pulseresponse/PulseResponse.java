package com.objectcomputing.checkins.services.pulseresponse;

import java.time.LocalDate;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import io.micronaut.data.annotation.AutoPopulated;
import io.micronaut.data.annotation.TypeDef;
import io.micronaut.data.model.DataType;

@Entity
@Table(name="pulse_response")
public class PulseResponse {
    
    public PulseResponse() {}

    public PulseResponse(LocalDate submissionDate, LocalDate updatedDate, UUID teamMemberId, String internalFeelings, String externalFeelings) {
        this.submissionDate = submissionDate;
        this.updatedDate = updatedDate;
        this.teamMemberId = teamMemberId;
        this.internalFeelings = internalFeelings;
        this.externalFeelings = externalFeelings;
    }
    
    @Id
    @Column(name="id")
    @AutoPopulated
    @TypeDef(type=DataType.STRING)
    private UUID id;

    @Column(name="submissionDate")
    @NotNull
    private LocalDate submissionDate;

    @Column(name="updatedDate")
    private LocalDate updatedDate;

    @Column(name="teamMemberId")
    @NotNull
    @TypeDef(type=DataType.STRING)
    private UUID teamMemberId;

    @Column(name="internalFeelings")
    private String internalFeelings;

    @Column(name="externalFeelings")
    private String externalFeelings;

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
}