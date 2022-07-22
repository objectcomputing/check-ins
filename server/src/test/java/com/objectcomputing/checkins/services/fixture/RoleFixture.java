package com.objectcomputing.checkins.services.fixture;

import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.role.Role;
import com.objectcomputing.checkins.services.role.RoleType;
import com.objectcomputing.checkins.services.role.member_roles.MemberRole;

import java.util.Optional;
import java.util.UUID;

public interface RoleFixture extends RepositoryFixture, PermissionFixture, MemberRoleFixture {
    default Role createRole(Role role) {
        return getRoleRepository().save(new Role(role.getRole(), role.getDescription()));
    }

    default Role createAndAssignAdminRole(MemberProfile memberProfile) {
        return createAndAssignRole(RoleType.ADMIN, memberProfile);
    }

    // TODO phase out RoleType
    default Role createAndAssignRole(RoleType type, MemberProfile memberProfile) {
        Role role = getRoleRepository().save(new Role(type.name(), "role description"));
        getMemberRoleRepository()
                .saveByIds(memberProfile.getId(), role.getId());
        return role;
    }

    default Role createAndAssignRole(String roleName, MemberProfile memberProfile) {
        Role role = getRoleRepository().save(new Role(roleName, "role description"));
        getMemberRoleRepository()
                .save(new MemberRole(memberProfile.getId(), role.getId()));

        return role;
    }

    default Role findRole(Role role) {
        return findRoleById(role.getId());
    }

    default Role findRoleById(UUID uuid) {
        return getRoleRepository().findById(uuid).orElse(null);
    }

    default Role assignHrRole(MemberProfile memberProfile) {
        return assignRoleToMemberProfile(RoleType.HR, memberProfile);
    }

    default Role assignAdminRole(MemberProfile memberProfile) {
        return assignRoleToMemberProfile(RoleType.ADMIN, memberProfile);
    }

    default Role assignPdlRole(MemberProfile memberProfile) {
        return assignRoleToMemberProfile(RoleType.PDL, memberProfile);
    }

    default Role assignMemberRole(MemberProfile memberProfile) {
        return assignRoleToMemberProfile(RoleType.MEMBER, memberProfile);
    }

    // TODO phase out RoleType
    default Role assignRoleToMemberProfile(RoleType type, MemberProfile memberProfile) {
        Optional<Role> role = getRoleRepository().findByRole(type.name());
        assignMemberToRole(memberProfile, role.get());
        return role.get();
    }

    default void createAndAssignRoles() {
        //Create Roles
        Role adminRole = createRole(new Role(RoleType.ADMIN.name(), "Admin Role"));
        Role pdlRole = createRole(new Role(RoleType.PDL.name(), "Pdl Role"));
        Role memberRole = createRole(new Role(RoleType.MEMBER.name(), "Member Role"));

        //Save permissions
        saveAllPermissions();

        //Setup permissions for Roles
        setPermissionsForAdmin(adminRole.getId());
        setPermissionsForPdl(pdlRole.getId());
        setPermissionsForMember(memberRole.getId());
    }

}
