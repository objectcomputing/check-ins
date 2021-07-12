package com.objectcomputing.checkins.services.opportunities;

import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotNull;
import java.util.UUID;
import java.time.LocalDate;

@Introspected
public class OpportunitiesCreateDTO {

    @NotNull
    @Schema(required = true, description = "name of the opportunity")
    private String name;

    @NotNull
    @Schema(required = true, description = "description of the opportunity")
    private String description;

    @NotNull
    @Schema(required = true, description = "link to the url")
    private String url;

    @NotNull
    @Schema(required = true, description = "date for expiresOn")
    private LocalDate expiresOn;


    @NotNull
    @Schema(required = true, description = "date for submittedOn")
    private LocalDate submittedOn;

    @NotNull
    @Schema(required = true, description = "id of the submittedBy member")
    private UUID submittedBy;

    @NotNull
    @Schema(required = true, description = "state of the associated opportunity")
    private Boolean pending;

    public String getName() { return name; }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public LocalDate getExpiresOn() {
        return expiresOn;
    }

    public void setExpiresOn(LocalDate expiresOn) {
        this.expiresOn = expiresOn;
    }

    public LocalDate getSubmittedOn() {
        return submittedOn;
    }

    public void setSubmittedOn(LocalDate submittedOn) {
        this.submittedOn = submittedOn;
    }

    public UUID getSubmittedBy() {
        return submittedBy;
    }

    public void setSubmittedBy(UUID submittedBy) {
        this.submittedBy = submittedBy;
    }

    public Boolean getPending() { return pending; }

    public void setPending(Boolean pending) { this.pending = pending; }

}
