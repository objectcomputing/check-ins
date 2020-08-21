package com.objectcomputing.checkins.services.skills;

import com.objectcomputing.checkins.services.memberSkill.MemberSkillAlreadyExistsException;
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

    private static final Logger LOG = LoggerFactory.getLogger(SkillServicesImpl.class);

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
                throw new MemberSkillAlreadyExistsException(String.format("Member %s already has this skill %s", name));
            }

            newSkill = skillRepository.save(skill);
        }
        return newSkill;

    }

    protected Skill readSkill(@NotNull UUID skillId) {

        Skill returned = skillRepository.findBySkillid(skillId).orElse(null);

        return returned;

    }

    public Set<Skill> readAll() {
        Set<Skill> skills = new HashSet<>();
        skillRepository.findAll().forEach(skills::add);
        return skills;
    }

    @Override
    public Skill read(UUID uuid) {
        return null;
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

    @Override
    public void delete(UUID id) {

    }

    protected List<Skill> findByNameLike(String name) {
        String wildcard = "%" + name + "%";
        List<Skill> skillList = skillRepository.findByNameIlike(wildcard);

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

}
