package com.objectcomputing.checkins.services.member_skill.skillsreport;

import com.objectcomputing.checkins.services.member_skill.MemberSkill;
import com.objectcomputing.checkins.services.member_skill.MemberSkillRepository;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileRepository;
import io.micronaut.test.annotation.MicronautTest;
import org.junit.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@MicronautTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SkillsReportServicesImplTest {
    @Mock
    private MemberSkillRepository memberSkillRepository;

    @Mock
    private MemberProfileRepository memberProfileRepository;

    @InjectMocks
    private SkillsReportServicesImpl skillsReportServices;

    @BeforeAll
    void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    @BeforeEach
    void resetMocks() {
        reset(memberSkillRepository, memberProfileRepository);
    }

    @Test
    void testReportNullRequest() {
        assertNull(skillsReportServices.report(null));
        verify(memberSkillRepository, never()).findBySkillid(any(UUID.class));
        verify(memberProfileRepository, never()).findNameById(any(UUID.class));
    }

    @Test
    void testReportEmptyRequestedSkillsList() {
        SkillsReportRequestDTO request =  new SkillsReportRequestDTO();
        request.setSkills(new ArrayList<>());
        SkillsReportResponseDTO response = skillsReportServices.report(request);
        assertNotNull(response);

        verify(memberSkillRepository, never()).findBySkillid(any(UUID.class));
        verify(memberProfileRepository, never()).findNameById(any(UUID.class));
    }

    @Test
    void testReport() {
        final UUID skillId1 = UUID.randomUUID();
        final UUID skillId2 = UUID.randomUUID();
        final UUID skillId3 = UUID.randomUUID();
        final UUID skillId4 = UUID.randomUUID();
        final UUID memberId1 = UUID.randomUUID();
        final UUID memberId2 = UUID.randomUUID();
        final UUID memberId3 = UUID.randomUUID();
        final UUID memberId4 = UUID.randomUUID();

        final MemberSkill ms1 = new MemberSkill(memberId1, skillId1, "intermediate", LocalDate.now());
        final MemberSkill ms2 = new MemberSkill(memberId1, skillId2, "advanced", LocalDate.now());
        final MemberSkill ms3 = new MemberSkill(memberId2, skillId3, "novice", LocalDate.now());
        final MemberSkill ms4 = new MemberSkill(memberId2, skillId4, "expert", LocalDate.now());
        final MemberSkill ms5 = new MemberSkill(memberId3, skillId2, "interested", LocalDate.now());
        final MemberSkill ms6 = new MemberSkill(memberId3, skillId3, "advanced", LocalDate.now());
        final MemberSkill ms7 = new MemberSkill(memberId4, skillId1, "advanced", LocalDate.now());
        final MemberSkill ms8 = new MemberSkill(memberId4, skillId2, "intermediate", LocalDate.now());
        final MemberSkill ms9 = new MemberSkill(memberId4, skillId4, "expert", LocalDate.now());

        List<MemberSkill> skillList1 = new ArrayList<>();
        skillList1.add(ms1);
        skillList1.add(ms7);
        List<MemberSkill> skillList2 = new ArrayList<>();
        skillList2.add(ms2);
        skillList2.add(ms5);
        skillList2.add(ms8);
        List<MemberSkill> skillList3 = new ArrayList<>();
        skillList3.add(ms3);
        skillList3.add(ms6);
        List<MemberSkill> skillList4 = new ArrayList<>();
        skillList4.add(ms4);
        skillList4.add(ms9);

        when(memberSkillRepository.findBySkillid(skillId1)).thenReturn(skillList1);
        when(memberSkillRepository.findBySkillid(skillId2)).thenReturn(skillList2);
        when(memberSkillRepository.findBySkillid(skillId3)).thenReturn(skillList3);
        when(memberSkillRepository.findBySkillid(skillId4)).thenReturn(skillList4);
        when(memberProfileRepository.findNameById(memberId1)).thenReturn("Joey");
        when(memberProfileRepository.findNameById(memberId2)).thenReturn("Chandler");
        when(memberProfileRepository.findNameById(memberId3)).thenReturn(null);
        when(memberProfileRepository.findNameById(memberId4)).thenReturn("Ross");

        // List of skills required in first request
        SkillLevelDTO dto1 = new SkillLevelDTO();
        SkillLevelDTO dto2 = new SkillLevelDTO();
        SkillLevelDTO dto3 = new SkillLevelDTO();
        dto1.setId(skillId1);
        dto1.setLevel("intermediate");
        dto2.setId(skillId2);
        dto3.setId(skillId3);
        dto3.setLevel("advanced");

        List<SkillLevelDTO> requestedSkills1 = new ArrayList<>();
        requestedSkills1.add(dto1);
        requestedSkills1.add(dto2);
        requestedSkills1.add(dto3);

        // Any member with at least 1 satisfying skill is returned
        SkillsReportRequestDTO request1 = new SkillsReportRequestDTO();
        request1.setSkills(requestedSkills1);
        SkillsReportResponseDTO response1 = skillsReportServices.report(request1);

        assertEquals(3, response1.getTeamMembers().size());
        for (TeamMemberSkillDTO elem : response1.getTeamMembers()) {
            assertTrue(elem.getId().equals(memberId1) ||
                    elem.getId().equals(memberId3) || elem.getId().equals(memberId4));
            if (elem.getId().equals(memberId1)) {
                assertReturnedMember1(elem, skillId1, skillId2);
            } else if (elem.getId().equals(memberId3)) {
                assertReturnedMember3(elem, skillId2, skillId3);
            } else {
                assertReturnedMember4(elem, skillId1, skillId2);
            }
        }
        verify(memberSkillRepository, times(3)).findBySkillid(any(UUID.class));
        verify(memberProfileRepository, times(3)).findNameById(any(UUID.class));

        // Specify a list of members
        Set<UUID> members =  new HashSet<>();
        members.add(memberId2);
        members.add(memberId3);
        members.add(memberId4);
        request1.setMembers(members);
        SkillsReportResponseDTO response2 = skillsReportServices.report(request1);

        assertEquals(2, response2.getTeamMembers().size());
        for (TeamMemberSkillDTO elem : response2.getTeamMembers()) {
            assertTrue(elem.getId().equals(memberId3) || elem.getId().equals(memberId4));
            if (elem.getId().equals(memberId3)) {
                assertReturnedMember3(elem, skillId2, skillId3);
            } else {
                assertReturnedMember4(elem, skillId1, skillId2);
            }
        }
        verify(memberSkillRepository, times(6)).findBySkillid(any(UUID.class));
        verify(memberProfileRepository, times(6)).findNameById(any(UUID.class));

        // Each returned member must satisfy all requested skills
        request1.setInclusive(true);
        SkillsReportResponseDTO response3 = skillsReportServices.report(request1);
        assertTrue(response3.getTeamMembers().isEmpty());
        verify(memberSkillRepository, times(9)).findBySkillid(any(UUID.class));
        verify(memberProfileRepository, times(9)).findNameById(any(UUID.class));

        // Another request
        SkillLevelDTO dto4 = new SkillLevelDTO();
        SkillLevelDTO dto5 = new SkillLevelDTO();
        dto4.setId(skillId2);
        dto4.setLevel("intermediate");
        dto5.setId(skillId4);
        dto5.setLevel("advanced");

        List<SkillLevelDTO> requestedSkills2 = new ArrayList<>();
        requestedSkills2.add(dto4);
        requestedSkills2.add(dto5);

        SkillsReportRequestDTO request2 = new SkillsReportRequestDTO();
        request2.setSkills(requestedSkills2);
        request2.setInclusive(true);
        SkillsReportResponseDTO response4 = skillsReportServices.report(request2);

        assertEquals(1, response4.getTeamMembers().size());
        assertEquals(memberId4, response4.getTeamMembers().get(0).getId());
        assertEquals("Ross", response4.getTeamMembers().get(0).getName());
        assertEquals(2, response4.getTeamMembers().get(0).getSkills().size());
        for (SkillLevelDTO skill : response4.getTeamMembers().get(0).getSkills()) {
            assertTrue(skill.getId().equals(skillId2) || skill.getId().equals(skillId4));
            if (skill.getId().equals(skillId2)) {
                assertEquals("intermediate", skill.getLevel());
            } else {
                assertEquals("expert", skill.getLevel());
            }
        }
    }

    private void assertReturnedMember1(TeamMemberSkillDTO elem, UUID skillId1, UUID skillId2) {
        assertEquals("Joey", elem.getName());
        assertEquals(2, elem.getSkills().size());
        for (SkillLevelDTO skill : elem.getSkills()) {
            assertTrue(skill.getId().equals(skillId1) || skill.getId().equals(skillId2));
            if (skill.getId().equals(skillId1)) {
                assertEquals("intermediate", skill.getLevel());
            } else {
                assertEquals("advanced", skill.getLevel());
            }
        }
    }

    private void assertReturnedMember3(TeamMemberSkillDTO elem, UUID skillId2, UUID skillId3) {
        assertNull(elem.getName());
        assertEquals(2, elem.getSkills().size());
        for (SkillLevelDTO skill : elem.getSkills()) {
            assertTrue(skill.getId().equals(skillId2) || skill.getId().equals(skillId3));
            if (skill.getId().equals(skillId2)) {
                assertEquals("interested", skill.getLevel());
            } else {
                assertEquals("advanced", skill.getLevel());
            }
        }
    }

    private void assertReturnedMember4(TeamMemberSkillDTO elem, UUID skillId1, UUID skillId2) {
        assertEquals("Ross", elem.getName());
        assertEquals(2, elem.getSkills().size());
        for (SkillLevelDTO skill : elem.getSkills()) {
            assertTrue(skill.getId().equals(skillId1) || skill.getId().equals(skillId2));
            if (skill.getId().equals(skillId1)) {
                assertEquals("advanced", skill.getLevel());
            } else {
                assertEquals("intermediate", skill.getLevel());
            }
        }
    }
}
