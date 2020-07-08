package com.objectcomputing.checkins.services.skills;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class SkillServices {

    private static final Logger LOG = LoggerFactory.getLogger(SkillServices.class);

    @Inject
    private SkillRepository skillsRepo;

    protected Skill saveSkill(Skill skill) {

        LOG.info("Storing skill. {}" , skill);

        List<Skill> returnedList = findByValue(null, skill.getName(), null);
        return returnedList.size() < 1 ? skillsRepo.save(skill) : null;

    }

    protected Skill readSkill(UUID skillId) {

        LOG.info("Reading skill by id.");
        Skill returned = skillsRepo.findBySkillid(skillId);

        LOG.info("skill found. {}", returned);

        return returned;

    }

    protected List<Skill> findByValue(UUID skillid, String name, Boolean pending) {
        LOG.info("finding values." +skillid + " " + name + " " + pending);
        List<Skill> skillList = null;
        if (skillid != null) {
           skillList = Collections.singletonList(readSkill(skillid));
        } else if(name != null) {
            skillList = findByNameLike(name);
        }  else if(pending != null) {
            skillList = findByPending(pending);
        }

        LOG.info("skills found: {}",  skillList);

        return skillList;
    }

    private List<Skill> findByNameLike(String name) {
        LOG.info("finding by name like.");
        List<Skill> skillList = skillsRepo.findByNameLike(name);
        LOG.info("skills found: {}", skillList);

        return skillList;
    }

    protected List<Skill> findByPending(boolean pending) {
        LOG.info("finding by pending.");
        List<Skill> skillList = skillsRepo.findByPending(pending);
        LOG.info("skills found: {}" ,skillList);

        return skillList;
    }

}
