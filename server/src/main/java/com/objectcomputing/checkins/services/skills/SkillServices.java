package com.objectcomputing.checkins.services.skills;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public class SkillServices {

    private static final Logger LOG = LoggerFactory.getLogger(SkillServices.class);

    @Inject
    private SkillRepository skillsRepo;

    Skill saveSkill(Skill skill) {

        LOG.info("Storing skill.");
        Skill returned = skillsRepo.save(skill);
        LOG.info("skill stored.");

        return returned;
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
