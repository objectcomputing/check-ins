package com.objectcomputing.checkins.services.volunteering;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.data.annotation.AutoPopulated;
import io.micronaut.data.annotation.TypeDef;
import io.micronaut.data.model.DataType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
@Entity
@Introspected
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "volunteering_organization")
public class VolunteeringOrganization {

    @Id
    @Column(name = "organization_id")
    @AutoPopulated
    @TypeDef(type = DataType.STRING)
    @Schema(description = "id of the volunteering organization")
    private UUID id;

    @Column(name = "name")
    @NotBlank
    @Schema(description = "name of the volunteering organization")
    private String name;

    @Column(name = "description")
    @NotBlank
    @Schema(description = "description of the volunteering organization")
    private String description;

    @Column(name = "website")
    @Schema(description = "website for the volunteering organization")
    private String website;

    @Column(name = "is_active")
    @Schema(description = "whether the Volunteering Organization is active")
    private boolean active = true;

    public VolunteeringOrganization(String name, String description, String website) {
        this(null, name, description, website, true);
    }

    public VolunteeringOrganization(String name, String description, String website, boolean active) {
        this(null, name, description, website, active);
    }
}
