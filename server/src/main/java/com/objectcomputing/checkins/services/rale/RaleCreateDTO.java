package com.objectcomputing.checkins.services.rale;

import com.objectcomputing.checkins.services.rale.member.RaleMemberCreateDTO;
import com.objectcomputing.checkins.services.rale.RaleType;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.data.annotation.TypeDef;
import io.micronaut.data.model.DataType;
import io.swagger.v3.oas.annotations.media.Schema;

import io.micronaut.core.annotation.Nullable;

import javax.persistence.Column;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static com.objectcomputing.checkins.services.role.RoleType.Constants.*;

@Introspected
public class RaleCreateDTO {
//    @NotBlank
//    @Schema(required = true, description = "name of the rale")
//    private RaleType rale;

    @NotNull
    @Column(name = "rale")
    @TypeDef(type = DataType.STRING)
    @Schema(description = "rale this member has", required = true,
            allowableValues = {ADMIN_ROLE, PDL_ROLE, MEMBER_ROLE})
    private RaleType rale;

    @Nullable
    @Schema(description = "description of the rale")
    private String description;

    @Schema(description = "members of this rale")
    private List<RaleMemberCreateDTO> raleMembers;

    public RaleCreateDTO(RaleType rale, @Nullable String description) {
        this.rale = rale;
        this.description = description;
    }

    public RaleCreateDTO() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RaleCreateDTO that = (RaleCreateDTO) o;
        return Objects.equals(rale, that.rale) &&
                Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rale, description);
    }

    public List<RaleMemberCreateDTO> getRaleMembers() {
        return raleMembers;
    }

    public void setRaleMembers(List<RaleMemberCreateDTO> raleMembers) {
        this.raleMembers = raleMembers;
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
    public static class RaleMemberCreateDTO {

        @Schema(description = "whether member is lead or not represented by true or false respectively",
                nullable = true)
        private Boolean lead;

        @NotNull
        @Schema(description = "Member who is on this rale")
        private UUID memberId;

        public RaleMemberCreateDTO(UUID memberId, Boolean lead) {
            this.memberId = memberId;
            this.lead = lead;
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
    }
}
