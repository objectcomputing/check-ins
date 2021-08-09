package com.objectcomputing.checkins.services.fixture;

import com.objectcomputing.checkins.services.guild.GuildCreateDTO;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.role.Role;
import com.objectcomputing.checkins.services.role.RoleCreateDTO;
import com.objectcomputing.checkins.services.role.RoleUpdateDTO;
import com.objectcomputing.checkins.services.role.member.RoleMember;
import com.objectcomputing.checkins.services.role.member.RoleMemberCreateDTO;
import com.objectcomputing.checkins.services.role.member.RoleMemberResponseDTO;
import com.objectcomputing.checkins.services.role.member.RoleMemberUpdateDTO;


public interface RoleMemberFixture extends RepositoryFixture{
    default RoleMember createDefaultRoleMember(Role roleEntity, MemberProfile memberProfile) {
        return getRoleMemberRepository().save(new RoleMember(null, roleEntity.getId(), memberProfile.getId(), false));
    }

    default RoleMember createLeadRoleMember(Role roleEntity, MemberProfile memberProfile) {
        return getRoleMemberRepository().save(new RoleMember(null, roleEntity.getId(), memberProfile.getId(), true));
    }

    default RoleCreateDTO.RoleMemberCreateDTO createDefaultRoleMemberDto(MemberProfile memberProfile, Boolean lead) {
        return new RoleCreateDTO.RoleMemberCreateDTO(memberProfile.getId(), lead);
    }

    default RoleUpdateDTO.RoleMemberUpdateDTO updateDefaultRoleMemberDto(Role entity, MemberProfile memberProfile, Boolean lead) {
        return new RoleUpdateDTO.RoleMemberUpdateDTO(null, entity.getId(), memberProfile.getId(), lead);
    }

    default RoleMemberCreateDTO createDefaultRoleMemberDto(Role roleEntity, MemberProfile memberProfile, Boolean lead) {
        return new RoleMemberCreateDTO(roleEntity.getId(), memberProfile.getId(), true);
    }

    default RoleMemberResponseDTO dtoFromEntity(RoleMember memberEntity, MemberProfile memberProfile) {
        return new RoleMemberResponseDTO(memberEntity.getId(), memberProfile.getFirstName(), memberProfile.getLastName(),
                memberProfile.getId(), memberEntity.isLead());
    }
}


//import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
//import com.objectcomputing.checkins.services.role.Role;
//import com.objectcomputing.checkins.services.role.RoleCreateDTO;
//import com.objectcomputing.checkins.services.role.RoleUpdateDTO;
//import com.objectcomputing.checkins.services.role.member.RoleMember;
//import com.objectcomputing.checkins.services.role.member.RoleMemberCreateDTO;
//import com.objectcomputing.checkins.services.role.member.RoleMemberResponseDTO;
//
//
//public interface RoleMemberFixture extends RepositoryFixture{
//    default RoleMember createDefaultRoleMember(Role roleEntity, MemberProfile memberProfile) {
//        return getRoleMemberRepository().save(new RoleMember(null, roleEntity.getId(), memberProfile.getId(), false));
//    }
//
//    default RoleMember createLeadRoleMember(Role roleEntity, MemberProfile memberProfile) {
//        return getRoleMemberRepository().save(new RoleMember(null, roleEntity.getId(), memberProfile.getId(), true));
//    }
//
//    default RoleCreateDTO.RoleMemberCreateDTO createDefaultRoleMemberDto(MemberProfile memberProfile, Boolean lead) {
//        return new RoleCreateDTO.RoleMemberCreateDTO(memberProfile.getId(), lead);
//    }
//
//    default RoleUpdateDTO.RoleMemberUpdateDTO updateDefaultRoleMemberDto(Role entity, MemberProfile memberProfile, Boolean lead) {
//        return new RoleUpdateDTO.RoleMemberUpdateDTO(null, entity.getId(), memberProfile.getId(), lead);
//    }
//
//    default RoleMemberCreateDTO createDefaultRoleMemberDto(Role roleEntity, MemberProfile memberProfile, Boolean lead) {
//        return new RoleMemberCreateDTO(roleEntity.getId(), memberProfile.getId(), true);
//    }
//
//    default RoleMemberResponseDTO dtoFromEntity(RoleMember memberEntity, MemberProfile memberProfile) {
//        return new RoleMemberResponseDTO(memberEntity.getId(), memberProfile.getFirstName(), memberProfile.getLastName(),
//                memberProfile.getId(), memberEntity.isLead());
//    }
//}
