package com.objectcomputing.checkins.services.volunteering;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.objectcomputing.checkins.converter.LocalDateConverter;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.data.annotation.AutoPopulated;
import io.micronaut.data.annotation.TypeDef;
import io.micronaut.data.model.DataType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Setter
@Getter
@Entity
@Introspected
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "volunteering_relationship")
public class VolunteeringRelationship {

    @Id
    @Column(name = "relationship_id")
    @AutoPopulated
    @TypeDef(type = DataType.STRING)
    @Schema(description = "id of the volunteering relationship")
    private UUID id;

    @NotNull
    @Column(name = "member_id")
    @TypeDef(type = DataType.STRING)
    @Schema(description = "id of the member with the relationship")
    private UUID memberId;

    @NotNull
    @Column(name = "organization_id")
    @TypeDef(type = DataType.STRING)
    @Schema(description = "id of the organization with the relationship")
    private UUID organizationId;

    @NotNull
    @Column(name = "start_date")
    @TypeDef(type = DataType.DATE, converter = LocalDateConverter.class)
    @Schema(description = "when the relationship started")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @Nullable
    @Column(name = "end_date")
    @TypeDef(type = DataType.DATE, converter = LocalDateConverter.class)
    @Schema(description = "(optionally) when the relationship ended")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    @Column(name = "is_active")
    @Schema(description = "whether the Volunteering Relationship is active")
    private boolean active = true;

    public VolunteeringRelationship(UUID memberId, UUID organizationId, LocalDate startDate, @Nullable LocalDate endDate) {
        this(null, memberId, organizationId, startDate, endDate, true);
    }

    public VolunteeringRelationship(UUID memberId, UUID organizationId, LocalDate startDate, @Nullable LocalDate endDate, boolean active) {
        this(null, memberId, organizationId, startDate, endDate, active);
    }
}