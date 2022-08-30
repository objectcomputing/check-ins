package com.objectcomputing.checkins.services.document;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.annotation.AutoPopulated;
import io.micronaut.data.annotation.TypeDef;
import io.micronaut.data.jdbc.annotation.ColumnTransformer;
import io.micronaut.data.model.DataType;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "documents")
public class Document {

    @Id
    @Column(name = "id")
    @AutoPopulated
    @TypeDef(type = DataType.STRING)
    @Schema(description = "id of the document", required = true)
    private UUID id;

    @NotNull
    @Column(name = "name")
    @ColumnTransformer(
            read = "pgp_sym_decrypt(name::bytea,'${aes.key}')",
            write = "pgp_sym_encrypt(?, '${aes.key}') "
    )
    @TypeDef(type = DataType.STRING)
    @Schema(description = "the name of the document", required = true)
    private String name;

    @Nullable
    @Column(name = "description")
    @ColumnTransformer(
            read = "pgp_sym_decrypt(description::bytea,'${aes.key}')",
            write = "pgp_sym_encrypt(?, '${aes.key}') "
    )
    @TypeDef(type = DataType.STRING)
    @Schema(description = "description of the document")
    private String description;

    @NotNull
    @Column(name = "url")
    @ColumnTransformer(
            read = "pgp_sym_decrypt(url::bytea,'${aes.key}')",
            write = "pgp_sym_encrypt(?, '${aes.key}') "
    )
    @TypeDef(type = DataType.STRING)
    @Schema(description = "the URL where the document is accessed")
    private String url;

    public Document(String name, @Nullable String description, String url) {
        this.id = null;
        this.name = name;
        this.description = description;
        this.url = url;
    }

    public Document(UUID id, String name, @Nullable String description, String url) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.url = url;
    }

    public Document() {}

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

    @Nullable
    public String getDescription() {
        return description;
    }

    public void setDescription(@Nullable String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Document document = (Document) o;
        return Objects.equals(id, document.id) && Objects.equals(name, document.name) && Objects.equals(description, document.description) && Objects.equals(url, document.url);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, url);
    }

    @Override
    public String toString() {
        return "Document{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
