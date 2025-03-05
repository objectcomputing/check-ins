package com.objectcomputing.checkins.services.memberprofile.currentuser;

import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileResponseDTO;
import com.objectcomputing.checkins.services.permissions.Permission;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.Nullable;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
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
    @Schema(implementation = MemberProfile.class)
    private MemberProfileResponseDTO memberProfile;

}
