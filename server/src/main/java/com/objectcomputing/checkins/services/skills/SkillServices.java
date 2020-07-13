package com.objectcomputing.checkins.services.skills;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

public class SkillServices {

    private static final Logger LOG = LoggerFactory.getLogger(SkillServices.class);

    @Inject
    private SkillRepository skillsRepo;

    protected Skill saveSkill(Skill skill) {

        List<Skill> returnedList = findByValue(null, skill.getName(), null);
        return returnedList.size() < 1 ? skillsRepo.save(skill) : null;

    }

    protected Skill readSkill(UUID skillId) {

        Skill returned = skillsRepo.findBySkillid(skillId);

        return returned;

    }

    protected List<Skill> findByValue(UUID skillid, String name, Boolean pending) {
        List<Skill> skillList = null;
        if (skillid != null) {
           skillList = Collections.singletonList(readSkill(skillid));
        } else if(name != null) {
            skillList = findByNameLike(name);
        }  else if(pending != null) {
            skillList = findByPending(pending);
        }

        return skillList;
    }

    private List<Skill> findByNameLike(String name) {
        List<Skill> skillList = skillsRepo.findByNameLike(name);

        return skillList;
    }

    protected List<Skill> findByPending(boolean pending) {
        List<Skill> skillList = skillsRepo.findByPending(pending);

        return skillList;
    }

    protected Skill updatePending(Skill skill) {

        Skill returned = skillsRepo.update(skill);

        return returned;

    }

    protected void loadSkills(Skill[] skillslist)
    {

        Stream<Skill> stream = Stream.of(skillslist);

            stream.forEach(s-> saveSkill(s));

    }
}
