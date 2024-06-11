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
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Setter
@Getter
@Entity
@Introspected
@Table(name = "volunteering_event")
public class VolunteeringEvent {

    @Id
    @Column(name = "event_id")
    @AutoPopulated
    @TypeDef(type = DataType.STRING)
    @Schema(description = "id of the volunteering event")
    private UUID id;

    @Column(name = "relationship_id")
    @NotNull
    @Schema(description = "id of the Volunteering relationship")
    private UUID relationshipId;

    @NotNull
    @Column(name = "event_date")
    @TypeDef(type = DataType.DATE, converter = LocalDateConverter.class)
    @Schema(description = "when the volunteering event occurred")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate eventDate;

    @Column(name = "hours")
    @Schema(description = "number of hours spent volunteering")
    @TypeDef(type = DataType.INTEGER)
    private int hours;

    @Nullable
    @Column(name = "notes")
    @TypeDef(type = DataType.STRING)
    @Schema(description = "notes about the volunteering event")
    private String notes;
}