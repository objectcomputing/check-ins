package com.objectcomputing.checkins.services.workingenvironment;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.annotation.AutoPopulated;
import io.micronaut.data.annotation.TypeDef;
import io.micronaut.data.jdbc.annotation.ColumnTransformer;
import io.micronaut.data.model.DataType;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

import java.util.Objects;
import java.util.UUID;

@Entity
@Introspected
@Table(name = "working_environment")
public class WorkingEnvironment {
    @Id
    @Column(name = "id")
    @AutoPopulated
    @TypeDef(type = DataType.STRING)
    @Schema(description = "id of the new employee profile this entry is associated with")
    private UUID id;

    @NotBlank
    @Column(name = "worklocation")
    @ColumnTransformer(read = "pgp_sym_decrypt(workLocation::bytea,'${aes.key}')", write = "pgp_sym_encrypt(?,'${aes.key}') ")
    @Schema(description = "Work Location requested")
    private String workLocation;

    @NotBlank
    @Column(name = "keytype")
    @ColumnTransformer(read = "pgp_sym_decrypt(keyType::bytea,'${aes.key}')", write = "pgp_sym_encrypt(?,'${aes.key}') ")
    @Schema(description = "Type of key requested")
    private String keyType;

    @NotBlank
    @Column(name = "ostype")
    @ColumnTransformer(read = "pgp_sym_decrypt(osType::bytea,'${aes.key}')", write = "pgp_sym_encrypt(?,'${aes.key}') ")
    @Schema(description = "Computer OS requested")
    private String osType;

    @Nullable
    @Column(name = "accessories")
    @ColumnTransformer(read = "pgp_sym_decrypt(accessories::bytea,'${aes.key}')", write = "pgp_sym_encrypt(?,'${aes.key}') ")
    @Schema(description = "Accessories requested")
    private String accessories;

    @Nullable
    @Column(name = "otheraccessories")
    @ColumnTransformer(read = "pgp_sym_decrypt(otherAccessories::bytea,'${aes.key}')", write = "pgp_sym_encrypt(?,'${aes.key}') ")
    @Schema(description = "Other Accessories requested")
    private String otherAccessories;

    public WorkingEnvironment(UUID id, String workLocation, String keyType, String osType, @Nullable String accessories,
            @Nullable String otherAccessories) {
        this.id = id;
        this.workLocation = workLocation;
        this.keyType = keyType;
        this.osType = osType;
        this.accessories = accessories;
        this.otherAccessories = otherAccessories;
    }

    public WorkingEnvironment(String workLocation, String keyType, String osType, @Nullable String accessories,
            @Nullable String otherAccessories) {
        this.workLocation = workLocation;
        this.keyType = keyType;
        this.osType = osType;
        this.accessories = accessories;
        this.otherAccessories = otherAccessories;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getWorkLocation() {
        return workLocation;
    }

    public void setWorkLocation(String workLocation) {
        this.workLocation = workLocation;
    }

    public String getKeyType() {
        return keyType;
    }

    public void setKeyType(String keyType) {
        this.keyType = keyType;
    }

    public String getOsType() {
        return osType;
    }

    public void setOsType(String osType) {
        this.osType = osType;
    }

    @Nullable
    public String getAccessories() {
        return accessories;
    }

    public void setAccessories(@Nullable String accessories) {
        this.accessories = accessories;
    }

    @Nullable
    public String getOtherAccessories() {
        return otherAccessories;
    }

    public void setOtherAccessories(@Nullable String otherAccessories) {
        this.otherAccessories = otherAccessories;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WorkingEnvironment that = (WorkingEnvironment) o;
        return Objects.equals(id, that.id) && Objects.equals(workLocation, that.workLocation) && Objects.equals(keyType, that.keyType) && Objects.equals(osType, that.osType) && Objects.equals(accessories, that.accessories) && Objects.equals(otherAccessories, that.otherAccessories);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, workLocation, keyType, osType, accessories, otherAccessories);
    }
}
