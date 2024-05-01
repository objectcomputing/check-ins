package com.objectcomputing.checkins.services.guild;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.annotation.AutoPopulated;
import io.micronaut.data.annotation.TypeDef;
import io.micronaut.data.jdbc.annotation.ColumnTransformer;
import io.micronaut.data.model.DataType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Objects;
import java.util.UUID;

@Entity
@Setter
@Getter
@NoArgsConstructor
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

    @NotNull
    @Column(name = "is_community")
    @Schema(description = "Is the guild a community")
    private Boolean isCommunity;

    public Guild(String name, String description, @Nullable String link, Boolean isCommunity) {
        this(null, name, description, link, isCommunity);
    }

    public Guild(UUID id, String name, String description, @Nullable String link, Boolean isCommunity) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.link = link;
        this.isCommunity = isCommunity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Guild guild = (Guild) o;
        return Objects.equals(id, guild.id) &&
                Objects.equals(name, guild.name) &&
                Objects.equals(description, guild.description) &&
                Objects.equals(link, guild.link) &&
                Objects.equals(isCommunity, guild.isCommunity);

    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, link, isCommunity);
    }

    @Override
    public String toString() {
        return "Guild{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", link='" + link +
                ", isCommunity=" + isCommunity +
                '}';
    }
}
