package com.objectcomputing.checkins.services.skills;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

@Singleton
public class SkillServices {

    private static final Logger LOG = LoggerFactory.getLogger(SkillServices.class);

    @Inject
    private SkillRepository skillRepository;

    public void setSkillRepository(SkillRepository skillsRepository) {
        this.skillRepository = skillsRepository;
    }

    protected Skill saveSkill(Skill skill) {

        List<Skill> returnedList = findByValue(skill.getName(), null);
        return returnedList.size() < 1 ? skillRepository.save(skill) : null;

    }

    protected Skill readSkill(UUID skillId) {

        Skill returned = skillRepository.findBySkillid(skillId);

        return returned;

    }

    public Set<Skill> readAll() {
        Set<Skill> actionItems = new HashSet<>();
        skillRepository.findAll().forEach(actionItems::add);
        return actionItems;
    }

    protected List<Skill> findByValue(String name, Boolean pending) {
        List<Skill> skillList = null;

        if(name != null) {
            skillList = findByNameLike(name);
        }  else if(pending != null) {
            skillList = findByPending(pending);
        }

        return skillList;
    }

    protected List<Skill> findByNameLike(String name) {
        String wildcard = "%" + name + "%" ;
        List<Skill> skillList = skillRepository.findByNameIlike(wildcard);

        return skillList;

    }

    protected List<Skill> findByPending(boolean pending) {
        List<Skill> skillList = skillRepository.findByPending(pending);

        return skillList;

    }

    protected Skill update(Skill skill) {

        Skill returned = null;
        Skill skillInDatabase = readSkill(skill.getSkillid());
        if ((skillInDatabase != null)
                && skillInDatabase.getName().equals(skill.getName())) {
            returned = skillRepository.update(skill);
        }

        return returned;

    }

    protected void loadSkills(Skill[] skillslist)
    {

        Stream<Skill> stream = Stream.of(skillslist);

        stream.forEach(s-> saveSkill(s));

    }

}
