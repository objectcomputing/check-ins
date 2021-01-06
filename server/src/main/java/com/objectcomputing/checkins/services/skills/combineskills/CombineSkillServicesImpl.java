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

        returnSkill = skillServices.save(newSkill);

        for (UUID skillToCombine : skillsArray) {

            Set<MemberSkill> memberSkills = memberSkillServices.findByFields(null, skillToCombine);

            changeMemberSkills(memberSkills, returnSkill);

            skillServices.delete(skillToCombine);

        }

        return returnSkill;

    }

    private void changeMemberSkills(Set<MemberSkill> memberSkills, Skill returnSkill) {

        for (MemberSkill memberSkill : memberSkills) {

            memberSkill.setSkillid(returnSkill.getId());
            memberSkillServices.update(memberSkill);
        }

    }

}
