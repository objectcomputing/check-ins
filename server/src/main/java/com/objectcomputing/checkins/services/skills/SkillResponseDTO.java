package com.objectcomputing.checkins.services.skills;

import com.objectcomputing.checkins.services.skills.tags.SkillTagResponseDTO;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

public class SkillResponseDTO {
    @Schema(description = "unique identifier of the skill", required = true)
    @NotNull
    private UUID id;

    @Schema(description = "name of the skill", required = true)
    @NotBlank
    private String name;

    @Schema(description = "is the skill yet approved", defaultValue = "false")
    private Boolean pending;

    @Schema(description = "description of the skill", required = false)
    private String description;

    @Schema(description = "Is the skill extraneous to OCI", defaultValue = "false")
    private Boolean extraneous;

    @Schema(description = "tags for this skill")
    private List<SkillTagResponseDTO> tags;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getPending() {
        return pending;
    }

    public void setPending(Boolean pending) {
        this.pending = pending;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getExtraneous() {
        return extraneous;
    }

    public void setExtraneous(Boolean extraneous) {
        this.extraneous = extraneous;
    }

    public List<SkillTagResponseDTO> getTags() {
        return tags;
    }

    public void setTags(List<SkillTagResponseDTO> tags) {
        this.tags = tags;
    }
}
