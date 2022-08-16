package com.objectcomputing.checkins.services.workingenvironment;
import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.Schema;

import io.micronaut.core.annotation.Nullable;
import javax.validation.constraints.NotNull;

import java.util.Objects;

@Introspected
public class WorkingEnvironmentCreateDTO {
    
    @NotNull
    @Schema(description = "Work Location requested", required = true)
    private String workLocation;

    @NotNull
    @Schema(description = "Type of key requested", required = true)
    private String keyType;

    @NotNull
    @Schema(description = "Computer OS requested", required = true)
    private String osType;

    @Nullable
    @Schema(description = "Accessories requested", nullable = true)
    private String accessories;

    @Nullable
    @Schema(description = "Other Accessories requested", nullable = true)
    private String otherAccessories;

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
        WorkingEnvironmentCreateDTO that = (WorkingEnvironmentCreateDTO) o;
        return Objects.equals(workLocation, that.workLocation) && Objects.equals(keyType, that.keyType) && Objects.equals(osType, that.osType) && Objects.equals(accessories, that.accessories) && Objects.equals(otherAccessories, that.otherAccessories);
    }
}
