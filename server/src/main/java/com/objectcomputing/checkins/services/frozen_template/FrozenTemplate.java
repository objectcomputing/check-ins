package com.objectcomputing.checkins.services.frozen_template;


import io.micronaut.data.annotation.AutoPopulated;
import io.micronaut.data.annotation.TypeDef;
import io.micronaut.data.model.DataType;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.annotation.Nullable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "frozen_templates")
public class FrozenTemplate {
    @Id
    @Column(name = "id")
    @AutoPopulated
    @TypeDef(type = DataType.STRING)
    @Schema(description = "unique id of the feedback template ", required = true)
    private UUID id;

    @Column(name = "title")
    @NotBlank
    @TypeDef(type = DataType.STRING)
    @Schema(description = "title of feedback template", required = true)
    private String title;

    @Column(name = "description")
    @Nullable
    @TypeDef(type = DataType.STRING)
    @Schema(description = "description of feedback template", required = false)
    private String description;

    @Column(name = "creator_id")
    @NotBlank
    @TypeDef(type = DataType.STRING)
    @Schema(description = "UUID of person who created the original feedback template, not necessarily request creator", required = true)
    private UUID createdBy;

    @Column(name = "request_id")
    @NotBlank
    @TypeDef(type = DataType.STRING)
    @Schema(description = "UUID of the request this frozen template is attached to ", required = true)
    private UUID requestId;



    public FrozenTemplate(UUID id, String title, @Nullable String description, UUID createdBy, UUID requestId) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.createdBy = createdBy;
        this.requestId = requestId;
    }


    public FrozenTemplate(String title, @Nullable String description, UUID createdBy, UUID requestId) {
        this.id = null;
        this.title = title;
        this.description = description;
        this.createdBy = createdBy;
        this.requestId = requestId;
    }

    FrozenTemplate() {}

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Nullable
    public String getDescription() {
        return description;
    }

    public void setDescription(@Nullable String description) {
        this.description = description;
    }

    public UUID getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(UUID createdBy) {
        this.createdBy = createdBy;
    }

    public UUID getRequestId() {
        return requestId;
    }

    public void setRequestId(UUID requestId) {
        this.requestId = requestId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FrozenTemplate that = (FrozenTemplate) o;
        return id.equals(that.id) && title.equals(that.title) && Objects.equals(description, that.description) && createdBy.equals(that.createdBy) && requestId.equals(that.requestId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, description, createdBy, requestId);
    }





}


