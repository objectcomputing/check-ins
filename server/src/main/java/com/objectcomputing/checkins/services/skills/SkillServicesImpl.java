package com.objectcomputing.checkins.services.skills;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Singleton
public class SkillServicesImpl implements SkillServices {

    @Inject
    private SkillRepository skillRepository;

    public void setSkillRepository(SkillRepository skillsRepository) {
        this.skillRepository = skillsRepository;
    }

    public Skill save(Skill skill) {
        Skill newSkill = null;
        if (skill != null) {
            final String name = skill.getName();

            if (skill.getSkillid() != null) {
                throw new SkillBadArgException(String.format("Found unexpected id %s for skill, please try updating instead.",
                        skill.getSkillid()));
            } else if (skillRepository.findByName(skill.getName()).isPresent()) {
                throw new SkillAlreadyExistsException(String.format("Member %s already has this skill %s", skill.getSkillid(), name));
            }

            newSkill = skillRepository.save(skill);
        }
        return newSkill;

    }

    protected Skill readSkill(@NotNull UUID skillId) {

        Skill returned = skillRepository.findBySkillid(skillId).orElse(null);

        return returned;

    }

    public Set<Skill> findByValue(String name, Boolean pending) {
        Set<Skill> skillList = new HashSet<>();
        skillRepository.findAll().forEach(skillList::add);

        if (name != null) {
            skillList.retainAll(findByNameLike(name));
        }
        if (pending != null) {
            skillList.retainAll(skillRepository.findByPending(pending));
        }

        return skillList;
    }

    public void delete(@NotNull UUID id) {
        skillRepository.deleteById(id);
    }

    protected List<Skill> findByNameLike(String name) {
        String wildcard = "%" + name + "%";
        List<Skill> skillList = skillRepository.findByNameIlike(wildcard);

        return skillList;

    }

    protected Skill update(Skill skill) {

        Skill newSkill = null;

        if (skill != null) {
            if (skill.getSkillid() != null && skillRepository.findBySkillid(skill.getSkillid()).isPresent()) {
                newSkill = skillRepository.update(skill);
            } else {
                throw new SkillBadArgException(String.format("Skill %s does not exist, cannot update", skill.getSkillid()));
            }
        }

        return newSkill;

    }

}
