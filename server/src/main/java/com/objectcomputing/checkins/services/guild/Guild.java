package com.objectcomputing.checkins.services.guild;

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
import io.micronaut.core.annotation.Nullable;

import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "guild")
public class Guild {
    @Id
    @Column(name = "id")
    @AutoPopulated
    @TypeDef(type = DataType.STRING)
    @Schema(description = "the id of the guild", required = true)
    private UUID id;

    @NotBlank
    @Column(name = "name", unique = true)
    @ColumnTransformer(
            read = "pgp_sym_decrypt(name::bytea,'${aes.key}')",
            write = "pgp_sym_encrypt(?,'${aes.key}') "
    )
    @Schema(description = "name of the guild")
    private String name;


    @Nullable
    @Column(name="link", unique=true)
    @ColumnTransformer(
        read = "pgp_sym_decrypt(link::bytea,'${aes.key}')",
        write = "pgp_sym_encrypt(?,'${aes.key}') "
    )
    @Schema(description="link to the homepage of the guild")
    private String link;

    @NotBlank
    @Column(name = "description")
    @ColumnTransformer(
            read = "pgp_sym_decrypt(description::bytea,'${aes.key}')",
            write = "pgp_sym_encrypt(?,'${aes.key}') "
    )
    @Schema(description = "description of the guild")
    private String description;



    public Guild(String name, String description, @Nullable String link) {
        this(null, name, description, link);
    }

    public Guild(UUID id, String name, String description, @Nullable String link) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.link = link;
    }

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Nullable
    public String getLink() {
        return this.link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Guild guild = (Guild) o;
        return Objects.equals(id, guild.id) &&
                Objects.equals(name, guild.name) &&
                Objects.equals(description, guild.description) &&
                Objects.equals(link, this.link);

    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, link);
    }

    @Override
    public String toString() {
        return "Guild{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", link='" + link +
                '}';
    }
}
