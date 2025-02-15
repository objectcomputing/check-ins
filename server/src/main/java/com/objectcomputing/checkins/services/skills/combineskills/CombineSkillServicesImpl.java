package com.objectcomputing.checkins.services.skills.combineskills;

import com.objectcomputing.checkins.services.permissions.Permission;
import com.objectcomputing.checkins.services.permissions.RequiredPermission;
import com.objectcomputing.checkins.services.member_skill.MemberSkill;
import com.objectcomputing.checkins.services.member_skill.MemberSkillServices;
import com.objectcomputing.checkins.services.memberprofile.currentuser.CurrentUserServices;
import com.objectcomputing.checkins.services.skills.Skill;
import com.objectcomputing.checkins.services.skills.SkillServices;
import com.objectcomputing.checkins.services.validate.PermissionsValidation;
import jakarta.inject.Singleton;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Singleton
public class CombineSkillServicesImpl implements CombineSkillServices {

    private final SkillServices skillServices;
    private final MemberSkillServices memberSkillServices;
    private final PermissionsValidation permissionsValidation;
    private final CurrentUserServices currentUserServices;

    public CombineSkillServicesImpl(SkillServices skillServices,
                                    MemberSkillServices memberSkillServices,
                                    PermissionsValidation permissionsValidation,
                                    CurrentUserServices currentUserServices) {
        this.skillServices = skillServices;
        this.memberSkillServices = memberSkillServices;
        this.permissionsValidation = permissionsValidation;
        this.currentUserServices = currentUserServices;
    }

    @RequiredPermission(Permission.CAN_EDIT_SKILLS)
    public Skill combine(@NotNull @Valid CombineSkillsDTO skillDTO) {
        Set<Skill> existingSkills = skillServices.findByValue(skillDTO.getName(), null);
        for (Skill existingSkill : existingSkills) {
            if (existingSkill.getName().equals(skillDTO.getName())) {
                return combineWithExistingSkill(existingSkill, skillDTO);
            }
        }

        return combineToNewSkill(skillDTO);
    }

    private Skill combineWithExistingSkill(Skill existingSkill, CombineSkillsDTO skillsDTO) {
        Set<UUID> memberIds = new HashSet<>();
        for (UUID id : skillsDTO.getSkillsToCombine()) {
            Set<MemberSkill> memberSkills = memberSkillServices.findByFields(null, id);
            changeMemberSkills(memberSkills, existingSkill, memberIds);

            if (!id.equals(existingSkill.getId())) {
                skillServices.delete(id);
            }
        }

        existingSkill.setDescription(skillsDTO.getDescription());
        return skillServices.update(existingSkill);
    }

    private Skill combineToNewSkill(CombineSkillsDTO skillDTO) {
        Skill newSkill = new Skill(skillDTO.getName(), skillDTO.getDescription());
        Skill returnSkill = skillServices.save(newSkill);

        UUID[] skillsArray = skillDTO.getSkillsToCombine();
        Set<UUID> memberIds = new HashSet<>();

        for (UUID skillToCombine : skillsArray) {
            Set<MemberSkill> memberSkills = memberSkillServices.findByFields(null, skillToCombine);
            changeMemberSkills(memberSkills, returnSkill, memberIds);
            skillServices.delete(skillToCombine);
        }

        return returnSkill;
    }

    private void changeMemberSkills(Set<MemberSkill> memberSkills, Skill returnSkill, Set<UUID> updatedMembers) {

        for (MemberSkill memberSkill : memberSkills) {
            if (!updatedMembers.contains(memberSkill.getMemberid())) {
                memberSkill.setSkillid(returnSkill.getId());
                memberSkillServices.update(memberSkill);
                updatedMembers.add(memberSkill.getMemberid());
            } else {
                memberSkillServices.delete(memberSkill.getId());
            }
        }
    }

}
