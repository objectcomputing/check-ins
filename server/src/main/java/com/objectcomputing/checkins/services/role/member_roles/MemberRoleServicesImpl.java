package com.objectcomputing.checkins.services.role.member_roles;

import javax.inject.Singleton;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Singleton
public class MemberRoleServicesImpl implements MemberRoleServices {

    private final MemberRoleRepository memberRoleRepository;

    public MemberRoleServicesImpl(MemberRoleRepository memberRoleRepository) {
        this.memberRoleRepository = memberRoleRepository;
    }

    public List<MemberRole> findAll() {
        return memberRoleRepository.findAll();
    }


//    public void delete(MemberRole role){
//        memberRoleRepository.delete(role);
//    }

    public void removeMemberFromRoles(UUID memberid){
        // errors occurred when using UUID directly in query but string works
        memberRoleRepository.removeMemberFromRoles(memberid.toString());

    }

    public MemberRole saveByIds(UUID memberId, UUID roleId){
        return memberRoleRepository.saveByIds(memberId, roleId);
    }


    public Optional<MemberRole> findById(MemberRoleId memberRoleId) {
        return memberRoleRepository.findById(memberRoleId);
    }

    public void removeAllByRoleId(UUID roleId){
        memberRoleRepository.removeAllByRoleId(roleId);
    }
}
