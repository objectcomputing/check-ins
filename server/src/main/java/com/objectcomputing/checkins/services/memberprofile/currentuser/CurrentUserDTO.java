package com.objectcomputing.checkins.services.memberprofile.currentuser;

import com.objectcomputing.checkins.services.memberprofile.MemberProfileEntity;
import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;

@Introspected
public class CurrentUserDTO {

    @Nullable
    @Schema(description = "full name of the user")
    private String name;

    @Nullable
    @Schema(description = "User's roles")
    private List<String> role;

    @Nullable
    @Schema(description = "Image URL of the user")
    private String imageUrl;

    @NotNull
    @Schema(implementation = MemberProfileEntity.class, required = true)
    private MemberProfileEntity memberProfileEntity;

    @Nullable
    public String getName() {
        return name;
    }

    public void setName(@Nullable String name) {
        this.name = name;
    }

    @Nullable
    public List<String> getRole() {
        return role;
    }

    public void setRole(@Nullable List<String> role) {
        this.role = role;
    }

    @Nullable
    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(@Nullable String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public MemberProfileEntity getMemberProfile() {
        return memberProfileEntity;
    }

    public void setMemberProfile(MemberProfileEntity memberProfileEntity) {
        this.memberProfileEntity = memberProfileEntity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CurrentUserDTO that = (CurrentUserDTO) o;
        return name.equals(that.name) &&
                Objects.equals(role, that.role) &&
                Objects.equals(imageUrl, that.imageUrl) &&
                memberProfileEntity.equals(that.memberProfileEntity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, role, imageUrl, memberProfileEntity);
    }
}
