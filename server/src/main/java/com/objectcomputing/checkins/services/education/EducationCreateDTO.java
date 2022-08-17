package com.objectcomputing.checkins.services.education;

import java.time.LocalDate;
import java.util.Objects;

import javax.validation.constraints.NotBlank;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.Nullable;
import io.swagger.v3.oas.annotations.media.Schema;

@Introspected
public class EducationCreateDTO {

    @NotBlank
    @Schema(description = "highest degree completed")
    private String highestDegree;

    @NotBlank
    @Schema(description = "name of college")
    private String institution;

    @NotBlank
    @Schema(description = "location of college")
    private String location;

    @NotBlank
    @Schema(description = "name of degree")
    private String degree;

    @NotBlank
    @Schema(description = "date of degree completion")
    private LocalDate completionDate;

    @NotBlank
    @Schema(description = "name of major")
    private String major;

    @Nullable
    @Schema(description = "any additional info")
    private String additionalInfo;

    public String getHighestDegree() {
        return highestDegree;
    }

    public void setHighestDegree(String highestDegree) {
        this.highestDegree = highestDegree;
    }

    public String getInstitution() {
        return institution;
    }

    public void setInstitution(String institution) {
        this.institution = institution;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDegree() {
        return degree;
    }

    public void setDegree(String degree) {
        this.degree = degree;
    }

    public LocalDate getCompletionDate() {
        return completionDate;
    }

    public void setCompletionDate(LocalDate completionDate) {
        this.completionDate = completionDate;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    @Nullable
    public String getAdditionalInfo() {
        return additionalInfo;
    }

    public void setAdditionalInfo(@Nullable String additionalInfo) {
        this.additionalInfo = additionalInfo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        EducationCreateDTO that = (EducationCreateDTO) o;
        return Objects.equals(highestDegree, that.highestDegree) && Objects.equals(institution, that.institution)
                && Objects.equals(location, that.location) && Objects.equals(degree, that.degree)
                && Objects.equals(completionDate, that.completionDate) && Objects.equals(major, that.major)
                && Objects.equals(additionalInfo, that.additionalInfo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(highestDegree, institution, location, degree, completionDate, major, additionalInfo);
    }
}
