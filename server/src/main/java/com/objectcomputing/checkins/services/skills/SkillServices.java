package com.objectcomputing.checkins.services.skills;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class SkillServices {

    private static final Logger LOG = LoggerFactory.getLogger(SkillServices.class);

    @Inject
    private SkillRepository skillsRepo;

    Skill saveSkill(Skill skill) {

        LOG.info("Storing skill." + skill);
        Skill returned = skillsRepo.save(skill);
        LOG.info("skill stored. returned = " + returned);

        return returned;
    }

    Skill readSkill(UUID skillId) {

        LOG.info("Reading skill by id.");
        Skill returned = skillsRepo.findBySkillid(skillId);

        LOG.info("skill found. " + returned);

        return returned;

    }

    List<Skill> findByValue(UUID skillid, String name, Boolean pending) {
        LOG.info("finding values." +skillid + " " + name + " " + pending);
        List<Skill> skillList = null;
        if (skillid != null) {
           skillList = Collections.singletonList(readSkill(skillid));
        } else if(name != null) {
            skillList = findByName(name);
        }  else if(pending != null) {
            skillList = findByPending(pending);
        }

        LOG.info("skills found: " + skillList);

        return skillList;
    }

    List<Skill> findByName(String name) {
        LOG.info("finding by name.");
        List<Skill> skillList = skillsRepo.findByName(name);
        LOG.info("skills found: " + skillList);

        return skillList;
    }

    List<Skill> findByPending(boolean pending) {
        LOG.info("finding by pending.");
        List<Skill> skillList = skillsRepo.findByPending(pending);
        LOG.info("skills found: " + skillList);

        return skillList;
    }

//
//    List<Skill> saveSkills(int howManySkills) {
//
//        List<Skill> skills = new ArrayList<Skill>();
//
//        for (int i = 0; i < howManySkills; i++) {
//            skills.add(new Skill());
//        }
//        LOG.info("Storing empty skills.");
//        List<Skill> returned = skillsRepo.saveAll(skills);
//        LOG.info("skills stored.");
//
//        return returned;
//    }

}
