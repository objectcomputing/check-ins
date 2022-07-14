package com.objectcomputing.checkins.services.memberprofile;

import io.micronaut.core.annotation.Nullable;
import javax.validation.constraints.NotBlank;

public class MemberProfileUtils {
    public static String getFullName(MemberProfile memberProfile) {
        if (memberProfile == null) {
            return null;
        }
        return constructFullName(memberProfile.getFirstName(), memberProfile.getMiddleName(),
                memberProfile.getLastName(), memberProfile.getSuffix());
    }

    public static String getFullName(MemberProfileCreateDTO createDTO) {
        if (createDTO == null) {
            return null;
        }
        return constructFullName(createDTO.getFirstName(), createDTO.getMiddleName(),
                createDTO.getLastName(), createDTO.getSuffix());
    }

    public static String getFullName(MemberProfileUpdateDTO updateDTO) {
        if (updateDTO == null) {
            return null;
        }
        return constructFullName(updateDTO.getFirstName(), updateDTO.getMiddleName(),
                updateDTO.getLastName(), updateDTO.getSuffix());
    }

    public static String getFullName(MemberProfileResponseDTO responseDTO) {
        if (responseDTO == null) {
            return null;
        }
        return constructFullName(responseDTO.getFirstName(), responseDTO.getMiddleName(),
                responseDTO.getLastName(), responseDTO.getSuffix());
    }

    private static String constructFullName(@NotBlank String firstName, @Nullable String middleName,
                                            @NotBlank String lastName, @Nullable String suffix) {
        return firstName +
                (middleName != null && !middleName.isBlank() ? " " + middleName : "") + " " +
                lastName +
                (suffix != null && !suffix.isBlank() ? ", " + suffix : "");
    }
}
