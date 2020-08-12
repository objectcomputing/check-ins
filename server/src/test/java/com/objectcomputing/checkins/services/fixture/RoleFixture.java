package com.objectcomputing.checkins.services.fixture;

import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.role.Role;
import com.objectcomputing.checkins.services.role.RoleType;

import javax.transaction.Transactional;

@Transactional(Transactional.TxType.NEVER)
public interface RoleFixture extends RepositoryFixture {
    default Role createDefaultRoleRepository(MemberProfile memberProfile) {
       return createDefaultRoleRepository(RoleType.ADMIN, memberProfile);
    }

    default Role createDefaultRoleRepository(RoleType type, MemberProfile memberProfile) {
        return getRoleRepository().save(new Role(type, memberProfile.getUuid()));
    }

    default Role findRole(Role role) {
        return getRoleRepository().findById(role.getId()).orElse(null);
    }
}
