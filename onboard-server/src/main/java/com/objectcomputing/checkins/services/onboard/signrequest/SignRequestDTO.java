package com.objectcomputing.checkins.services.onboard.signrequest;

import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotBlank;

@Introspected
public class SignRequestDTO {

    @NotBlank
    @Schema(title = "The email the SignRequest is being sent to", required = true)
    private String email;

    @NotBlank
    @Schema(title = "The SignRequest document's URL", required = true)
    private String document;

    @NotBlank
    @Schema(title = "The name of the SignRequest", required = true)
    private String name;

    @NotBlank
    @Schema(title = "The email of the SignRequest's sender", required = true)
    private String from_email;

    @NotBlank
    @Schema(title = "The message included in the SignRequest", required = true)
    private String message;

    @NotBlank
    @Schema(title = "The signing status of the SignRequest", required = true)
    private String needs_to_sign;

    @NotBlank
    @Schema(title = "The subject of the SignRequest", required = true)
    private String subject;

    @NotBlank
    @Schema(title = "The number of days before the SignRequest automatically deletes itself", required = true)
    private String auto_delete_days;

    @NotBlank
    @Schema(title = "The people who are going to be sent the SignRequest", required = true)
    private String[] signers;

    public String getEmail() { return email; }

    public void setEmail(String email) { this.email = email; }

    public String getDocument() { return document; }

    public void setDocument() { this.document = document; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getFromEmail() { return from_email; }

    public void setFromEmail(String from_email) { this.from_email = from_email; }

    public String getMessage() { return message; }

    public void setMessage(String message) { this.message = message; }

    public String getSigningStatus() { return needs_to_sign; }

    public void setSigningStatus(String needs_to_sign) { this.needs_to_sign = needs_to_sign; }

    public String getSubject() { return subject; }

    public void setSubject(String subject) { this.subject = subject; }

    public String getDeleteDays() { return auto_delete_days; }

    public void setDeleteDays(String auto_delete_days) { this.auto_delete_days = auto_delete_days; }

    public String[] getSigners() { return signers; }

    public void setSigners(String[] signers) { this.signers = signers; }

}
