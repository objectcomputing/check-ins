package com.objectcomputing.checkins.services.pulseresponse;

import java.sql.Date;
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
@Table(name="PulseResponse")
public class PulseResponse {
    
    public PulseResponse() {}

    public PulseResponse(Date SubmissionDate, Date UpdatedDate, UUID teamMemberId, String questionResponse, String internalFeelings, String externalFeelings) {
        this.teamMemberId= teamMemberId;
    }
    
    @Id
    @Column(name="id")
    @AutoPopulated
    @TypeDef(type=DataType.STRING)
    private UUID id;

    @Column(name="SubmissionDate")
    private Date SubmissionDate;

    @Column(name="UpdatedDate")
    private Date UpdatedDate;

    @Column(name="teamMemberId")
    @NotNull
    @TypeDef(type=DataType.STRING)
    private UUID teamMemberId;

    @Column(name="questionResponse")
    private String questionResponse;

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

    public Date getSubmissionDate() {
        return SubmissionDate;
    }

    public void setSubmissionDate(Date submissionDate) {
        SubmissionDate = submissionDate;
    }

    public Date getUpdatedDate() {
        return UpdatedDate;
    }

    public void setUpdatedDate(Date updatedDate) {
        UpdatedDate = updatedDate;
    }

    public UUID getTeamMemberId() {
        return teamMemberId;
    }

    public void setTeamMemberId(UUID teamMemberId) {
        this.teamMemberId = teamMemberId;
    }

    public String getQuestionResponse() {
        return questionResponse;
    }

    public void setQuestionResponse(String questionResponse) {
        this.questionResponse = questionResponse;
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