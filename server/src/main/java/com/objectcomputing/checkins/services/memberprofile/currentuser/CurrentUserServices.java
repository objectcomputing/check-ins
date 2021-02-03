package com.objectcomputing.checkins.services.memberprofile.currentuser;

import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.role.RoleType;

import java.util.UUID;

public interface CurrentUserServices {

    MemberProfile findOrSaveUser(String name, String workEmail);

    boolean hasRole(RoleType role);

    boolean isAdmin();

    MemberProfile getCurrentUser();

    boolean isCurrentUserPdlFor(UUID memberId);

    void currentUserPdlFor(UUID memberId);
}
