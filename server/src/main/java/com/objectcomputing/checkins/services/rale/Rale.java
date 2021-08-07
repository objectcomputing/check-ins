package com.objectcomputing.checkins.services.rale;

import com.objectcomputing.checkins.services.role.RoleType;
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
@Table(name = "rale")
public class Rale {
    @Id
    @Column(name = "id")
    @AutoPopulated
    @TypeDef(type = DataType.STRING)
    @Schema(description = "the id of the rale", required = true)
    private UUID id;

    @NotBlank
    @Column(name = "rale", unique = true)
    @ColumnTransformer(
            read = "pgp_sym_decrypt(rale::bytea,'${aes.key}')",
            write = "pgp_sym_encrypt(?,'${aes.key}') "
    )

    @NotNull
    @Schema(description = "rale this member has", required = true,
            allowableValues = {ADMIN_ROLE, PDL_ROLE, MEMBER_ROLE})
    @TypeDef(type = DataType.OBJECT)
    private RaleType rale;
//    @Schema(description = "name of the rale")
//    private RaleType rale;

    
    
    @Nullable
    @Column(name = "description")
    @ColumnTransformer(
            read = "pgp_sym_decrypt(description::bytea,'${aes.key}')",
            write = "pgp_sym_encrypt(?,'${aes.key}') "
    )
    @Schema(description = "description of the rale", nullable = true)
    private String description;

    public Rale(RaleType rale, @Nullable String description) {
        this(null, rale, description);
    }

    public Rale(UUID id, RaleType rale, @Nullable String description) {
        this.id = id;
        this.rale = rale;
        this.description = description;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public RaleType getRale() {
        return rale;
    }

    public void setRale(RaleType rale) {
        this.rale = rale;
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
        Rale rale = (Rale) o;
        return Objects.equals(id, rale.id) &&
                Objects.equals(rale, rale.rale) &&
                Objects.equals(description, rale.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, rale, description);
    }

    @Override
    public String toString() {
        return "Rale{" +
                "id=" + id +
                ", name='" + rale + '\'' +
                ", description='" + description +
                '}';
    }
}
