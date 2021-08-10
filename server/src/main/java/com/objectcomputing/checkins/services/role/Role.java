package com.objectcomputing.checkins.services.role;

import io.micronaut.data.annotation.AutoPopulated;
import io.micronaut.data.annotation.TypeDef;
import io.micronaut.data.jdbc.annotation.ColumnTransformer;
import io.micronaut.data.model.DataType;
import io.swagger.v3.oas.annotations.media.Schema;

import io.micronaut.core.annotation.Nullable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Objects;
import java.util.UUID;

import static com.objectcomputing.checkins.services.role.RoleType.Constants.*;

@Entity
@Table(name = "role")
public class Role {
    @Id
    @Column(name = "id")
    @AutoPopulated
    @TypeDef(type = DataType.STRING)
    @Schema(description = "the id of the role", required = true)
    private UUID id;

    @NotBlank
    @Column(name = "role", unique = true)
//    @ColumnTransformer(
//            read = "pgp_sym_decrypt(role::bytea,'${aes.key}')",
//            write = "pgp_sym_encrypt(?,'${aes.key}') "
//    )

    @NotNull
    @Schema(description = "role this member has", required = true,
            allowableValues = {ADMIN_ROLE, PDL_ROLE, MEMBER_ROLE})
    @TypeDef(type = DataType.OBJECT)
    private RoleType role;
//    @Schema(description = "name of the role")
//    private RoleType role;

    
    
    @Nullable
    @Column(name = "description")
//    @ColumnTransformer(
//            read = "pgp_sym_decrypt(description::bytea,'${aes.key}')",
//            write = "pgp_sym_encrypt(?,'${aes.key}') "
//    )
    @Schema(description = "description of the role", nullable = true)
    private String description;

    public Role(RoleType role, @Nullable String description) {
        this(null, role, description);
    }

    public Role(UUID id, RoleType role, @Nullable String description) {
        this.id = id;
        this.role = role;
        this.description = description;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public RoleType getRole() {
        return role;
    }

    public void setRole(RoleType role) {
        this.role = role;
    }

    @Nullable
    public String getDescription() {
        return description;
    }

    public void setDescription(@Nullable String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Role role = (Role) o;
        return Objects.equals(id, role.id) &&
                Objects.equals(role, role.role) &&
                Objects.equals(description, role.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, role, description);
    }

    @Override
    public String toString() {
        return "Role{" +
                "id=" + id +
                ", name='" + role + '\'' +
                ", description='" + description +
                '}';
    }
}
