package com.objectcomputing.checkins.services.role.member_roles;

import jakarta.inject.Singleton;
import com.objectcomputing.checkins.services.role.MemberRoleDTO;
import com.objectcomputing.checkins.services.role.Role;
import com.objectcomputing.checkins.services.role.RoleRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.objectcomputing.checkins.util.Util.nullSafeUUIDToString;

@Singleton
public class MemberRoleServicesImpl implements MemberRoleServices {

    private final MemberRoleRepository memberRoleRepository;
    private final RoleRepository roleRepository;

    public MemberRoleServicesImpl(MemberRoleRepository memberRoleRepository, RoleRepository roleRepository) {
        this.memberRoleRepository = memberRoleRepository;
        this.roleRepository = roleRepository;
    }

    public List<MemberRole> findAll() {
        return memberRoleRepository.findAll();
    }

    public List<MemberRoleDTO> getAllMembersGroupedByRole() {
        List<MemberRoleDTO> groupedMemberRoles = new ArrayList<>();
        Iterable<Role> allRoles = roleRepository.findAll();

        for (Role role : allRoles) {
            List<UUID> memberIds = memberRoleRepository.findAllMembersWithRole(nullSafeUUIDToString(role.getId()));
            MemberRoleDTO memberRoleDTO = new MemberRoleDTO();
            memberRoleDTO.setRoleId(role.getId());
            memberRoleDTO.setRole(role.getRole());
            memberRoleDTO.setDescription(role.getDescription());
            memberRoleDTO.setMemberIds(memberIds);
            groupedMemberRoles.add(memberRoleDTO);
        }

        return groupedMemberRoles;
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
