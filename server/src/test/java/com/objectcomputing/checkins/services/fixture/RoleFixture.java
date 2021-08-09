package com.objectcomputing.checkins.services.fixture;

import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.role.*;

import java.util.UUID;

public interface RoleFixture extends MemberProfileFixture, RepositoryFixture{

    default Role createDefaultAdminRole(MemberProfile memberProfile) {
            return createDefaultRole(RoleType.ADMIN, memberProfile);
        }

        default Role createDefaultRole(RoleType type, MemberProfile memberProfile) {
            return getRoleRepository().save(new Role(memberProfile.getId(), type, "role description"));
        }

    default Role createDefaultRole() {
        return getRoleRepository().save(new Role(UUID.randomUUID(), RoleType.ADMIN, "Warriors"));
    }

    default Role createAnotherDefaultRole() {
        return getRoleRepository().save(new Role(UUID.randomUUID(), RoleType.ADMIN, "Warriors"));
    }

    default RoleCreateDTO createFromEntity(Role entity) {
        return new RoleCreateDTO(entity.getRole(), entity.getDescription());
    }

    default RoleUpdateDTO updateFromEntity(Role entity) {
        return new RoleUpdateDTO(entity.getId(), entity.getRole(), entity.getDescription());
    }

    default RoleResponseDTO responseFromEntity(Role entity) {
        return new RoleResponseDTO(entity.getId(), entity.getRole(), entity.getDescription());
    }

    default Role entityFromDTO(RoleUpdateDTO dto) {
        return new Role(dto.getId(), dto.getRole(), dto.getDescription());
    }

    
//    package com.objectcomputing.checkins.services.fixture;
//
//import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
//import com.objectcomputing.checkins.services.role.Role;
//import com.objectcomputing.checkins.services.role.RoleType;
//
//import java.util.UUID;
//
//    public interface RoleFixture extends RepositoryFixture {
//        default Role createDefaultAdminRole(MemberProfile memberProfile) {
//            return createDefaultRole(RoleType.ADMIN, memberProfile);
//        }
//
//        default Role createDefaultRole(RoleType type, MemberProfile memberProfile) {
//            return getRoleRepository().save(new Role(type, "role description", memberProfile.getId()));
//        }
//
//        default Role findRole(Role role) {
//            return findRoleById(role.getId());
//        }
//
//        default Role findRoleById(UUID uuid) {
//            return getRoleRepository().findById(uuid).orElse(null);
//        }
//
//    }
}
