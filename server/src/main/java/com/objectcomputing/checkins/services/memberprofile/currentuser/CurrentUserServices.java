package com.objectcomputing.checkins.services.memberprofile.currentuser;

import com.objectcomputing.checkins.security.permissions.Permissions;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.role.RoleType;

public interface CurrentUserServices {

    MemberProfile findOrSaveUser(String firstName, String lastName, String workEmail);

    boolean hasRole(RoleType role);

    boolean isAdmin();

    MemberProfile getCurrentUser();

}
