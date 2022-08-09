package com.objectcomputing.checkins.services.member_skill;

import com.objectcomputing.checkins.exceptions.AlreadyExistsException;
import com.objectcomputing.checkins.exceptions.BadArgException;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileRetrievalServices;
import com.objectcomputing.checkins.services.skills.SkillRepository;

import jakarta.inject.Singleton;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static com.objectcomputing.checkins.util.Validation.validate;

@Singleton
public class MemberSkillServiceImpl implements MemberSkillServices {

    private final MemberSkillRepository memberSkillRepository;
    private final MemberProfileRetrievalServices memberProfileRetrievalServices;
    private final SkillRepository skillRepository;

    public MemberSkillServiceImpl(MemberSkillRepository memberSkillRepository,
                                  MemberProfileRetrievalServices memberProfileRetrievalServices,
                                  SkillRepository skillRepository) {
        this.memberSkillRepository = memberSkillRepository;
        this.memberProfileRetrievalServices = memberProfileRetrievalServices;
        this.skillRepository = skillRepository;
    }

    public MemberSkill save(MemberSkill memberSkill) {
        MemberSkill memberSkillRet = null;
        if (memberSkill != null) {
            final UUID memberId = memberSkill.getMemberid();
            final UUID skillId = memberSkill.getSkillid();

            validate(skillId != null && memberId != null).orElseThrow(() -> {
                throw new BadArgException("Invalid member skill %s", memberSkill);
            });
            validate(memberSkill.getId() == null).orElseThrow(() -> {
                throw new BadArgException("Found unexpected id %s for member skill", memberSkill.getId());
            });
            memberProfileRetrievalServices.getById(memberId).orElseThrow(() -> {
                throw new BadArgException("Member Profile %s doesn't exist", memberId);
            });
            validate(skillRepository.findById(skillId).isPresent()).orElseThrow(() -> {
                throw new BadArgException("Skill %s doesn't exist", skillId);
            });
            validate(memberSkillRepository.findByMemberidAndSkillid(memberSkill.getMemberid(), memberSkill.getSkillid()).isEmpty()).orElseThrow(() -> {
                throw new AlreadyExistsException("Member %s already has this skill %s", memberId, skillId);
            });

            memberSkillRet = memberSkillRepository.save(memberSkill);
        }
        return memberSkillRet;

    }

    public MemberSkill read(@NotNull UUID id) {
        return memberSkillRepository.findById(id).orElse(null);
    }

    public Set<MemberSkill> findByFields(UUID memberid, UUID skillid) {
        Set<MemberSkill> memberSkills = new HashSet<>();
        memberSkillRepository.findAll().forEach(memberSkills::add);

        if (memberid != null) {
            memberSkills.retainAll(memberSkillRepository.findByMemberid(memberid));
        }
        if (skillid != null) {
            memberSkills.retainAll(memberSkillRepository.findBySkillid(skillid));
        }

        return memberSkills;
    }

    public MemberSkill update(@NotNull MemberSkill memberSkill) {

        MemberSkill newSkill;

        validate(memberSkill.getId() != null && memberSkillRepository.findById(memberSkill.getId()).isPresent()).orElseThrow(() -> {
            throw new BadArgException("MemberSkill %s does not exist, cannot update", memberSkill.getId());
        });

        newSkill = memberSkillRepository.update(memberSkill);

        return newSkill;
    }

    public void delete(@NotNull UUID id) {
        memberSkillRepository.deleteById(id);
    }

}
