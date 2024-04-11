package com.objectcomputing.checkins.services.role.member_roles;

import jakarta.inject.Singleton;
import java.lang.reflect.Member;
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


    public void delete(MemberRoleId id){
        memberRoleRepository.deleteById(id);
    }

    public void removeMemberFromRoles(UUID memberid){
        // errors occurred when using UUID directly in query but string works
        memberRoleRepository.removeMemberFromRoles(memberid.toString());

    }

    public MemberRole saveByIds(UUID memberId, UUID roleId){
        return memberRoleRepository.save(new MemberRole(memberId, roleId));
    }


    public Optional<MemberRole> findById(MemberRoleId memberRoleId) {
        return memberRoleRepository.findById(memberRoleId);
    }


    public void removeAllByRoleId(UUID roleId){
        memberRoleRepository.deleteByRoleId(roleId);
    }
}
