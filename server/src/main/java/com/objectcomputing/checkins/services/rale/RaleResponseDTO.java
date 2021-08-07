package com.objectcomputing.checkins.services.rale;

import com.objectcomputing.checkins.services.rale.member.RaleMemberResponseDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.Nullable;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Introspected
public class RaleResponseDTO {
    @NotNull
    private UUID id;

    @NotBlank
    @Schema(required = true, description = "name of the rale")
    private RaleType rale;

    @Nullable
    @Schema(description = "description of the rale")
    private String description;

    List<RaleMemberResponseDTO> raleMembers;

    public RaleResponseDTO(UUID id, RaleType rale, @Nullable String description) {
        this.id = id;
        this.rale = rale;
        this.description = description;
    }

    public RaleResponseDTO(String id, RaleType rale, @Nullable String description) {
        this(UUID.fromString(id), rale, description);
    }

    public RaleResponseDTO() {
        id = UUID.randomUUID();
    }

    @Override
    public String toString() {
        return "RaleResponseDTO{" +
                "id=" + id +
                ", rale='" + rale + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RaleResponseDTO that = (RaleResponseDTO) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(rale, that.rale) &&
                Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, rale, description);
    }

    public List<RaleMemberResponseDTO> getRaleMembers() {
        if (raleMembers == null) {
            raleMembers = new ArrayList<>();
        }
        return raleMembers;
    }

    public void setRaleMembers(List<RaleMemberResponseDTO> raleMembers) {
        this.raleMembers = raleMembers;
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
}
