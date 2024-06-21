package com.objectcomputing.checkins.services.document;

import io.micronaut.data.annotation.AutoPopulated;
import io.micronaut.data.annotation.TypeDef;
import io.micronaut.data.annotation.sql.ColumnTransformer;
import io.micronaut.data.model.DataType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Table(name = "document")
public class Document {

    @Id
    @Column(name = "document_id")
    @AutoPopulated
    @TypeDef(type = DataType.STRING)
    @Schema(description = "id of the document")
    private UUID id;

    @NotBlank
    @Column(name = "name")
    @ColumnTransformer(
            read = "pgp_sym_decrypt(name::bytea,'${aes.key}')",
            write = "pgp_sym_encrypt(?, '${aes.key}') "
    )
    @TypeDef(type = DataType.STRING)
    @Schema(description = "the displayed name of the document")
    private String name;

    @NotBlank
    @Column(name = "url")
    @ColumnTransformer(
            read = "pgp_sym_decrypt(url::bytea,'${aes.key}')",
            write = "pgp_sym_encrypt(?, '${aes.key}') "
    )
    @TypeDef(type = DataType.STRING)
    @Schema(description = "the url for the external document")
    private String url;

    @Nullable
    @Column(name = "description")
    @ColumnTransformer(
            read = "pgp_sym_decrypt(description::bytea,'${aes.key}')",
            write = "pgp_sym_encrypt(?, '${aes.key}') "
    )
    @TypeDef(type = DataType.STRING)
    @Schema(description = "the optional description of the document")
    private String description;

    public Document(@NotNull String name, @NotNull String url, @Nullable String description) {
        this(null, name, url, description);
    }

    DocumentResponseDTO asResponseDTO() {
        return new DocumentResponseDTO(id, name, url, description);
    }
}
