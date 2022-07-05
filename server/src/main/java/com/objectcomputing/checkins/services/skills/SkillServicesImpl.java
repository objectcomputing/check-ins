package com.objectcomputing.checkins.services.skills;

import com.objectcomputing.checkins.exceptions.AlreadyExistsException;
import com.objectcomputing.checkins.exceptions.BadArgException;
import com.objectcomputing.checkins.exceptions.PermissionException;
import com.objectcomputing.checkins.services.memberprofile.currentuser.CurrentUserServices;

import jakarta.inject.Singleton;
import javax.validation.constraints.NotNull;
import java.util.*;

@Singleton
public class SkillServicesImpl implements SkillServices {

    private final SkillRepository skillRepository;
    private final CurrentUserServices currentUserServices;

    public SkillServicesImpl(SkillRepository skillRepository,
                             CurrentUserServices currentUserServices) {
        this.skillRepository = skillRepository;
        this.currentUserServices = currentUserServices;
    }

    public Skill save(Skill skill) {
        Skill newSkill = null;
        if (skill != null) {

            if (skill.getId() != null) {
                throw new BadArgException("Found unexpected id %s for skill, please try updating instead.",
                        skill.getId());
            } else if (skillRepository.findByName(skill.getName()).isPresent()) {
                throw new AlreadyExistsException("Skill %s already exists. ", skill.getName());
            }

            newSkill = skillRepository.save(skill);
        }

        return newSkill;
    }

    public Skill readSkill(@NotNull UUID id) {
        return skillRepository.findById(id).orElse(null);
    }

    public Set<Skill> findByValue(String name, Boolean pending) {
        Set<Skill> skillList = new HashSet<>();

        if (name != null) {
            skillList.addAll(findByNameLike(name));
            if (pending != null) {
                skillList.retainAll(skillRepository.findByPending(pending));
            }
        } else if (pending != null) {
            skillList.addAll(skillRepository.findByPending(pending));
        } else {
            skillRepository.findAll().forEach(skillList::add);
        }

        return skillList;
    }

    public void delete(@NotNull UUID id) {
        if (!currentUserServices.isAdmin()) {
            throw new PermissionException("You do not have permission to access this resource");
        }
        skillRepository.deleteById(id);
    }

    protected List<Skill> findByNameLike(String name) {
        String wildcard = "%" + name + "%";
        return skillRepository.findByNameIlike(wildcard);
    }

    public Skill update(@NotNull Skill skill) {
        if (!currentUserServices.isAdmin()) {
            throw new PermissionException("You do not have permission to access this resource");
        }

        if (skill.getId() != null && skillRepository.findById(skill.getId()).isPresent()) {
            return skillRepository.update(skill);
        } else {
            throw new BadArgException("Skill %s does not exist, cannot update", skill.getId());
        }
    }

}
