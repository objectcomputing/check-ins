package com.objectcomputing.checkins.services.member_skill.skillsreport;

import com.objectcomputing.checkins.services.member_skill.MemberSkillRepository;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileRepository;
import com.objectcomputing.checkins.services.member_skill.MemberSkill;
import com.objectcomputing.checkins.exceptions.BadArgException;

import javax.inject.Singleton;
import java.util.List;
import java.util.Set;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

@Singleton
public class SkillsReportServicesImpl implements SkillsReportServices {
    private final MemberSkillRepository memberSkillRepo;
    private final MemberProfileRepository memberProfileRepo;

    public SkillsReportServicesImpl(MemberSkillRepository memberSkillRepo,
                                    MemberProfileRepository memberProfileRepo) {
        this.memberSkillRepo = memberSkillRepo;
        this.memberProfileRepo = memberProfileRepo;
    }

    public SkillsReportResponseDTO report(SkillsReportRequestDTO request) {
        SkillsReportResponseDTO response = null;
        if (request != null) {
            final List<SkillLevelDTO> skills = request.getSkills();
            if (skills == null) {
                throw new BadArgException(String.format("Invalid list of requested skills"));
            }

            final Set<UUID> members = request.getMembers();
            final Boolean inclusive = request.isInclusive();
            response = new SkillsReportResponseDTO();

            List<TeamMemberSkillDTO> potentialMembers = getPotentialQualifyingMembers(skills);
            if (members == null || members.isEmpty()) {
                if (inclusive == null || inclusive == false) {
                    response.setTeamMembers(potentialMembers);
                } else {
                    List<TeamMemberSkillDTO> qualifiedMembers = getMembersSatifyingAllSkills(potentialMembers, skills);
                    response.setTeamMembers(qualifiedMembers);
                }
            } else {
                if (inclusive == null || inclusive == false) {
                    List<TeamMemberSkillDTO> qualifiedMembers = removeMembersNotRequested(potentialMembers, members);
                    response.setTeamMembers(qualifiedMembers);
                } else {
                    List<TeamMemberSkillDTO> membersInList = removeMembersNotRequested(potentialMembers, members);
                    List<TeamMemberSkillDTO> qualifiedMembers = getMembersSatisfyingAllSkills(membersInList, skills);
                    response.setTeamMembers(qualifiedMembers);
                }
            }
        }

        return response;
    }
}

    private List<TeamMemberSkillDTO> getPotentialQualifyingMembers(List<SkillLevelDTO> skills) {
        // Get all members that satisfy a requested skill
        List<MemberSkill> entries = new ArrayList<MemberSkill>();

        for (SkillLevelDTO skill : skills) {
            if (skill.getId() == null) {
                throw new BadArgException(String.format("Invalid requested skill ID"));
            }

            List<MemberSkill> temp = memberSkillRepo.findBySkillid(skill.getId());
            if (skill.getLevel() != null) {
                for (MemberSkill member : temp) {
                    if (isSkillLevelSatisfied(member.getSkilllevel(), skill.getLevel())) {
                        entries.add(member);
                    }
                }
            } else {
                entries.addAll(temp);
            }
        }

        // Collect all skills belong to each team member
        HashMap<UUID, TeamMemberSkillDTO> map = new HashMap<>();
        for (MemberSkill ms : entries) {
            final UUID memberId = ms.getMemberid();
            SkillLevelDTO skill = new SkillLevelDTO();
            skill.setId(ms.getSkillid());
            skill.setLevel(ms.getSkilllevel());

            if (map.containsKey(memberId)) {
                TeamMemberSkillDTO dto = map.get(memberId);
                dto.getSkills().add(skill);
            } else {
                TeamMemberSkillDTO dto = new TeamMemberSkillDTO();
                dto.setId(memberId);

                String memberName = memberProfileRepo.findNameById(memberId);
                dto.setName(memberName);

                List<SkillLevelDTO> memberSkills = new ArrayList<SkillLevelDTO>();
                memberSkills.add(skill);
                dto.setSkills(memberSkills);

                map.put(memberId, dto);
            }
        }

        List<TeamMemberSkillDTO> ret = new ArrayList<TeamMemberSkillDTO>();
        ret.addAll(map.values());
        return ret;
    }

    private List<TeamMemberSkillDTO> getMembersSatisfyingAllSkills(List<TeamMemberSkillDTO> potentialMembers,
                                                                   List<SkillLevelDTO> requestedSkills) {
        List<TeamMemberSkillDTO> ret = new ArrayList<TeamMemberSkillDTO>();
        for (TeamMemberSkillDTO member : potentialMembers) {
            List<SkillLevelDTO> memberSkills = member.getSkills();
            boolean lackSomeSkill = false;

            for (SkillLevelDTO reqSkill : requestedSkills) {
                boolean found = false;
                for (SkillLevelDTO memSkill : memberSkills) {
                    if (memSkill.getId() == reqSkill.getId()) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    lackSomeSkill = true;
                    break;
                }
            }

            if (!lackSomeSkill) {
                ret.add(member);
            }
        }

        return ret;
    }

    private List<TeamMemberSkillDTO> removeMembersNotRequested(List<TeamMemberSkillDTO> potentialMembers,
                                                               Set<UUID> requestedMembers) {
        List<TeamMemberSkillDTO> ret = new ArrayList<TeamMemberSkillDTO>();
        for (TeamMemberSkillDTO member : potentialMembers) {
            if (requestedMembers.contains(member.getId())) {
                ret.add(member);
            }
        }

        return ret;
    }

    private boolean isSkillLevelSatisfied(String first, String second) {
        HashMap<String, int> levels = new HashMap<>();
        levels.put("novice", 1);
        levels.put("intermediate", 2);
        levels.put("advanced", 3);
        levels.put("expert", 4);

        String firstLc = first.toLowerCase();
        String secondLc = second.toLowerCase();
        if (!levels.containsKey(firstLc) || !levels.containsKey(secondLc)) {
            throw new BadArgException(String.format("Compare invalid skill level: %s and %s", first, second));
        }

        return levels.get(firstLc) >= levels.get(secondLc);
    }
