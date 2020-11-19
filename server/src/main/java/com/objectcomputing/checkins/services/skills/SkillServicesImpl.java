package com.objectcomputing.checkins.services.skills;

import com.objectcomputing.checkins.services.skills.tags.*;

import javax.inject.Singleton;
import javax.validation.constraints.NotNull;
import java.util.*;

@Singleton
public class SkillServicesImpl implements SkillServices {

    private final SkillRepository skillRepository;
    private final SkillSkillTagLookupRepository skillTagLookupRepository;
    private final SkillTagRepository skillTagRepository;

    public SkillServicesImpl(SkillRepository skillRepository,
                             SkillSkillTagLookupRepository skillTagLookupRepository,
                             SkillTagRepository skillTagRepository) {
        this.skillRepository = skillRepository;
        this.skillTagLookupRepository = skillTagLookupRepository;
        this.skillTagRepository = skillTagRepository;
    }

    public Skill save(Skill skill) {
        Skill newSkill = null;
        if (skill != null) {

            if (skill.getId() != null) {
                throw new SkillBadArgException(String.format("Found unexpected id %s for skill, please try updating instead.",
                        skill.getId()));
            } else if (skillRepository.findByName(skill.getName()).isPresent()&&!skill.isPending()) {
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

    @Override
    public Skill tagSkill(UUID skillId, UUID tagId) {
        Skill tagMe = skillRepository.findById(skillId)
                .orElseThrow(() -> {
                    throw new SkillNotFoundException(String.format("Skill with id %s does not exist", skillId));
                });
        if (tagMe.getTags() == null) {
            tagMe.setTags(new ArrayList<>());
        }
        SkillTag tag = skillTagRepository.findById(tagId).orElseThrow(() -> {
            throw new SkillTagNotFoundException(String.format("Tag with id %s does not exist", tagId));
        });
        tagMe.getTags().add(tag);
        skillTagLookupRepository.save(new SkillSkillTagLookup(skillId, tagId));
        return skillRepository.findById(skillId).get();
    }

    @Override
    public Skill untagSkill(UUID skillId, UUID tagId) {
        skillRepository.findById(skillId).orElseThrow(() -> {
            throw new SkillNotFoundException(String.format("Skill with id %s does not exist", skillId));
        });
        skillTagRepository.findById(tagId).orElseThrow(() -> {
            throw new SkillTagNotFoundException(String.format("Tag with id %s does not exist", tagId));
        });
        skillTagLookupRepository.deleteByTagId(tagId);
        return skillRepository.findById(skillId).get();
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
