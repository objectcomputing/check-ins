package com.objectcomputing.checkins.services.member_skill.skillsreport;

import com.objectcomputing.checkins.exceptions.BadArgException;
import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.member_skill.MemberSkill;
import com.objectcomputing.checkins.services.member_skill.MemberSkillRepository;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileRepository;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileServices;
import com.objectcomputing.checkins.services.skills.SkillRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledInNativeImage;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

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
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

// Disabled in nativeTest, as we get an exception from Mockito
//    => java.lang.NoClassDefFoundError: Could not initialize class org.mockito.internal.configuration.plugins.Plugins
@DisabledInNativeImage
class SkillsReportServicesImplTest extends TestContainersSuite {

    @Mock
    private MemberSkillRepository memberSkillRepository;

    @Mock
    private MemberProfileRepository memberProfileRepository;

    @Mock
    private MemberProfileServices memberProfileServices;

    @Mock
    private SkillRepository skillRepository;

    @InjectMocks
    private SkillsReportServicesImpl skillsReportServices;

    private AutoCloseable mockFinalizer;

    @BeforeAll
    void initMocks() {
        mockFinalizer = MockitoAnnotations.openMocks(this);
    }

    @BeforeEach
    void resetMocks() {
        reset(memberSkillRepository, memberProfileRepository, skillRepository);
    }

    @AfterAll
    void finalizeMocks() throws Exception {
        mockFinalizer.close();
    }

    @Test
    void testReportNullRequest() {
        assertNull(skillsReportServices.report(null));
        verify(memberSkillRepository, never()).findBySkillid(any(UUID.class));
        verify(memberProfileServices, never()).getById(any(UUID.class));
        verify(skillRepository, never()).existsById(any(UUID.class));
        verify(memberProfileRepository, never()).existsById(any(UUID.class));
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
        assertThrows(BadArgException.class, () -> skillsReportServices.report(request));
    }

    @Test
    void testReportMemberProfileNotExist() {
        final SkillLevelDTO dto = new SkillLevelDTO();
        final UUID skillId = UUID.randomUUID();
        dto.setId(skillId);
        when(skillRepository.existsById(skillId)).thenReturn(true);

        final List<SkillLevelDTO> skills = new ArrayList<>();
        skills.add(dto);

        final SkillsReportRequestDTO request = new SkillsReportRequestDTO();
        request.setSkills(skills);
        final Set<UUID> members = new HashSet<>();
        members.add(UUID.randomUUID());
        request.setMembers(members);
        assertThrows(BadArgException.class, () -> skillsReportServices.report(request));
    }

