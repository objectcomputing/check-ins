package com.objectcomputing.checkins.services.memberprofile;

import javax.annotation.Nullable;
import javax.validation.constraints.NotBlank;

public class MemberProfileUtils {
    public static String getFullName(MemberProfile memberProfile) {
        return constructFullName(memberProfile.getFirstName(), memberProfile.getMiddleName(),
                memberProfile.getLastName(), memberProfile.getSuffix());
    }

    public static String getFullName(MemberProfileCreateDTO createDTO) {
        return constructFullName(createDTO.getFirstName(), createDTO.getMiddleName(),
                createDTO.getLastName(), createDTO.getSuffix());
    }

    public static String getFullName(MemberProfileUpdateDTO updateDTO) {
        return constructFullName(updateDTO.getFirstName(), updateDTO.getMiddleName(),
                updateDTO.getLastName(), updateDTO.getSuffix());
    }

    private static String constructFullName(@NotBlank String firstName, @Nullable String middleName,
                                            @NotBlank String lastName, @Nullable String suffix) {
        return firstName +
                (middleName != null && !middleName.isBlank() ? " " + middleName : "") + " " +
                lastName +
                (suffix != null && !suffix.isBlank() ? ", " + suffix : "");
    }
}
