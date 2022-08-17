package com.objectcomputing.checkins.services.education;

import io.micronaut.data.annotation.AutoPopulated;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.TypeDef;
import io.micronaut.data.jdbc.annotation.ColumnTransformer;
import io.micronaut.data.model.DataType;
import io.swagger.v3.oas.annotations.media.Schema;
import javax.persistence.Entity;
import javax.persistence.Table;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

import javax.persistence.Column;
import javax.validation.constraints.NotBlank;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.Nullable;

@Entity
@Introspected
@Table(name = "education")
public class Education {
    @Id
    @Column(name = "id")
    @AutoPopulated
    @TypeDef(type = DataType.STRING)
    @Schema(description = "private key id")
    private UUID id;

    @NotBlank
    @Column(name = "highestdegree")
    @ColumnTransformer(read = "pgp_sym_decrypt(highestDegree::bytea,'${aes.key}')", write = "pgp_sym_encrypt(?,'${aes.key}') ")
    @Schema(description = "highest degree completed")
    private String highestDegree;

    @NotBlank
    @Column(name = "institution")
    @ColumnTransformer(read = "pgp_sym_decrypt(institution::bytea,'${aes.key}')", write = "pgp_sym_encrypt(?,'${aes.key}') ")
    @Schema(description = "name of college")
    private String institution;

    @NotBlank
    @Column(name = "location")
    @ColumnTransformer(read = "pgp_sym_decrypt(location::bytea,'${aes.key}')", write = "pgp_sym_encrypt(?,'${aes.key}') ")
    @Schema(description = "location of college")
    private String location;

    @NotBlank
    @Column(name = "degree")
    @ColumnTransformer(read = "pgp_sym_decrypt(degree::bytea,'${aes.key}')", write = "pgp_sym_encrypt(?,'${aes.key}') ")
    @Schema(description = "name of degree")
    private String degree;

    @NotBlank
    @Column(name = "completiondate")
    @ColumnTransformer(read = "pgp_sym_decrypt(completionDate::bytea,'${aes.key}')", write = "pgp_sym_encrypt(?,'${aes.key}') ")
    @Schema(description = "date of degree completion")
    private LocalDate completionDate;

    @Nullable
    @Column(name = "major")
    @ColumnTransformer(read = "pgp_sym_decrypt(major::bytea,'${aes.key}')", write = "pgp_sym_encrypt(?,'${aes.key}') ")
    @Schema(description = "name of major")
    private String major;

    @Nullable
    @Column(name = "additionalinfo")
    @ColumnTransformer(read = "pgp_sym_decrypt(additionalinfo::bytea,'${aes.key}')", write = "pgp_sym_encrypt(?,'${aes.key}') ")
    @Schema(description = "any additional info")
    private String additionalInfo;

    public Education(UUID id, String highestDegree, String institution, String location, String degree,
            LocalDate completionDate, @Nullable String major, @Nullable String additionalInfo) {
        this.id = id;
        this.highestDegree = highestDegree;
        this.institution = institution;
        this.location = location;
        this.degree = degree;
        this.completionDate = completionDate;
        this.major = major;
        this.additionalInfo = additionalInfo;
    }

    public Education(String highestDegree, String institution, String location, String degree, LocalDate completionDate,
            @Nullable String major, @Nullable String additionalInfo) {
        this.highestDegree = highestDegree;
        this.institution = institution;
        this.location = location;
        this.degree = degree;
        this.completionDate = completionDate;
        this.major = major;
        this.additionalInfo = additionalInfo;
    }

    public Education() {}

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

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

    @Nullable
    public String getMajor() {
        return major;
    }

    public void setMajor(@Nullable String major) {
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
        Education education = (Education) o;
        return Objects.equals(id, education.id) && Objects.equals(highestDegree, education.highestDegree)
                && Objects.equals(institution, education.institution) && Objects.equals(location, education.location)
                && Objects.equals(degree, education.degree) && Objects.equals(completionDate, education.completionDate)
                && Objects.equals(major, education.major) && Objects.equals(additionalInfo, education.additionalInfo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, highestDegree, institution, location, degree, completionDate, major, additionalInfo);
    }
}
