package com.objectcomputing.checkins.services.memberprofile.currentuser;

import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import java.util.Objects;

@Introspected
public class CurrentUserDTO {

    @NotNull
    @Schema(description = "full name of the employee", required = true)
    private String name;

    @Nullable
    @Schema(description = "employee's role at the company")
    private String role ;

    @Nullable
    @Schema(description = "Image URL of the user")
    private String imageUrl;

    @NotNull
    @Schema(implementation = MemberProfile.class, required = true)
    private MemberProfile memberProfile;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Nullable
    public String getRole() {
        return role;
    }

    public void setRole(@Nullable String role) {
        this.role = role;
    }

    @Nullable
    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(@Nullable String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public MemberProfile getMemberProfile() {
        return memberProfile;
    }

    public void setMemberProfile(MemberProfile memberProfile) {
        this.memberProfile = memberProfile;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CurrentUserDTO that = (CurrentUserDTO) o;
        return name.equals(that.name) &&
                Objects.equals(role, that.role) &&
                Objects.equals(imageUrl, that.imageUrl) &&
                memberProfile.equals(that.memberProfile);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, role, imageUrl, memberProfile);
    }
}
