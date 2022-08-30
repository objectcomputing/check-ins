package com.objectcomputing.checkins.services.referraltype;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.data.annotation.AutoPopulated;
import io.micronaut.data.annotation.TypeDef;
import io.micronaut.data.jdbc.annotation.ColumnTransformer;
import io.micronaut.data.model.DataType;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import java.util.Objects;
import java.util.UUID;

@Entity
@Introspected
@Table(name="referral_type")
public class ReferralType {

    @Id // indicates this member field below is the primary key of the current entity
    @Column(name = "id") //indicates this value is stored under a column in the database with the name "id"
    @AutoPopulated //Micronaut will autopopulate a user id for each onboardee's profile automatically
    @TypeDef(type = DataType.STRING) //indicates what type of data will be stored in the database
    @Schema(description = "id of the new referral type this entry is associated with", required = true)
    private UUID id;

    @NotBlank
    @Column(name = "discoveredopportunity")
    @ColumnTransformer(
            read = "pgp_sym_decrypt(desiredposition::bytea,'${aes.key}')",
            write = "pgp_sym_encrypt(?,'${aes.key}') "
    )
    @Schema(description = "how the applicant discovered this opportunity")
    private String discoveredOpportunity;

    @NotBlank
    @Column(name = "referredby")
    @ColumnTransformer(
            read = "pgp_sym_decrypt(desiredposition::bytea,'${aes.key}')",
            write = "pgp_sym_encrypt(?,'${aes.key}') "
    )
    @Schema(description = "who the applicant was referred by")
    private String referredBy;

    @NotBlank
    @Column(name = "referreremail")
    @ColumnTransformer(
            read = "pgp_sym_decrypt(desiredposition::bytea,'${aes.key}')",
            write = "pgp_sym_encrypt(?,'${aes.key}') "
    )
    @Schema(description = "the email of the referrer")
    private String referrerEmail;

    @NotBlank
    @Column(name = "referrerjobsite")
    @ColumnTransformer(
            read = "pgp_sym_decrypt(desiredposition::bytea,'${aes.key}')",
            write = "pgp_sym_encrypt(?,'${aes.key}') "
    )
    @Schema(description = "the job site that the referrer used")
    private String referrerJobSite;

    @NotBlank
    @Column(name = "referraltypeother")
    @ColumnTransformer(
            read = "pgp_sym_decrypt(desiredposition::bytea,'${aes.key}')",
            write = "pgp_sym_encrypt(?,'${aes.key}') "
    )
    @Schema(description = "this is the other text box for additional, custom input")
    private String referralTypeOther;

    public ReferralType(String discoveredOpportunity, String referredBy, String referrerEmail, String referrerJobSite,
                        String referralTypeOther) {
        this.discoveredOpportunity = discoveredOpportunity;
        this.referredBy = referredBy;
        this.referrerEmail = referrerEmail;
        this.referrerJobSite = referrerJobSite;
        this.referralTypeOther = referralTypeOther;
    }

    public ReferralType(UUID id, String discoveredOpportunity, String referredBy, String referrerEmail, String referrerJobSite,
                        String referralTypeOther) {
        this.id = id;
        this.discoveredOpportunity = discoveredOpportunity;
        this.referredBy = referredBy;
        this.referrerEmail = referrerEmail;
        this.referrerJobSite = referrerJobSite;
        this.referralTypeOther = referralTypeOther;
    }

    public ReferralType() {}

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getId() {
        return id;
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReferralType that = (ReferralType) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(discoveredOpportunity, that.discoveredOpportunity) &&
                Objects.equals(referredBy, that.referredBy) &&
                Objects.equals(referrerEmail, that.referrerEmail) &&
                Objects.equals(referrerJobSite, that.referrerJobSite) &&
                Objects.equals(referralTypeOther, that.referralTypeOther);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, discoveredOpportunity, referredBy, referrerEmail, referrerJobSite, referralTypeOther);
    }
}
