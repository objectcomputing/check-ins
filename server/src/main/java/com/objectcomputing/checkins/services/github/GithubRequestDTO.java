package com.objectcomputing.checkins.services.github;

import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotBlank;
import java.sql.Array;

@Introspected
public class GithubRequestDTO {

    @NotBlank
    @Schema(title = "The title of the new Github issue", required = true)
    private String title;

    @NotBlank
    @Schema(description = "The description of the issue", required = true)
    private String description;

    private String[] labels = {"bug"};

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String[] getLabels() {
        return labels;
    }

    public void setLabels(String[] labels) {
        this.labels = labels;
    }
}
