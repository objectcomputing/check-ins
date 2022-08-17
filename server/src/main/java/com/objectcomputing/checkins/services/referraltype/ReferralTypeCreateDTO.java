package com.objectcomputing.checkins.services.referraltype;

import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Introspected
public class ReferralTypeCreateDTO {

    @NotNull
    @Schema(description = "id of the new referral type this entry is associated with", required = true)
    private UUID id;

    @NotBlank
    @Schema(description = "how the application discovered the job opportunity")
    private String discoveredOpportunity;

    @NotBlank
    @Schema(description = "who referred the current applicant")
    private String referredBy;

    @NotBlank
    @Schema(description = "email of the person who referred the current applicant")
    private String referrerEmail;

    @NotBlank
    @Schema(description = "job site the applicant used for the referrer")
    private String referrerJobSite;

    @NotBlank
    @Schema(description = "additional text box for miscellaneous information")
    private String referralTypeOther;

    public void setId(UUID id) { this.id = id; }

    public UUID getId() { return id; }

    public void setDiscoveredOpportunity(String discoveredOpportunity) { this.discoveredOpportunity = discoveredOpportunity; }

    public String getDiscoveredOpportunity() { return discoveredOpportunity; }

    public void setReferredBy(String referredBy) { this.referredBy = referredBy; }

    public String getReferredBy() { return referredBy; }

    public void setReferrerEmail(String referrerEmail) { this.referrerEmail = referrerEmail; }

    public String getReferrerEmail() { return referrerEmail; }

    public void setReferrerJobSite(String referrerJobSite) { this.referrerJobSite = referrerJobSite; }

    public String getReferrerJobSite() { return referrerJobSite; }

    public void setReferralTypeOther(String referralTypeOther) { this.referralTypeOther = referralTypeOther; }

    public String getReferralTypeOther() { return referralTypeOther; }
}
