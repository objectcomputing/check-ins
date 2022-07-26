package com.objectcomputing.checkins.services.skills;

import com.objectcomputing.checkins.exceptions.AlreadyExistsException;
import com.objectcomputing.checkins.exceptions.BadArgException;
import com.objectcomputing.checkins.exceptions.PermissionException;
import com.objectcomputing.checkins.services.memberprofile.currentuser.CurrentUserServices;

import jakarta.inject.Singleton;
import javax.validation.constraints.NotNull;
import java.util.*;

import static com.objectcomputing.checkins.services.validate.Validation.validate;

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

            validate(skill.getId() == null).orElseThrow(() -> {
                throw new BadArgException("Found unexpected id %s for skill, please try updating instead.", skill.getId());
            });
            validate(skillRepository.findByName(skill.getName()).isEmpty()).orElseThrow(() -> {
                throw new AlreadyExistsException("Skill %s already exists. ", skill.getName());
            });

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
        validate(currentUserServices.isAdmin()).orElseThrow(() -> {
            throw new PermissionException("You do not have permission to access this resource");
        });

        skillRepository.deleteById(id);
    }

    protected List<Skill> findByNameLike(String name) {
        String wildcard = "%" + name + "%";
        return skillRepository.findByNameIlike(wildcard);
    }

    public Skill update(@NotNull Skill skill) {
        validate(currentUserServices.isAdmin()).orElseThrow(() -> {
            throw new PermissionException("You do not have permission to access this resource");
        });
        validate(skill.getId() != null && skillRepository.findById(skill.getId()).isPresent()).orElseThrow(() -> {
            throw new BadArgException("Skill %s does not exist, cannot update", skill.getId());
        });

        return skillRepository.update(skill);
    }

}
