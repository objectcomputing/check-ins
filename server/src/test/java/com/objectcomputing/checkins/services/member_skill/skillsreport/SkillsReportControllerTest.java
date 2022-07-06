package com.objectcomputing.checkins.services.member_skill.skillsreport;

import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.fixture.MemberProfileFixture;
import com.objectcomputing.checkins.services.fixture.MemberSkillFixture;
import com.objectcomputing.checkins.services.fixture.SkillFixture;
import com.objectcomputing.checkins.services.member_skill.MemberSkill;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.skills.Skill;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import org.junit.jupiter.api.Test;

import jakarta.inject.Inject;
import java.time.LocalDate;
import java.util.*;

import static com.objectcomputing.checkins.services.role.RoleType.Constants.MEMBER_ROLE;
import static org.junit.jupiter.api.Assertions.*;

public class SkillsReportControllerTest extends TestContainersSuite
        implements MemberSkillFixture, MemberProfileFixture, SkillFixture {

    @Inject
    @Client("/reports/skills")
    HttpClient client;

    @Test
    void testValidRequestNonEmptyResponse() {
        final MemberProfile memberProfile = createADefaultMemberProfile();
        final Skill skill = createADefaultSkill();
        final MemberSkill memberSkill = createMemberSkill(memberProfile, skill, SkillLevel.ADVANCED_LEVEL, LocalDate.now());

        final SkillsReportRequestDTO skillsReportRequestDTO = new SkillsReportRequestDTO();
        final List<SkillLevelDTO> skillLevelDTOList = new ArrayList<>();
        final SkillLevelDTO skillLevelDTO = new SkillLevelDTO();
        skillLevelDTO.setId(skill.getId());
        skillLevelDTO.setLevel(SkillLevel.INTERMEDIATE);
        skillLevelDTOList.add(skillLevelDTO);
        skillsReportRequestDTO.setSkills(skillLevelDTOList);

        final HttpRequest<SkillsReportRequestDTO> request = HttpRequest.POST("", skillsReportRequestDTO)
                .basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        final HttpResponse<SkillsReportResponseDTO> response = client.toBlocking()
                .exchange(request, SkillsReportResponseDTO.class);

        final SkillsReportResponseDTO skillsReportResponseDTO = response.body();

        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertEquals(String.format("%s", request.getPath()), response.getHeaders().get("Location"));
        assertEquals(1, skillsReportResponseDTO.getTeamMembers().size());
        assertEquals(1, skillsReportResponseDTO.getTeamMembers().get(0).getSkills().size());
        assertEquals(skill.getId(), skillsReportResponseDTO.getTeamMembers().get(0).getSkills().get(0).getId());
        assertEquals(SkillLevel.convertFromString(memberSkill.getSkilllevel()),
                skillsReportResponseDTO.getTeamMembers().get(0).getSkills().get(0).getLevel());
        assertEquals(memberProfile.getId(), skillsReportResponseDTO.getTeamMembers().get(0).getId());
    }

    @Test
    void testValidRequestEmptyResponse() {
        final MemberProfile memberProfile = createADefaultMemberProfile();
        final Skill skill = createADefaultSkill();
        createMemberSkill(memberProfile, skill, SkillLevel.INTERMEDIATE_LEVEL, null);

        final SkillsReportRequestDTO skillsReportRequestDTO = new SkillsReportRequestDTO();
        final List<SkillLevelDTO> skillLevelDTOList = new ArrayList<>();
        final SkillLevelDTO skillLevelDTO = new SkillLevelDTO();
        skillLevelDTO.setId(skill.getId());
        skillLevelDTO.setLevel(SkillLevel.EXPERT);
        skillLevelDTOList.add(skillLevelDTO);
        skillsReportRequestDTO.setSkills(skillLevelDTOList);

        final HttpRequest<SkillsReportRequestDTO> request = HttpRequest.POST("", skillsReportRequestDTO)
                .basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        final HttpResponse<SkillsReportResponseDTO> response = client.toBlocking()
                .exchange(request, SkillsReportResponseDTO.class);

        final SkillsReportResponseDTO skillsReportResponseDTO = response.body();
        assertNotNull(skillsReportResponseDTO);

        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertEquals(String.format("%s", request.getPath()), response.getHeaders().get("Location"));
    }

    @Test
    void testInvalidRequestSkillNotExist() {
        final SkillsReportRequestDTO skillsReportRequestDTO = new SkillsReportRequestDTO();
        final List<SkillLevelDTO> skillLevelDTOList = new ArrayList<>();
        final SkillLevelDTO skillLevelDTO = new SkillLevelDTO();
        final UUID skillId = UUID.randomUUID();
        skillLevelDTO.setId(skillId);
        skillLevelDTO.setLevel(SkillLevel.ADVANCED);
        skillLevelDTOList.add(skillLevelDTO);
        skillsReportRequestDTO.setSkills(skillLevelDTOList);

        final HttpRequest<SkillsReportRequestDTO> request = HttpRequest.POST("", skillsReportRequestDTO)
                .basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
    }

    @Test
    void testInvalidRequestMemberProfileNotExist() {
        final Skill skill = createADefaultSkill();

        final SkillsReportRequestDTO skillsReportRequestDTO = new SkillsReportRequestDTO();
        final List<SkillLevelDTO> skillLevelDTOList = new ArrayList<>();
        final SkillLevelDTO skillLevelDTO = new SkillLevelDTO();
        skillLevelDTO.setId(skill.getId());
        skillLevelDTOList.add(skillLevelDTO);
        skillsReportRequestDTO.setSkills(skillLevelDTOList);
        final Set<UUID> members = new HashSet<>();
        final UUID memberId = UUID.randomUUID();
        members.add(memberId);
        skillsReportRequestDTO.setMembers(members);

        final HttpRequest<SkillsReportRequestDTO> request = HttpRequest.POST("", skillsReportRequestDTO)
                .basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
    }
}
