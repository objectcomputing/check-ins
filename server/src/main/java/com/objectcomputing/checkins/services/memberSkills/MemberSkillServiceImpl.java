package com.objectcomputing.checkins.services.memberSkills;

import com.objectcomputing.checkins.services.memberprofile.MemberProfileRepository;
import com.objectcomputing.checkins.services.skills.SkillRepository;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class MemberSkillServiceImpl implements MemberSkillsServices {

    @Inject
    private MemberSkillRepository memberSkillRepository;

    @Inject
    private MemberProfileRepository memberProfileRepository;

    @Inject
    private SkillRepository skillRepository;

    public MemberSkill save(MemberSkill memberSkill) {
        MemberSkill memberSkillRet = null;
        if (memberSkill != null) {
            final UUID memberId = memberSkill.getMemberid();
            final UUID skillId = memberSkill.getSkillid();
            if (skillId == null || memberId == null) {
                throw new MemberSkillsBadArgException(String.format("Invalid member skill %s", memberSkill));
            } else if (memberSkill.getId() != null) {
                throw new MemberSkillsBadArgException(String.format("Found unexpected id %s for member skill", memberSkill.getId()));
            } else if (!memberProfileRepository.findById(memberId).isPresent()) {
                throw new MemberSkillsBadArgException(String.format("Member Profile %s doesn't exist", memberId));
            } else if (!skillRepository.findById(skillId).isPresent()) {
                throw new MemberSkillsBadArgException(String.format("Skill %s doesn't exist", skillId));
            } else if (memberSkillRepository.findByMemberidAndSkillid(memberSkill.getMemberid(),
                    memberSkill.getSkillid()).isPresent()) {
                throw new MemberSkillsBadArgException(String.format("Member %s already has this skill %s", memberId, skillId));
            }

            memberSkillRet = memberSkillRepository.save(memberSkill);
        }
        return memberSkillRet;

    }

    public Set<MemberSkill> readAll() {
        Set<MemberSkill> memberSkillList = new HashSet<>();
        memberSkillRepository.findAll().forEach(memberSkillList::add);
        return memberSkillList;
    }

    public MemberSkill read(UUID id) {
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

    private List<MemberSkill> findByMemberid(UUID memberid) {
        List<MemberSkill> memberSkillList = memberSkillRepository.findByMemberid(memberid);

        return memberSkillList;
    }

    private List<MemberSkill> findBySkillid(UUID skillid) {
        List<MemberSkill> skillList = memberSkillRepository.findBySkillid(skillid);

        return skillList;

    }

    public void delete(@NotNull UUID id) {
        memberSkillRepository.deleteById(id);
    }

    @Override
    public MemberSkill update(MemberSkill m) {
        return null;
    }
}
