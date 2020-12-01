package com.objectcomputing.checkins.services.skills;

import javax.inject.Singleton;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Singleton
public class SkillServicesImpl implements SkillServices {

    private final SkillRepository skillRepository;

    public SkillServicesImpl(SkillRepository skillRepository) {
        this.skillRepository = skillRepository;
    }

    public Skill save(Skill skill) {
        Skill newSkill = null;
        if (skill != null) {

            if (skill.getId() != null) {
                throw new SkillBadArgException(String.format("Found unexpected id %s for skill, please try updating instead.",
                        skill.getId()));
            } else if (skillRepository.findByName(skill.getName()).isPresent()) {
                throw new SkillAlreadyExistsException(String.format("Skill %s already exists. ",  skill.getName()));
            }

            newSkill = skillRepository.save(skill);
        }
        return newSkill;

    }

    public Skill readSkill(@NotNull UUID id) {

        Skill returned = skillRepository.findById(id).orElse(null);

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

    public Skill update(Skill skill) {

        Skill newSkill = null;

        if (skill != null) {
            if (skill.getId() != null && skillRepository.findById(skill.getId()).isPresent()) {
                newSkill = skillRepository.update(skill);
            } else {
                throw new SkillBadArgException(String.format("Skill %s does not exist, cannot update", skill.getId()));
            }
        }

        return newSkill;

    }

}
