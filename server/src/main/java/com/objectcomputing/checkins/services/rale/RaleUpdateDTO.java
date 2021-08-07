package com.objectcomputing.checkins.services.rale;

import com.objectcomputing.checkins.services.rale.member.RaleMemberResponseDTO;
import com.objectcomputing.checkins.services.rale.member.RaleMemberUpdateDTO;
import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.Schema;

import io.micronaut.core.annotation.Nullable;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static com.objectcomputing.checkins.util.Util.nullSafeUUIDFromString;

@Introspected
public class RaleUpdateDTO {
    @NotNull
    private UUID id;

    @NotBlank
    @Schema(required = true, description = "name of the rale")
    private RaleType rale;

    @Nullable
    @Schema(description = "description of the rale")
    private String description;

    @Schema(description = "members of this rale")
    private List<RaleMemberUpdateDTO> raleMembers;

    public RaleUpdateDTO(UUID id, RaleType rale, @Nullable String description) {
        this.id = id;
        this.rale = rale;
        this.description = description;
    }

    public RaleUpdateDTO(String id, RaleType rale, String description) {
        this(nullSafeUUIDFromString(id), rale, description);
    }

    public RaleUpdateDTO() {
        id = UUID.randomUUID();
    }

    @Override
    public String toString() {
        return "RaleUpdateDTO{" +
                "id=" + id +
                ", name='" + rale + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RaleUpdateDTO updateDTO = (RaleUpdateDTO) o;
        return Objects.equals(id, updateDTO.id) &&
                Objects.equals(rale, updateDTO.rale) &&
                Objects.equals(description, updateDTO.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, rale, description);
    }

    public List<RaleMemberUpdateDTO> getRaleMembers() {
        return raleMembers;
    }

    public void setRaleMembers(List<RaleMemberUpdateDTO> raleMembers) {
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

    @Introspected
    public static class RaleMemberUpdateDTO {
        @Schema(description = "ID of the entity to update")
        private UUID id;

        @Schema(description = "whether member is lead or not represented by true or false respectively",
                nullable = true)
        private Boolean lead;

        @NotNull
        @Schema(description = "Member who is on this rale")
        private UUID memberId;

        @NotNull
        @Schema(description = "Rale to which the member belongs")
        private UUID raleId;

        public RaleMemberUpdateDTO(UUID id, UUID raleId, UUID memberId, Boolean lead) {
            this.id = id;
            this.raleId = raleId;
            this.memberId = memberId;
            this.lead = lead;
        }

        public UUID getId() {
            return id;
        }

        public void setId(UUID id) {
            this.id = id;
        }

        public Boolean getLead() {
            return lead;
        }

        public void setLead(Boolean lead) {
            this.lead = lead;
        }

        public UUID getMemberId() {
            return memberId;
        }

        public void setMemberId(UUID memberId) {
            this.memberId = memberId;
        }

        public UUID getRaleId() {
            return raleId;
        }

        public void setRaleId(UUID raleId) {
            this.raleId = raleId;
        }
    }
}
