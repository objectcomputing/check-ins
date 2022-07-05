package com.objectcomputing.checkins.services.member_skill.skillsreport;

import com.objectcomputing.checkins.services.member_skill.MemberSkillRepository;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileRepository;
import com.objectcomputing.checkins.services.member_skill.MemberSkill;
import com.objectcomputing.checkins.exceptions.BadArgException;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileServices;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileUtils;
import com.objectcomputing.checkins.services.skills.SkillRepository;

import jakarta.inject.Singleton;
import javax.validation.constraints.NotNull;
import java.util.*;

@Singleton
public class SkillsReportServicesImpl implements SkillsReportServices {
    private final MemberSkillRepository memberSkillRepo;
    private final MemberProfileRepository memberProfileRepo;
    private final MemberProfileServices memberProfileServices;
    private final SkillRepository skillRepo;

    public SkillsReportServicesImpl(MemberSkillRepository memberSkillRepo,
                                    MemberProfileRepository memberProfileRepo,
                                    MemberProfileServices memberProfileServices,
                                    SkillRepository skillRepo) {
        this.memberSkillRepo = memberSkillRepo;
        this.memberProfileRepo = memberProfileRepo;
        this.memberProfileServices = memberProfileServices;
        this.skillRepo = skillRepo;
    }

    @NotNull
    public SkillsReportResponseDTO report(@NotNull SkillsReportRequestDTO request) {
        SkillsReportResponseDTO response = null;
        if (request != null) {
            final List<SkillLevelDTO> skills = request.getSkills();
            final Set<UUID> members = request.getMembers();
            final Boolean inclusive = request.isInclusive();

            for (SkillLevelDTO skill : skills) {
                if (!skillRepo.existsById(skill.getId())) {
                    throw new BadArgException(String.format("Invalid skill ID %s", skill.getId()));
                }
            }

            if (members != null) {
                for (UUID member : members) {
                    if (!memberProfileRepo.existsById(member)) {
                        throw new BadArgException(String.format("Invalid member profile ID %s", member));
                    }
                }
            }

            response = new SkillsReportResponseDTO();

            final List<TeamMemberSkillDTO> potentialMembers = getPotentialQualifyingMembers(skills);
            if (members == null || members.isEmpty()) {
                if (inclusive == null || !inclusive) {
                    response.setTeamMembers(potentialMembers);
                } else {
                    final List<TeamMemberSkillDTO> qualifiedMembers = getMembersSatisfyingAllSkills(potentialMembers, skills);
                    response.setTeamMembers(qualifiedMembers);
                }
            } else {
                final List<TeamMemberSkillDTO> membersInList = removeMembersNotRequested(potentialMembers, members);
                if (inclusive == null || !inclusive) {
                    response.setTeamMembers(membersInList);
                } else {
                    final List<TeamMemberSkillDTO> qualifiedMembers = getMembersSatisfyingAllSkills(membersInList, skills);
                    response.setTeamMembers(qualifiedMembers);
                }
            }
        }

        return response;
    }

    @NotNull
    private List<TeamMemberSkillDTO> getPotentialQualifyingMembers(List<SkillLevelDTO> skills) {
        // Get all member_skill entries that satisfy a requested skill
        final List<MemberSkill> entries = new ArrayList<>();

        for (SkillLevelDTO skill : skills) {
            if (skill.getId() == null) {
                throw new BadArgException("Invalid requested skill ID");
            }

            final List<MemberSkill> temp = memberSkillRepo.findBySkillid(skill.getId());
            if (skill.getLevel() != null && temp.size() > 0) {
                for (MemberSkill memSkill : temp) {
                    if (memSkill.getSkilllevel() != null) {
                        if (isSkillLevelSatisfied(memSkill.getSkilllevel(), skill.getLevel())) {
                            entries.add(memSkill);
                        }
                    }
                }
            } else {
                // The input doesn't specify a required level, so all members have this skill are added
                entries.addAll(temp);
            }
        }

        // Collect all entries belong to each team member
        final HashMap<UUID, TeamMemberSkillDTO> map = new HashMap<>();
        for (MemberSkill ms : entries) {
            final UUID memberId = ms.getMemberid();
            final SkillLevelDTO skill = new SkillLevelDTO();
            skill.setId(ms.getSkillid());
            skill.setLevel(SkillLevel.convertFromString(ms.getSkilllevel()));

            if (map.containsKey(memberId)) {
                final TeamMemberSkillDTO dto = map.get(memberId);
                dto.getSkills().add(skill);
            } else {
                final TeamMemberSkillDTO dto = new TeamMemberSkillDTO();
                dto.setId(memberId);

                final MemberProfile memProfile = memberProfileServices.getById(memberId);
                final String memberName = MemberProfileUtils.getFullName(memProfile);
                dto.setName(memberName);

                final List<SkillLevelDTO> memberSkills = new ArrayList<>();
                memberSkills.add(skill);
                dto.setSkills(memberSkills);

                map.put(memberId, dto);
            }
        }

        return new ArrayList<>(map.values());
    }

    @NotNull
    private List<TeamMemberSkillDTO> getMembersSatisfyingAllSkills(List<TeamMemberSkillDTO> potentialMembers,
                                                                   List<SkillLevelDTO> requestedSkills) {
        final List<TeamMemberSkillDTO> ret = new ArrayList<>();
        for (TeamMemberSkillDTO member : potentialMembers) {
            final List<SkillLevelDTO> memberSkills = member.getSkills();
            boolean lackSomeSkill = false;

            for (SkillLevelDTO reqSkill : requestedSkills) {
                boolean found = false;
                for (SkillLevelDTO memSkill : memberSkills) {
                    if (memSkill.getId().equals(reqSkill.getId())) {
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

    @NotNull
    private List<TeamMemberSkillDTO> removeMembersNotRequested(List<TeamMemberSkillDTO> potentialMembers,
                                                               Set<UUID> requestedMembers) {
        final List<TeamMemberSkillDTO> ret = new ArrayList<>();
        for (TeamMemberSkillDTO member : potentialMembers) {
            if (requestedMembers.contains(member.getId())) {
                ret.add(member);
            }
        }

        return ret;
    }

    private boolean isSkillLevelSatisfied(String first, SkillLevel second) {
        final SkillLevel firstLevel = SkillLevel.convertFromString(first);
        return firstLevel.greaterThanOrEqual(second);
    }
}
