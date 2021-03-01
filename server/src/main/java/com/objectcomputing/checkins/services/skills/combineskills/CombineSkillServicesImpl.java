package com.objectcomputing.checkins.services.skills.combineskills;

import com.objectcomputing.checkins.services.member_skill.MemberSkill;
import com.objectcomputing.checkins.services.member_skill.MemberSkillServices;
import com.objectcomputing.checkins.services.memberprofile.currentuser.CurrentUserServices;
import com.objectcomputing.checkins.services.skills.Skill;
import com.objectcomputing.checkins.services.skills.SkillServices;
import com.objectcomputing.checkins.services.validate.PermissionsValidation;

import javax.inject.Singleton;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
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

    public Skill combine(@NotNull @Valid CombineSkillsDTO skillDTO) {

        boolean isAdmin = currentUserServices.isAdmin();
        Skill returnSkill = null;

        permissionsValidation.validatePermissions(!isAdmin, "User is unauthorized to do this operation");

        Skill newSkill = new Skill(skillDTO.getName(), skillDTO.getDescription());
        UUID[] skillsArray = skillDTO.getSkillsToCombine();
        List<UUID> memberIds = new ArrayList<>();

        returnSkill = skillServices.save(newSkill);

        for (UUID skillToCombine : skillsArray) {

            Set<MemberSkill> memberSkills = memberSkillServices.findByFields(null, skillToCombine);

            changeMemberSkills(memberSkills, returnSkill, memberIds);

            skillServices.delete(skillToCombine);

        }

        return returnSkill;
    }

    private void changeMemberSkills(Set<MemberSkill> memberSkills, Skill returnSkill, List<UUID> memberIds) {

        for (MemberSkill memberSkill : memberSkills) {
            if (!memberIds.contains(memberSkill.getMemberid())) {
                memberSkill.setSkillid(returnSkill.getId());
                memberSkillServices.update(memberSkill);
                memberIds.add(memberSkill.getMemberid());
            }
            else{
                memberSkillServices.delete(memberSkill.getId());
            }
        }
    }

}
