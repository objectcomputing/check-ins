package com.objectcomputing.checkins.services.skills.combineskills;

import com.objectcomputing.checkins.services.member_skill.MemberSkill;
import com.objectcomputing.checkins.services.member_skill.MemberSkillServices;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.memberprofile.currentuser.CurrentUserServices;
import com.objectcomputing.checkins.services.role.RoleType;
import com.objectcomputing.checkins.services.skills.Skill;
import com.objectcomputing.checkins.services.skills.SkillServices;
import com.objectcomputing.checkins.services.validate.PermissionsValidation;
import io.micronaut.security.utils.SecurityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import javax.validation.constraints.NotNull;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

@Singleton
public class CombineSkillServicesImpl implements CombineSkillServices {

    private final SkillServices skillServices;
    private final MemberSkillServices memberSkillServices;
    private final SecurityService securityService;
    private final CurrentUserServices currentUserServices;
    private final PermissionsValidation permissionsValidation;

    public CombineSkillServicesImpl(SkillServices skillServices, MemberSkillServices memberSkillServices, SecurityService securityService, CurrentUserServices currentUserServices, PermissionsValidation permissionsValidation) {
        this.skillServices = skillServices;
        this.memberSkillServices = memberSkillServices;
        this.securityService = securityService;
        this.currentUserServices = currentUserServices;
        this.permissionsValidation = permissionsValidation;
    }

    public Skill save(@NotNull CombineSkillsDTO skillDTO) {

        Boolean isAdmin = securityService != null && securityService.hasRole(RoleType.Constants.ADMIN_ROLE);
        Skill returnSkill = null;

        permissionsValidation.validatePermissions(!isAdmin, "User is unauthorized to do this operation");

        Skill newSkill = new Skill(skillDTO.getName(), skillDTO.getDescription());
        UUID[] skillsArray = skillDTO.getSkillsToCombine();

        returnSkill = skillServices.save(newSkill);

        for (UUID skillToCombine : skillsArray) {

            Set<MemberSkill> memberSkills = memberSkillServices.findByFields(null, skillToCombine);
            Stream<MemberSkill> stream = memberSkills.stream();
            UUID newSkillId = returnSkill.getId();

            memberSkills.forEach(memberSkill -> {
                memberSkillServices.delete(memberSkill.getId());

                memberSkill.setSkillid(newSkillId);
                memberSkill.setId(null);
                memberSkillServices.save(memberSkill);
            });


//            for (MemberSkill memberSkill : memberSkills) {
//                memberSkillServices.delete(memberSkill.getId());
//
//                memberSkill.setSkillid(returnSkill.getId());
//                memberSkill.setId(null);
//                memberSkillServices.save(memberSkill);
//            }

            skillServices.delete(skillToCombine);

        }

        return returnSkill;

    }

}
