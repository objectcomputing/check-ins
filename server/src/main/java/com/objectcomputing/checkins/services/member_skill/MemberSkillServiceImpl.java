package com.objectcomputing.checkins.services.member_skill;

import com.objectcomputing.checkins.services.memberprofile.MemberProfileRepository;
import com.objectcomputing.checkins.services.skills.SkillRepository;

import javax.inject.Singleton;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Singleton
public class MemberSkillServiceImpl implements MemberSkillServices {

    private final MemberSkillRepository memberSkillRepository;
    private final MemberProfileRepository memberProfileRepository;
    private final SkillRepository skillRepository;

    public MemberSkillServiceImpl(MemberSkillRepository memberSkillRepository,
                                  MemberProfileRepository memberProfileRepository,
                                  SkillRepository skillRepository) {
        this.memberSkillRepository = memberSkillRepository;
        this.memberProfileRepository = memberProfileRepository;
        this.skillRepository = skillRepository;
    }

    public MemberSkill save(MemberSkill memberSkill) {
        MemberSkill memberSkillRet = null;
        if (memberSkill != null) {
            final UUID memberId = memberSkill.getMemberid();
            final UUID skillId = memberSkill.getSkillid();
            if (skillId == null || memberId == null) {
                throw new MemberSkillBadArgException(String.format("Invalid member skill %s", memberSkill));
            } else if (memberSkill.getId() != null) {
                throw new MemberSkillBadArgException(String.format("Found unexpected id %s for member skill", memberSkill.getId()));
            } else if (memberProfileRepository.findById(memberId).isEmpty()) {
                throw new MemberSkillBadArgException(String.format("Member Profile %s doesn't exist", memberId));
            } else if (skillRepository.findById(skillId).isEmpty()) {
                throw new MemberSkillBadArgException(String.format("Skill %s doesn't exist", skillId));
            } else if (memberSkillRepository.findByMemberidAndSkillid(memberSkill.getMemberid(),
                    memberSkill.getSkillid()).isPresent()) {
                throw new MemberSkillAlreadyExistsException(String.format("Member %s already has this skill %s", memberId, skillId));
            }

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

    public void delete(@NotNull UUID id) {
        memberSkillRepository.deleteById(id);
    }

}
