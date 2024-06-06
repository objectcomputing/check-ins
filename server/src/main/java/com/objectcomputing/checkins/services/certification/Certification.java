package com.objectcomputing.checkins.services.certification;

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
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
@Entity
@Introspected
@Table(name = "certification")
class Certification {

    @Id
    @Column(name = "certification_id")
    @AutoPopulated
    @TypeDef(type = DataType.STRING)
    @Schema(description = "id of the certification")
    private UUID id;

    @Column(name = "name")
    @NotBlank
    @Schema(description = "name of the certification")
    private String name;

    @Column(name = "badge_url")
    @NotBlank
    @Schema(description = "url of the badge")
    private String badgeUrl;

    public Certification() {
    }

    Certification(UUID id, String name, String badgeUrl) {
        this.id = id;
        this.name = name;
        this.badgeUrl = badgeUrl;
    }

    Certification(String name, String badgeUrl) {
        this(null, name, badgeUrl);
    }
}
