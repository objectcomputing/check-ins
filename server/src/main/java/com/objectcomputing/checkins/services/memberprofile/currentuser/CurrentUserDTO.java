package com.objectcomputing.checkins.services.memberprofile.currentuser;

import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.permissions.Permission;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.Nullable;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Objects;

@Introspected
public class CurrentUserDTO {

    @NotBlank
    @Schema(description = "first name of the user")
    private String firstName;

    @NotBlank
    @Schema(description = "last name of the user")
    private String lastName;

    @NotBlank
    @Schema(description = "full name of the user")
    private String name;

    @Nullable
    @Schema(description = "User's permissions")
    private List<Permission> permissions;

    @Nullable
    @Schema(description = "User's roles")
    private List<String> role;

    @Nullable
    @Schema(description = "Image URL of the user")
    private String imageUrl;

    @NotNull
    @Schema(implementation = MemberProfile.class, required = true)
    private MemberProfile memberProfile;

    @NotBlank
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(@NotBlank String firstName) {
        this.firstName = firstName;
    }

    @NotBlank
    public String getLastName() {
        return lastName;
    }

    public void setLastName(@NotBlank String lastName) {
        this.lastName = lastName;
    }

    @NotBlank
    public String getName() {
        return name;
    }

    public void setName(@NotBlank String name) {
        this.name = name;
    }

    @Nullable
    public List<Permission> getPermissions() {
        return permissions;
    }

    public void setPermissions(@Nullable List<Permission> permissions) {
        this.permissions = permissions;
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
        return Objects.equals(firstName, that.firstName) && Objects.equals(lastName, that.lastName) && Objects.equals(name, that.name) && Objects.equals(permissions, that.permissions) && Objects.equals(role, that.role) && Objects.equals(imageUrl, that.imageUrl) && Objects.equals(memberProfile, that.memberProfile);
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstName, lastName, name, permissions, role, imageUrl, memberProfile);
    }
}
