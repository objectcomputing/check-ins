package com.objectcomputing.checkins.services.fixture;

import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.role.Role;
import com.objectcomputing.checkins.services.role.member_roles.MemberRole;
import com.objectcomputing.checkins.services.role.member_roles.MemberRoleId;

import java.util.Optional;

public interface MemberRoleFixture extends RepositoryFixture {

    default void assignMemberToRole(MemberProfile memberProfile, Role role) {
        getMemberRoleRepository()
                .saveByIds(memberProfile.getId(), role.getId());
    }

    default Optional<MemberRole> findMemberRole(MemberRoleId id){
        return getMemberRoleRepository().findById(id);
    }

}


