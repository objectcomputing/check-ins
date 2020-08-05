package com.objectcomputing.checkins.services.memberSkills;

import javax.inject.Inject;
import java.util.List;
import java.util.UUID;

public class MemberSkillServiceImpl implements MemberSkillsServices {

    @Inject
    private MemberSkillRepository memberSkillRepository;

    public MemberSkill save(MemberSkill memberSkill) {

        List<MemberSkill> returnedList = findByFields(memberSkill.getMemberid(), memberSkill.getSkillid());
        return returnedList.size() < 1 ? memberSkillRepository.save(memberSkill) : null;

    }

    public List<MemberSkill> findByFields(UUID memberid, UUID skillid) {
        List<MemberSkill> memberSkillList = null;

        if(memberid != null) {
            memberSkillList = findByMemberid(memberid);
        }  else if(skillid != null) {
            memberSkillList = findBySkillid(skillid);
        }

        return memberSkillList;
    }

    private List<MemberSkill> findByMemberid(UUID memberid) {
        List<MemberSkill> memberSkillList = memberSkillRepository.findByMemberid(memberid);  //boom

        return memberSkillList;
    }

    private List<MemberSkill> findBySkillid(UUID skillid) {
        List<MemberSkill> skillList = memberSkillRepository.findBySkillid(skillid);

        return skillList;

    }

    @Override
    public MemberSkill read(UUID uuid) {
        return null;
    }

    @Override
    public MemberSkill update(MemberSkill m) {
        return null;
    }
}
