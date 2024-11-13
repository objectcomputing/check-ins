package com.objectcomputing.checkins.services.memberprofile.currentuser;

import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.role.RoleType;
import com.objectcomputing.checkins.services.permissions.Permission;
import com.objectcomputing.checkins.exceptions.PermissionException;

public interface CurrentUserServices {

    MemberProfile findOrSaveUser(String firstName, String lastName, String workEmail);

    boolean hasRole(RoleType role);

    boolean isAdmin();

    MemberProfile getCurrentUser();

    boolean hasPermission(Permission permission);

}
