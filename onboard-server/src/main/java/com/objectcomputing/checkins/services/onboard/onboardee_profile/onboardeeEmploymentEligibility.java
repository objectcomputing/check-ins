import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.annotation.AutoPopulated;
import io.micronaut.data.annotation.TypeDef;
import io.micronaut.data.jdbc.annotation.ColumnTransformer;
import io.micronaut.data.model.DataType;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.management.ConstructorParameters;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import java.lang.annotation.Inherited;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

@Entity
@Introspected
@Table(name = "onboard_employment_eligibility")

public class onboardeeEmploymentEligibility {
    @Id
    @Column(name = "id")
    @AutoPopulated
    @TypeDef(type = DataType.String)
    @Schema(description = "id of the new employee profile this entry is associated with", required = true)
    private UUID id;

    @NotBlank
    @Column(name = "ageLegal")
    @Schema(description = "is the new employee 18 years old or older")
    private boolean ageLegal;

    @NotBlank
    @Column(name = "uscitizen")
    @Schema(description = "is the new employee a US citizen")
    private boolean usCitizen;

    @Nullable
    @Column(name = "visaStatus")
    @Schema(description = "new employee's visa status")
    private String visaStatus;

    @Nullable
    @Column(name = "visaExpiration")
    @Schema(description = "expiration date of visa")
    private LocalDate visaExpiration;

    @NotBlank
    @Column(name = "felonyStatus")
    @Schema(description = "has the new employee been convicted of a felony")
    private boolean felonyStatus;

    @nullable
    @Column(name = "felonyExplanation")
    @Schema(description = "explanation of convicted felony")
    private String felonyExplanation;

    public onboardeeEmploymentEligibility(boolean ageLegal, boolean usCitizen, String visaStatus,
            LocalDate expirationDate, boolean felonyStatus, String felonyExplanation) {
        this.ageLegal = ageLegal;
        this.usCitizen = usCitizen;
        this.visaStatus = visaStatus;
        this.expirationDate = expirationDate;
        this.felonyStatus = felonyStatus;
        this.felonyExplanation = felonyExplanation;

    }

    public onboardeeEmploymentEligibility(UUID id, boolean ageLegal, boolean usCitizen, String visaStatus,
            LocalDate expirationDate, boolean felonyStatus, String felonyExplanation) {
        this.id = id;
        this.ageLegal = ageLegal;
        this.usCitizen = usCitizen;
        this.visaStatus = visaStatus;
        this.expirationDate = expirationDate;
        this.felonyStatus = felonyStatus;
        this.felonyExplanation = felonyExplanation;

    }

    public onboardeeEmploymentEligibility() {
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getId() {
        return id;
    }

    public boolean getAgeLegal() {
        return ageLegal;
    }

    public void setAgeLegal(String ageLegal) {
        this.ageLegal = ageLegal;
    }

    public boolean getUsCitizen() {
        return usCitizen;
    }

    public void setUsCitizen(boolean usCitizen) {
        this.usCitizen = usCitizen;
    }

    public String getVisaStatus() {
        return visaStatus;
    }

    public void setVisaStatus(String visaStatus) {
        this.visaStatus = visaStatus;
    }

    public LocalDate getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(LocalDate expirationDate) {
        this.expirationDate = expirationDate;
    }

    public boolean getFelonyStatus() {
        return felonyStatus;
    }

    public void setFelonyStatus(boolean felonyStatus) {
        this.felonyStatus = felonyStatus;
    }

    public String getFelonyExplanation() {
        return felonyExplanation;
    }

    public void setFelonyExplanation(string felonyExplanation) {
        this.felonyExplanation = felonyExplanation;
    }
}
