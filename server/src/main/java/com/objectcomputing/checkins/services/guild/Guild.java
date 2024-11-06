package com.objectcomputing.checkins.services.guild;

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
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    @Schema(description = "the id of the guild")
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

    @Nullable
    @Column(name = "description")
    @ColumnTransformer(
            read = "pgp_sym_decrypt(description::bytea,'${aes.key}')",
            write = "pgp_sym_encrypt(?,'${aes.key}') "
    )
    @Schema(description = "description of the guild")
    private String description;

    @NotNull
    @Column(name = "community")
    @Schema(description = "Is the guild a community")
    private boolean community;

    @NotNull
    @Column(name = "is_active")
    @Schema(description = "whether the guild is active")
    private boolean active = true;

    public Guild(String name, @Nullable String description, @Nullable String link, boolean community, boolean active) {
        this(null, name, description, link, community, active);
    }

    public Guild(UUID id, String name, @Nullable String description, @Nullable String link, boolean community, boolean active) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.link = link;
        this.community = community;
        this.active = active;
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
                Objects.equals(community, guild.community) &&
                Objects.equals(active, guild.active);

    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, link, community, active);
    }

    @Override
    public String toString() {
        return "Guild{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", link='" + link +
                ", community=" + community +
                ", active=" + active +
                '}';
    }
}
