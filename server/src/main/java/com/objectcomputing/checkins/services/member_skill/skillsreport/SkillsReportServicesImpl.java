package com.objectcomputing.checkins.services.member_skill.skillsreport;

import com.objectcomputing.checkins.services.permissions.Permission;
import com.objectcomputing.checkins.services.permissions.RequiredPermission;
import com.objectcomputing.checkins.exceptions.BadArgException;
import com.objectcomputing.checkins.services.member_skill.MemberSkill;
import com.objectcomputing.checkins.services.member_skill.MemberSkillRepository;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileRepository;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileServices;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileUtils;
import com.objectcomputing.checkins.services.skills.SkillRepository;
import jakarta.inject.Singleton;
import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;

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

    @Override
    @RequiredPermission(Permission.CAN_VIEW_SKILLS_REPORT)
    public @NotNull SkillsReportResponseDTO report(@NotNull SkillsReportRequestDTO request) {
        final List<SkillLevelDTO> skills = request.getSkills();
        final Set<UUID> members = request.getMembers();
        final boolean inclusive = Boolean.TRUE.equals(request.isInclusive());

        validateSkills(skills);
        validateMembers(members);

        SkillsReportResponseDTO response = new SkillsReportResponseDTO();

        final List<TeamMemberSkillDTO> potentialMembers = getPotentialQualifyingMembers(skills);
        if (members == null || members.isEmpty()) {
            if (inclusive) {
                response.setTeamMembers(getMembersSatisfyingAllSkills(potentialMembers, skills));
            } else {
                response.setTeamMembers(potentialMembers);
            }
        } else {
            final List<TeamMemberSkillDTO> membersInList = removeMembersNotRequested(potentialMembers, members);
            if (inclusive) {
                response.setTeamMembers(getMembersSatisfyingAllSkills(membersInList, skills));
            } else {
                response.setTeamMembers(membersInList);
            }
        }
        return response;
    }

    private void validateMembers(Set<UUID> members) {
        if (members != null) {
            for (UUID member : members) {
                if (!memberProfileRepo.existsById(member)) {
                    throw new BadArgException(String.format("Invalid member profile ID %s", member));
                }
            }
        }
    }

    private void validateSkills(List<SkillLevelDTO> skills) {
        for (SkillLevelDTO skill : skills) {
            if (!skillRepo.existsById(skill.getId())) {
                throw new BadArgException(String.format("Invalid skill ID %s", skill.getId()));
            }
        }
    }

    private List<TeamMemberSkillDTO> getPotentialQualifyingMembers(List<SkillLevelDTO> skills) {
        // Get all member_skill entries that satisfy a requested skill
        List<MemberSkill> entries = new ArrayList<>();

        for (SkillLevelDTO skill : skills) {
            if (skill.getId() == null) {
                throw new BadArgException("Invalid requested skill ID");
            }

            final List<MemberSkill> temp = memberSkillRepo.findBySkillid(skill.getId());
            if (skill.getLevel() != null && !temp.isEmpty()) {
                for (MemberSkill memSkill : temp) {
                    if (memSkill.getSkilllevel() != null && isSkillLevelSatisfied(memSkill.getSkilllevel(), skill.getLevel())) {
                        entries.add(memSkill);
                    }
                }
            } else {
                // The input doesn't specify a required level, so all members have this skill are added
                entries.addAll(temp);
            }
        }

        // Collect all entries belong to each team member
        final HashMap<UUID, TeamMemberSkillDTO> map = collectEntries(entries);

        return new ArrayList<>(map.values());
    }

    private HashMap<UUID, TeamMemberSkillDTO> collectEntries(List<MemberSkill> entries) {
        final HashMap<UUID, TeamMemberSkillDTO> map = new HashMap<>();

        for (MemberSkill ms : entries) {
            final UUID memberId = ms.getMemberid();

            final SkillLevelDTO skill = new SkillLevelDTO(ms.getSkillid(), SkillLevel.convertFromString(ms.getSkilllevel()));

            var dto = map.computeIfAbsent(memberId, mId ->
                    new TeamMemberSkillDTO(
                            mId,
                            MemberProfileUtils.getFullName(memberProfileServices.getById(mId)),
                            new ArrayList<>()
                    )
            );
            dto.getSkills().add(skill);
        }
        return map;
    }

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
