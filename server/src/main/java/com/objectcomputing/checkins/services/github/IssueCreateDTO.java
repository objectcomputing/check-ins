package com.objectcomputing.checkins.services.github;

import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotBlank;
import java.sql.Array;

@Introspected
public class IssueCreateDTO {

    @NotBlank
    @Schema(title = "The title of the new Github issue", required = true)
    private String title;

    @NotBlank
    @Schema(title = "The description of the issue", required = true)
    private String body;

    private String[] labels = {"bug"};

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String description) {
        this.body = description;
    }

    public String[] getLabels() {
        return labels;
    }

    public void setLabels(String[] labels) {
        this.labels = labels;
    }
}
