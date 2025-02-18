package com.objectcomputing.checkins.services.slack.kudos;

import io.micronaut.core.annotation.Introspected;
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

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Introspected
@Table(name = "automated_kudos_read_time")
public class KudosChannelReadTime {
    static final public String key = "Singleton";

    @Id
    @Column(name = "id")
    @Schema(description = "the id of the kudos channel read time")
    private String id;

    @NotNull
    @Column(name = "readtime")
    @TypeDef(type = DataType.TIMESTAMP)
    @Schema(description = "date the kudos were created")
    private LocalDateTime readTime;

    public KudosChannelReadTime() {
        id = key;
        readTime = LocalDateTime.now();
    }
}
