package com.objectcomputing.checkins.services.pulseresponse;

import com.objectcomputing.checkins.converter.LocalDateConverter;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.Nullable;
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
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

@Entity
@Getter
@Setter
@Introspected
@Table(name = "pulse_response")
public class PulseResponse {

    @Id
    @Column(name="id")
    @AutoPopulated
    @TypeDef(type=DataType.STRING)
    @Schema(description = "the id of the pulse_response")
    private UUID id;

    @Column(name="internal_score")
    @NotNull
    @Schema(description = "integer for internalScore", required = true)
    private Integer internalScore;

    @Column(name="external_score")
    @Nullable
    @Schema(description = "integer for externalScore", required = true)
    private Integer externalScore;

    @Column(name="submissiondate")
    @NotNull
    @Schema(description = "date for submissionDate")
    @TypeDef(type = DataType.DATE, converter = LocalDateConverter.class)
    private LocalDate submissionDate;

    @Column(name="teammemberid")
    @TypeDef(type=DataType.STRING)
    @Nullable
    @Schema(description = "id of the teamMember this entry is associated with")
    private UUID teamMemberId;

    @Column(name="internalfeelings")
    @ColumnTransformer(
            read =  "pgp_sym_decrypt(internalFeelings::bytea,'${aes.key}')",
            write = "pgp_sym_encrypt(?,'${aes.key}') "
    )
    @Nullable
    @Schema(description = "description of internalfeelings")
    private String internalFeelings;

    @Column(name="externalfeelings")
    @ColumnTransformer(
            read =  "pgp_sym_decrypt(externalFeelings::bytea,'${aes.key}')",
            write = "pgp_sym_encrypt(?,'${aes.key}') "
    )
    @Nullable
    @Schema(description = "description of externalfeelings")
    private String externalFeelings;

    protected PulseResponse() {
    }

    public PulseResponse(UUID id, Integer internalScore, @Nullable Integer externalScore, LocalDate submissionDate, @Nullable UUID teamMemberId, String internalFeelings, String externalFeelings) {
        this.id = id;
        this.internalScore = internalScore;
        this.externalScore = externalScore;
        this.submissionDate = submissionDate;
        this.teamMemberId = teamMemberId;
        this.internalFeelings = internalFeelings;
        this.externalFeelings = externalFeelings;
    }

    public PulseResponse(Integer internalScore, Integer externalScore, LocalDate submissionDate, UUID teamMemberId, String internalFeelings, String externalFeelings) {
        this(null, internalScore, externalScore, submissionDate, teamMemberId, internalFeelings, externalFeelings);
    }

    public UUID getId() {
        return this.id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Integer getInternalScore() {
        return internalScore;
    }

    public void setInternalScore(Integer internalScore) {
        this.internalScore = internalScore;
    }

    public Integer getExternalScore() {
        return externalScore;
    }

    public void setExternalScore(Integer externalScore) {
        this.externalScore = externalScore;
    }

    public LocalDate getSubmissionDate() {
        return submissionDate;
    }

    public void setSubmissionDate(LocalDate submissionDate) {
        this.submissionDate = submissionDate;
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
                Objects.equals(internalScore, that.internalScore) &&
                Objects.equals(externalScore, that.externalScore) &&
                Objects.equals(submissionDate, that.submissionDate) &&
                Objects.equals(teamMemberId, that.teamMemberId) &&
                Objects.equals(internalFeelings, that.internalFeelings) &&
                Objects.equals(externalFeelings, that.externalFeelings);
    }

    @Override
    public String toString() {
        return "PulseResponse{" +
                "id=" + id +
                ", internalScore" + internalScore +
                ", externalScore" + externalScore +
                ", submissionDate=" + submissionDate +
                ", teamMemberId=" + teamMemberId +
                ", internalFeelings=" + internalFeelings +
                ", externalFeelings=" + externalFeelings +
                '}';
    }
    @Override
    public int hashCode() {
        return Objects.hash(id, internalScore, externalScore, submissionDate, teamMemberId, internalFeelings, externalFeelings);
    }
}

