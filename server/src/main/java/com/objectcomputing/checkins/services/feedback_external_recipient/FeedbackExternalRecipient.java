package com.objectcomputing.checkins.services.feedback_external_recipient;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.annotation.AutoPopulated;
import io.micronaut.data.annotation.TypeDef;
import io.micronaut.data.annotation.sql.ColumnTransformer;
import io.micronaut.data.model.DataType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import java.util.Objects;
import java.util.UUID;

@Entity
@Getter
@Setter
@Introspected
@Table(name = "feedback_external_recipient")
public class FeedbackExternalRecipient {

    @Id
    @Column(name = "id")
    @AutoPopulated
    @TypeDef(type = DataType.STRING)
    @Schema(description = "unique id of the feedback external recipient")
    private UUID id;

    @NotNull
    @Column(name="email")
    @ColumnTransformer(
            read =  "pgp_sym_decrypt(workEmail::bytea,'${aes.key}')",
            write = "pgp_sym_encrypt(?,'${aes.key}') "
    )
    @Schema(description = "email of the feedback external recipient")
    private String email;

    @Column(name = "firstname")
    @ColumnTransformer(
            read =  "pgp_sym_decrypt(firstName::bytea,'${aes.key}')",
            write = "pgp_sym_encrypt(?,'${aes.key}') "
    )
    @Schema(description = "first name of the feedback external recipient")
    private String firstName;

    @Column(name = "lastname")
    @ColumnTransformer(
            read =  "pgp_sym_decrypt(lastName::bytea,'${aes.key}')",
            write = "pgp_sym_encrypt(?,'${aes.key}') "
    )
    @Schema(description = "last name of the feedback external recipient")
    private String lastName;

    @Column(name = "company_name")
    @ColumnTransformer(
            read =  "pgp_sym_decrypt(lastName::bytea,'${aes.key}')",
            write = "pgp_sym_encrypt(?,'${aes.key}') "
    )
    @Schema(description = "company of the feedback external recipient")
    private String companyName;

    public FeedbackExternalRecipient(
            @NotBlank String email, @Nullable String firstName, @Nullable String lastName, @Nullable String company
    ) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.companyName = company;
    }

    public FeedbackExternalRecipient() {}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FeedbackExternalRecipient that = (FeedbackExternalRecipient) o;
        return Objects.equals(id, that.id)
                && Objects.equals(email, that.email)
                && Objects.equals(firstName, that.firstName)
                && Objects.equals(lastName, that.lastName)
                && Objects.equals(companyName, that.companyName)
                ;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email, firstName, lastName, companyName);
    }

    @Override
    public String toString() {
        return "FeedbackRequest{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", company='" + companyName + '\'' +
                '}';
    }
}
