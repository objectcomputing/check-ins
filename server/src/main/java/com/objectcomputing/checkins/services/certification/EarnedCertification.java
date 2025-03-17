package com.objectcomputing.checkins.services.certification;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.objectcomputing.checkins.converter.LocalDateConverter;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.annotation.AutoPopulated;
import io.micronaut.data.annotation.TypeDef;
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
import java.util.UUID;

@Setter
@Getter
@Entity
@Introspected
@Table(name = "earned_certification")
public class EarnedCertification {

    @Id
    @Column(name = "earned_certification_id")
    @AutoPopulated
    @TypeDef(type = DataType.STRING)
    @Schema(description = "id of the earned certification")
    private UUID id;

    @NotNull
    @Column(name = "member_id")
    @TypeDef(type = DataType.STRING)
    @Schema(description = "id of the member who earned the certification")
    private UUID memberId;

    @NotNull
    @Column(name = "certification_id")
    @TypeDef(type = DataType.STRING)
    @Schema(description = "id of the certification")
    private UUID certificationId;

    @NotNull
    @Column(name = "earned_date")
    @TypeDef(type = DataType.DATE, converter = LocalDateConverter.class)
    @Schema(description = "when the certification was earned")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate earnedDate;

    @Nullable
    @Column(name = "expiration_date")
    @TypeDef(type = DataType.DATE, converter = LocalDateConverter.class)
    @Schema(description = "optionally when the certification expires")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate expirationDate;

    @Nullable
    @Column(name = "validation_url")
    @TypeDef(type = DataType.STRING)
    @Schema(description = "optionally the url of the validation")
    private String validationUrl;

    public EarnedCertification() {
    }

    public EarnedCertification(UUID memberId, UUID certificationId, LocalDate earnedDate, LocalDate expirationDate, String validationUrl) {
        this(null, memberId, certificationId, earnedDate, expirationDate, validationUrl);
    }

    public EarnedCertification(UUID id, UUID memberId, UUID certificationId, LocalDate earnedDate, LocalDate expirationDate, String validationUrl) {
        this.id = id;
        this.memberId = memberId;
        this.certificationId = certificationId;
        this.earnedDate = earnedDate;
        this.expirationDate = expirationDate;
        this.validationUrl = validationUrl;
    }

    EarnedCertification withCertification(@NonNull UUID certificationId) {
        setCertificationId(certificationId);
        return this;
    }
}