    @Test
    void testReportEmptyRequestedSkillsList() {
        final SkillsReportRequestDTO request =  new SkillsReportRequestDTO();
        request.setSkills(new ArrayList<>());
        final SkillsReportResponseDTO response = skillsReportServices.report(request);
        assertNotNull(response);

        verify(memberSkillRepository, never()).findBySkillid(any(UUID.class));
        verify(memberProfileServices, never()).getById(any(UUID.class));
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

        final MemberSkill ms1 = new MemberSkill(memberId1, skillId1, SkillLevel.INTERMEDIATE_LEVEL, LocalDate.now());
        final MemberSkill ms2 = new MemberSkill(memberId1, skillId2, SkillLevel.ADVANCED_LEVEL, LocalDate.now());
        final MemberSkill ms3 = new MemberSkill(memberId2, skillId3, SkillLevel.NOVICE_LEVEL, LocalDate.now());
        final MemberSkill ms4 = new MemberSkill(memberId2, skillId4, SkillLevel.EXPERT_LEVEL, LocalDate.now());
        final MemberSkill ms5 = new MemberSkill(memberId3, skillId2, SkillLevel.INTERESTED_LEVEL, LocalDate.now());
        final MemberSkill ms6 = new MemberSkill(memberId3, skillId3, SkillLevel.ADVANCED_LEVEL, LocalDate.now());
        final MemberSkill ms7 = new MemberSkill(memberId4, skillId1, SkillLevel.ADVANCED_LEVEL, LocalDate.now());
        final MemberSkill ms8 = new MemberSkill(memberId4, skillId2, SkillLevel.INTERMEDIATE_LEVEL, LocalDate.now());
        final MemberSkill ms9 = new MemberSkill(memberId4, skillId4, SkillLevel.EXPERT_LEVEL, LocalDate.now());

        final List<MemberSkill> skillList1 = new ArrayList<>();
        skillList1.add(ms1);
        skillList1.add(ms7);
        final List<MemberSkill> skillList2 = new ArrayList<>();
        skillList2.add(ms2);
        skillList2.add(ms5);
        skillList2.add(ms8);
        final List<MemberSkill> skillList3 = new ArrayList<>();
        skillList3.add(ms3);
        skillList3.add(ms6);
        final List<MemberSkill> skillList4 = new ArrayList<>();
        skillList4.add(ms4);
        skillList4.add(ms9);

        when(memberSkillRepository.findBySkillid(skillId1)).thenReturn(skillList1);
        when(memberSkillRepository.findBySkillid(skillId2)).thenReturn(skillList2);
        when(memberSkillRepository.findBySkillid(skillId3)).thenReturn(skillList3);
        when(memberSkillRepository.findBySkillid(skillId4)).thenReturn(skillList4);
        MemberProfile joey = new MemberProfile("Joey", null, "Tribbiani", null,
                null, null, null, null, null, null, null,
                null, null, null, null, null, null);
        MemberProfile chandler = new MemberProfile("Chandler", null, "Bing", null,
                null, null, null, null, null, null, null,
                null, null,null, null, null, null);
        MemberProfile ross = new MemberProfile("Ross", null, "Geller", null,
                null, null, null, null, null, null, null,
                null, null,null, null, null, null);
        when(memberProfileServices.getById(memberId1)).thenReturn(joey);
        when(memberProfileServices.getById(memberId2)).thenReturn(chandler);
        when(memberProfileServices.getById(memberId3)).thenReturn(null);
        when(memberProfileServices.getById(memberId4)).thenReturn(ross);

        when(skillRepository.existsById(skillId1)).thenReturn(true);
        when(skillRepository.existsById(skillId2)).thenReturn(true);
        when(skillRepository.existsById(skillId3)).thenReturn(true);
        when(skillRepository.existsById(skillId4)).thenReturn(true);
        when(memberProfileRepository.existsById(memberId1)).thenReturn(true);
        when(memberProfileRepository.existsById(memberId2)).thenReturn(true);
        when(memberProfileRepository.existsById(memberId3)).thenReturn(true);
        when(memberProfileRepository.existsById(memberId4)).thenReturn(true);

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

        // Any member with at least 1 satisfying skill is returned
        final SkillsReportRequestDTO request1 = new SkillsReportRequestDTO();
        request1.setSkills(requestedSkills1);
        final SkillsReportResponseDTO response1 = skillsReportServices.report(request1);

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
        verify(memberProfileServices, times(3)).getById(any(UUID.class));
        verify(skillRepository, times(3)).existsById(any(UUID.class));
        verify(memberProfileRepository, never()).existsById(any(UUID.class));

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
                assertReturnedMember3(elem, skillId2, skillId3);
            } else {
                assertReturnedMember4(elem, skillId1, skillId2);
            }
        }
        verify(memberSkillRepository, times(6)).findBySkillid(any(UUID.class));
        verify(memberProfileServices, times(6)).getById(any(UUID.class));
        verify(skillRepository, times(6)).existsById(any(UUID.class));
        verify(memberProfileRepository, times(3)).existsById(any(UUID.class));

        // Each returned member must satisfy all requested skills
        request1.setInclusive(true);
        final SkillsReportResponseDTO response3 = skillsReportServices.report(request1);
        assertTrue(response3.getTeamMembers().isEmpty());
        verify(memberSkillRepository, times(9)).findBySkillid(any(UUID.class));
        verify(memberProfileServices, times(9)).getById(any(UUID.class));
        verify(skillRepository, times(9)).existsById(any(UUID.class));
        verify(memberProfileRepository, times(6)).existsById(any(UUID.class));

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
        assertEquals("Ross Geller", response4.getTeamMembers().get(0).getName());
        assertEquals(2, response4.getTeamMembers().get(0).getSkills().size());
        for (SkillLevelDTO skill : response4.getTeamMembers().get(0).getSkills()) {
            assertTrue(skill.getId().equals(skillId2) || skill.getId().equals(skillId4));
            if (skill.getId().equals(skillId2)) {
                assertEquals(SkillLevel.convertFromString(SkillLevel.INTERMEDIATE_LEVEL), skill.getLevel());
            } else {
                assertEquals(SkillLevel.convertFromString(SkillLevel.EXPERT_LEVEL), skill.getLevel());
            }
        }
        verify(memberSkillRepository, times(11)).findBySkillid(any(UUID.class));
        verify(memberProfileServices, times(12)).getById(any(UUID.class));
        verify(skillRepository, times(11)).existsById(any(UUID.class));
        verify(memberProfileRepository, times(6)).existsById(any(UUID.class));
    }

    private void assertReturnedMember1(TeamMemberSkillDTO elem, UUID skillId1, UUID skillId2) {
        assertEquals("Joey Tribbiani", elem.getName());
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

    private void assertReturnedMember3(TeamMemberSkillDTO elem, UUID skillId2, UUID skillId3) {
        assertNull(elem.getName());
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

    private void assertReturnedMember4(TeamMemberSkillDTO elem, UUID skillId1, UUID skillId2) {
        assertEquals("Ross Geller", elem.getName());
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
