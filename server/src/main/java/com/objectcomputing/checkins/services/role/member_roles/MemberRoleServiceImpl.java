package com.objectcomputing.checkins.services.role.member_roles;

import javax.inject.Singleton;
import java.util.List;
import java.util.UUID;

@Singleton
public class MemberRoleServiceImpl implements MemberRoleService{

    private final MemberRoleRepository memberRoleRepository;

    public MemberRoleServiceImpl(MemberRoleRepository memberRoleRepository) {
        this.memberRoleRepository = memberRoleRepository;
    }

    public List<MemberRole> findAll() {
        return memberRoleRepository.findAll();
    }


    public void deleteById(String memberid, String roleid){
        memberRoleRepository.deleteById(memberid, roleid);
    }

    public void removeMemberFromRoles(String memberid){
        memberRoleRepository.removeMemberFromRoles(memberid);

    }

    public MemberRole save(UUID memberid, UUID roleid){
        return memberRoleRepository.save(memberid, roleid);
    }
}
