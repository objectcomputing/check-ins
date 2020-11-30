package com.objectcomputing.checkins.services.skills.combineskills;

import com.objectcomputing.checkins.services.member_skill.MemberSkill;
import com.objectcomputing.checkins.services.member_skill.MemberSkillServices;
import com.objectcomputing.checkins.services.skills.Skill;
import com.objectcomputing.checkins.services.skills.SkillServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import javax.validation.constraints.NotNull;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

@Singleton
public class CombineSkillServicesImpl implements CombineSkillServices {

    private static final Logger LOG = LoggerFactory.getLogger(CombineSkillServicesImpl.class);
    private final SkillServices skillServices;
    private final MemberSkillServices memberSkillServices;

    public CombineSkillServicesImpl(SkillServices skillServices, MemberSkillServices memberSkillServices) {
        this.skillServices = skillServices;
        this.memberSkillServices = memberSkillServices;
    }

    public Skill save(@NotNull CombineSkillsDTO skillDTO) {
        Skill newSkill = new Skill(skillDTO.getName(), skillDTO.getDescription());
        Skill returnSkill = new Skill();
        UUID[] skillsArray = skillDTO.getSkillsToCombine();

        returnSkill = skillServices.save(newSkill);

        for (UUID skillToCombine : skillsArray) {

            Set<MemberSkill> memberSkills = memberSkillServices.findByFields(null, skillToCombine);
            Stream<MemberSkill> stream = memberSkills.stream();
            UUID newSkillId = returnSkill.getId();

                memberSkills.forEach(memskill->{
                    LOG.error("memberSkills.forEach(mskill-> " + memskill.getId() + " ");
                    LOG.error("memberSkills.forEach(mskill-> " + memskill.getMemberid() + " ");
                    memberSkillServices.delete(memskill.getId());

                    memskill.setSkillid(newSkillId);
                    memskill.setId(null);
                    memberSkillServices.save(memskill);
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


//            if (skillDTO.getId() != null) {
//                throw new SkillBadArgException(String.format("Found unexpected id %s for skill, please try updating instead.",
//                        skillDTO.getId()));
//            } else if (skillRepository.findByName(skillDTO.getName()).isPresent()&&!skillDTO.isPending()) {
//                throw new SkillAlreadyExistsException(String.format("Skill %s already exists. ",  skillDTO.getName()));
//            }

        return returnSkill;

    }

}
