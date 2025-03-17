package com.objectcomputing.checkins.services.member_skill.skillsreport;

import com.objectcomputing.checkins.exceptions.BadArgException;
import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.fixture.SkillFixture;
import com.objectcomputing.checkins.services.fixture.MemberProfileFixture;
import com.objectcomputing.checkins.services.fixture.MemberSkillFixture;
import com.objectcomputing.checkins.services.fixture.RoleFixture;
import com.objectcomputing.checkins.services.skills.Skill;
import com.objectcomputing.checkins.services.member_skill.skillsreport.SkillLevel;
import com.objectcomputing.checkins.services.member_skill.MemberSkill;
import com.objectcomputing.checkins.services.member_skill.MemberSkillRepository;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileRepository;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileServices;
import com.objectcomputing.checkins.services.skills.SkillRepository;
import com.objectcomputing.checkins.services.CurrentUserServicesReplacement;

import io.micronaut.context.annotation.Property;
import io.micronaut.core.util.StringUtils;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import jakarta.inject.Inject;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Property(name = "replace.currentuserservices", value = StringUtils.TRUE)
class SkillsReportServicesImplTest extends TestContainersSuite
                                   implements MemberProfileFixture, MemberSkillFixture, SkillFixture, RoleFixture {
    @Inject
    CurrentUserServicesReplacement currentUserServices;

    @Inject
    private SkillsReportServicesImpl skillsReportServices;

    @BeforeEach
    void setUp() {
        createAndAssignRoles();
    }

    @Test
    void testReportSkillNotExist() {
        final SkillLevelDTO dto = new SkillLevelDTO();
        dto.setId(UUID.randomUUID());
        dto.setLevel(SkillLevel.INTERMEDIATE);

        final List<SkillLevelDTO> skills = new ArrayList<>();
        skills.add(dto);

        final SkillsReportRequestDTO request = new SkillsReportRequestDTO();
        request.setSkills(skills);
        currentUserServices.currentUser = createADefaultMemberProfile();
        assignAdminRole(currentUserServices.currentUser);
        assertThrows(BadArgException.class, () -> skillsReportServices.report(request));
    }

    @Test
    void testReportMemberProfileNotExist() {
        final Skill skill = createADefaultSkill();

        final List<SkillLevelDTO> skills = new ArrayList<>();
        skills.add(new SkillLevelDTO(skill.getId(), SkillLevel.NOVICE));

        final SkillsReportRequestDTO request = new SkillsReportRequestDTO();
        request.setSkills(skills);
        final Set<UUID> members = new HashSet<>();
        members.add(UUID.randomUUID());
        request.setMembers(members);

        currentUserServices.currentUser = createADefaultMemberProfile();
        assignAdminRole(currentUserServices.currentUser);
        assertThrows(BadArgException.class, () -> skillsReportServices.report(request));
    }

    @Test
    void testReportEmptyRequestedSkillsList() {
        final SkillsReportRequestDTO request =  new SkillsReportRequestDTO();
        request.setSkills(new ArrayList<>());
        currentUserServices.currentUser = createADefaultMemberProfile();
        assignAdminRole(currentUserServices.currentUser);
        final SkillsReportResponseDTO response = skillsReportServices.report(request);
        assertNotNull(response);
        assertEquals(0, response.getTeamMembers().size());
    }

    @Test
    void testReport() {
        Skill skill1 = createSkill("Skill1", false, "First", false);
        Skill skill2 = createSkill("Skill2", false, "Second", false);
        Skill skill3 = createSkill("Skill3", false, "Third", false);
        Skill skill4 = createSkill("Skill4", false, "Fourth", false);

        MemberProfile member1 = createADefaultMemberProfile();
        MemberProfile member2 = createASecondDefaultMemberProfile();
        MemberProfile member3 = createAThirdDefaultMemberProfile();
        MemberProfile member4 = createADefaultMemberProfileForPdl(member1);
        MemberProfile member5 = createAPastTerminatedMemberProfile();

        final UUID skillId1 = skill1.getId();
        final UUID skillId2 = skill2.getId();
        final UUID skillId3 = skill3.getId();
        final UUID skillId4 = skill4.getId();
        final UUID memberId1 = member1.getId();
        final UUID memberId2 = member2.getId();
        final UUID memberId3 = member3.getId();
        final UUID memberId4 = member4.getId();

        final MemberSkill ms1 = createMemberSkill(member1, skill1, SkillLevel.INTERMEDIATE_LEVEL, LocalDate.now());
        final MemberSkill ms2 = createMemberSkill(member1, skill2, SkillLevel.ADVANCED_LEVEL, LocalDate.now());
        final MemberSkill ms3 = createMemberSkill(member2, skill3, SkillLevel.NOVICE_LEVEL, LocalDate.now());
        final MemberSkill ms4 = createMemberSkill(member2, skill4, SkillLevel.EXPERT_LEVEL, LocalDate.now());
        final MemberSkill ms5 = createMemberSkill(member3, skill2, SkillLevel.INTERESTED_LEVEL, LocalDate.now());
        final MemberSkill ms6 = createMemberSkill(member3, skill3, SkillLevel.ADVANCED_LEVEL, LocalDate.now());
        final MemberSkill ms7 = createMemberSkill(member4, skill1, SkillLevel.ADVANCED_LEVEL, LocalDate.now());
        final MemberSkill ms8 = createMemberSkill(member4, skill2, SkillLevel.INTERMEDIATE_LEVEL, LocalDate.now());
        final MemberSkill ms9 = createMemberSkill(member4, skill4, SkillLevel.EXPERT_LEVEL, LocalDate.now());

        // Skills for the terminated member
        createMemberSkill(member5, skill1, SkillLevel.ADVANCED_LEVEL, LocalDate.now());
        createMemberSkill(member5, skill2, SkillLevel.ADVANCED_LEVEL, LocalDate.now());
        createMemberSkill(member5, skill3, SkillLevel.ADVANCED_LEVEL, LocalDate.now());
        createMemberSkill(member5, skill4, SkillLevel.ADVANCED_LEVEL, LocalDate.now());

        // List of skills required in first request
        final SkillLevelDTO dto1 = new SkillLevelDTO();
        final SkillLevelDTO dto2 = new SkillLevelDTO();
        final SkillLevelDTO dto3 = new SkillLevelDTO();
        dto1.setId(skillId1);
        dto1.setLevel(SkillLevel.INTERMEDIATE);
        dto2.setId(skillId2);
        dto3.setId(skillId3);
        dto3.setLevel(SkillLevel.ADVANCED);

        final List<SkillLevelDTO> requestedSkills1 = new ArrayList<>();
        requestedSkills1.add(dto1);
        requestedSkills1.add(dto2);
        requestedSkills1.add(dto3);

        currentUserServices.currentUser = member1;
        assignAdminRole(currentUserServices.currentUser);

        // Any member with at least 1 satisfying skill is returned
        final SkillsReportRequestDTO request1 = new SkillsReportRequestDTO();
        request1.setSkills(requestedSkills1);
        final SkillsReportResponseDTO response1 = skillsReportServices.report(request1);

        assertEquals(3, response1.getTeamMembers().size());
        for (TeamMemberSkillDTO elem : response1.getTeamMembers()) {
            assertTrue(elem.getId().equals(memberId1) ||
                    elem.getId().equals(memberId3) || elem.getId().equals(memberId4));
            if (elem.getId().equals(memberId1)) {
                assertReturnedMember1(elem, skillId1, skillId2, member1.getFirstName() + " " + member1.getLastName());
            } else if (elem.getId().equals(memberId3)) {
                assertReturnedMember3(elem, skillId2, skillId3, member3.getFirstName() + " " + member3.getLastName());
            } else {
                assertReturnedMember4(elem, skillId1, skillId2, member4.getFirstName() + " " + member4.getLastName());
            }
        }

        // Specify a list of members
        final Set<UUID> members =  new HashSet<>();
        members.add(memberId2);
        members.add(memberId3);
        members.add(memberId4);
        request1.setMembers(members);
        final SkillsReportResponseDTO response2 = skillsReportServices.report(request1);

        assertEquals(2, response2.getTeamMembers().size());
        for (TeamMemberSkillDTO elem : response2.getTeamMembers()) {
            assertTrue(elem.getId().equals(memberId3) || elem.getId().equals(memberId4));
            if (elem.getId().equals(memberId3)) {
                assertReturnedMember3(elem, skillId2, skillId3, member3.getFirstName() + " " + member3.getLastName());
            } else {
                assertReturnedMember4(elem, skillId1, skillId2, member4.getFirstName() + " " + member4.getLastName());
            }
        }

        // Each returned member must satisfy all requested skills
        request1.setInclusive(true);
        final SkillsReportResponseDTO response3 = skillsReportServices.report(request1);
        assertTrue(response3.getTeamMembers().isEmpty());

        // Another request
        final SkillLevelDTO dto4 = new SkillLevelDTO();
        final SkillLevelDTO dto5 = new SkillLevelDTO();
        dto4.setId(skillId2);
        dto4.setLevel(SkillLevel.INTERMEDIATE);
        dto5.setId(skillId4);
        dto5.setLevel(SkillLevel.ADVANCED);

        final List<SkillLevelDTO> requestedSkills2 = new ArrayList<>();
        requestedSkills2.add(dto4);
        requestedSkills2.add(dto5);

        final SkillsReportRequestDTO request2 = new SkillsReportRequestDTO();
        request2.setSkills(requestedSkills2);
        request2.setInclusive(true);
        final SkillsReportResponseDTO response4 = skillsReportServices.report(request2);

        assertEquals(1, response4.getTeamMembers().size());
        assertEquals(memberId4, response4.getTeamMembers().get(0).getId());
        assertEquals(member4.getFirstName() + " " + member4.getLastName(), response4.getTeamMembers().get(0).getName());
        assertEquals(2, response4.getTeamMembers().get(0).getSkills().size());
        for (SkillLevelDTO skill : response4.getTeamMembers().get(0).getSkills()) {
            assertTrue(skill.getId().equals(skillId2) || skill.getId().equals(skillId4));
            if (skill.getId().equals(skillId2)) {
                assertEquals(SkillLevel.convertFromString(SkillLevel.INTERMEDIATE_LEVEL), skill.getLevel());
            } else {
                assertEquals(SkillLevel.convertFromString(SkillLevel.EXPERT_LEVEL), skill.getLevel());
            }
        }
    }

    private void assertReturnedMember1(TeamMemberSkillDTO elem, UUID skillId1, UUID skillId2, String fullName) {
        assertEquals(fullName, elem.getName());
        assertEquals(2, elem.getSkills().size());
        for (SkillLevelDTO skill : elem.getSkills()) {
            assertTrue(skill.getId().equals(skillId1) || skill.getId().equals(skillId2));
            if (skill.getId().equals(skillId1)) {
                assertEquals(SkillLevel.convertFromString(SkillLevel.INTERMEDIATE_LEVEL), skill.getLevel());
            } else {
                assertEquals(SkillLevel.convertFromString(SkillLevel.ADVANCED_LEVEL), skill.getLevel());
            }
        }
    }

    private void assertReturnedMember3(TeamMemberSkillDTO elem, UUID skillId2, UUID skillId3, String fullName) {
        assertEquals(fullName, elem.getName());
        assertEquals(2, elem.getSkills().size());
        for (SkillLevelDTO skill : elem.getSkills()) {
            assertTrue(skill.getId().equals(skillId2) || skill.getId().equals(skillId3));
            if (skill.getId().equals(skillId2)) {
                assertEquals(SkillLevel.convertFromString(SkillLevel.INTERESTED_LEVEL), skill.getLevel());
            } else {
                assertEquals(SkillLevel.convertFromString(SkillLevel.ADVANCED_LEVEL), skill.getLevel());
            }
        }
    }

    private void assertReturnedMember4(TeamMemberSkillDTO elem, UUID skillId1, UUID skillId2, String fullName) {
        assertEquals(fullName, elem.getName());
        assertEquals(2, elem.getSkills().size());
        for (SkillLevelDTO skill : elem.getSkills()) {
            assertTrue(skill.getId().equals(skillId1) || skill.getId().equals(skillId2));
            if (skill.getId().equals(skillId1)) {
                assertEquals(SkillLevel.convertFromString(SkillLevel.ADVANCED_LEVEL), skill.getLevel());
            } else {
                assertEquals(SkillLevel.convertFromString(SkillLevel.INTERMEDIATE_LEVEL), skill.getLevel());
            }
        }
    }
}
