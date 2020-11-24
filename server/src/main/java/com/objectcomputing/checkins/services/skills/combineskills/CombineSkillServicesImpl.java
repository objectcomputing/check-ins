package com.objectcomputing.checkins.services.skills.combineskills;

import com.objectcomputing.checkins.services.member_skill.MemberSkill;
import com.objectcomputing.checkins.services.member_skill.MemberSkillServices;
import com.objectcomputing.checkins.services.skills.*;

import javax.inject.Singleton;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Singleton
//public class CombineSkillServicesImpl  {
public class CombineSkillServicesImpl implements CombineSkillServices {

    private final SkillServices skillServices;
    private final MemberSkillServices memberSkillServices;

    public CombineSkillServicesImpl(SkillServices skillServices, MemberSkillServices memberSkillServices) {
        this.skillServices = skillServices;
        this.memberSkillServices = memberSkillServices;
    }

//    create new skill with name and description
//    for each provided skill id:
//    reassign member skills to new skill
//      Find all members with this skill
//      For each member found
//      Create new skill for member
//      Remove old skill
//      delete skill
//    return value should be new skill entity

    public Skill save(CombineSkillsDTO skillDTO) {
        Skill newSkill = null;
        Skill returnSkill = null;
        UUID[] skillsArray = skillDTO.getSkillsToCombine();
        if (skillDTO != null) {
//    create new skill with name and description
     // and save skill o that we have the id
            newSkill.setName(skillDTO.getName());
            newSkill.setDescription(skillDTO.getDescription());
            returnSkill = skillServices.save(newSkill);

//            for each provided skill id:
//            loop

            for( UUID skillToCombine: skillsArray) {
//                memberSkillServices.findByFields()

//            Find all members with this old skill - findByFields - null,skillid
//            reassign member skills to new skill
//            For each member found
//            Create new skill for member
//            Remove old skill


            }

//            delete old skill

//            if (skillDTO.getId() != null) {
//                throw new SkillBadArgException(String.format("Found unexpected id %s for skill, please try updating instead.",
//                        skillDTO.getId()));
//            } else if (skillRepository.findByName(skillDTO.getName()).isPresent()&&!skillDTO.isPending()) {
//                throw new SkillAlreadyExistsException(String.format("Skill %s already exists. ",  skillDTO.getName()));
//            }

        }
        return returnSkill;

    }
//
//    public Skill readSkill(@NotNull UUID id) {
//
//        Skill returned = skillRepository.findById(id).orElse(null);
//
//        return returned;
//
//    }
//
//    public Set<Skill> findByValue(String name, Boolean pending) {
//        Set<Skill> skillList = new HashSet<>();
//        skillRepository.findAll().forEach(skillList::add);
//
//        if (name != null) {
//            skillList.retainAll(findByNameLike(name));
//        }
//        if (pending != null) {
//            skillList.retainAll(skillRepository.findByPending(pending));
//        }
//
//        return skillList;
//    }
//
//    public void delete(@NotNull UUID id) {
//        skillRepository.deleteById(id);
//    }
//
//    protected List<Skill> findByNameLike(String name) {
//        String wildcard = "%" + name + "%";
//        List<Skill> skillList = skillRepository.findByNameIlike(wildcard);
//
//        return skillList;
//
//    }

}
