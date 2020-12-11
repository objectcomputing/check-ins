package com.objectcomputing.checkins.services.skills.combineskills;

import com.objectcomputing.checkins.services.member_skill.MemberSkill;
import com.objectcomputing.checkins.services.member_skill.MemberSkillServices;
import com.objectcomputing.checkins.services.role.RoleType;
import com.objectcomputing.checkins.services.skills.Skill;
import com.objectcomputing.checkins.services.skills.SkillServices;
import com.objectcomputing.checkins.services.validate.PermissionsValidation;
import io.micronaut.security.utils.SecurityService;

import javax.inject.Singleton;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Set;
import java.util.UUID;

@Singleton
public class CombineSkillServicesImpl implements CombineSkillServices {

    private final SkillServices skillServices;
    private final MemberSkillServices memberSkillServices;
    private final SecurityService securityService;
    private final PermissionsValidation permissionsValidation;

    public CombineSkillServicesImpl(SkillServices skillServices, MemberSkillServices memberSkillServices, SecurityService securityService, PermissionsValidation permissionsValidation) {
        this.skillServices = skillServices;
        this.memberSkillServices = memberSkillServices;
        this.securityService = securityService;
        this.permissionsValidation = permissionsValidation;
    }

    public Skill save(@NotNull @Valid CombineSkillsDTO skillDTO) {

        Boolean isAdmin = securityService != null && securityService.hasRole(RoleType.Constants.ADMIN_ROLE);
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
