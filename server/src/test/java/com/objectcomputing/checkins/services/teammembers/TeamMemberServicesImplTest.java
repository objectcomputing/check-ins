package com.objectcomputing.checkins.services.teammembers;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.test.annotation.MicronautTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static com.objectcomputing.checkins.services.role.RoleType.Constants.MEMBER_ROLE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@MicronautTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TeamMemberServicesImplTest {

    private static UUID testTeamMemberId = UUID.fromString("44bd3ea6-ade7-428d-9630-e7e701f67d85");
    private static UUID testTeamId = UUID.fromString("cdcd949c-d2cb-4f6a-b13b-08ed4836c608");
    private static UUID testUuid = UUID.fromString("73e754f8-ae11-4467-acfc-20da64295d5b");
    private static boolean isLead = false;

    @Mock
    private TeamMemberRepository mockTeamMemberRepository;

    @InjectMocks
    private TeamMemberServicesImpl testObject;

    @BeforeAll
    void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetAll() {
        when(mockTeamMemberRepository.findAll())
                .thenReturn(Collections.singletonList(new TeamMember(testTeamId, testTeamMemberId, testUuid, isLead)));

        List<TeamMember> result = testObject.findByTeamAndMember(null, null);

        assertEquals(1, result.size());
        assertEquals(testTeamId, result.get(0).getTeamId());
        assertEquals(testTeamMemberId, result.get(0).getMemberId());
        assertEquals(testUuid, result.get(0).getUuid());
        assertEquals(isLead, result.get(0).getIsLead());
    }

    @Test
    public void testGetByTeamId() {
        when(mockTeamMemberRepository.findByTeamId(testTeamId))
                .thenReturn(Collections.singletonList(new TeamMember(testTeamId, testTeamMemberId, testUuid, isLead)));

        List<TeamMember> result = testObject.findByTeamAndMember(testTeamId, null);

        assertEquals(1, result.size());
        assertEquals(testTeamId, result.get(0).getTeamId());
        assertEquals(testTeamMemberId, result.get(0).getMemberId());
        assertEquals(testUuid, result.get(0).getUuid());
        assertEquals(isLead, result.get(0).getIsLead());
    }

    @Test
    public void testGetByMemberId() {
        when(mockTeamMemberRepository.findByMemberId(testTeamMemberId))
                .thenReturn(Collections.singletonList(new TeamMember(testTeamId, testTeamMemberId, testUuid, isLead)));

        List<TeamMember> result = testObject.findByTeamAndMember(null, testTeamMemberId);

        assertEquals(1, result.size());
        assertEquals(testTeamId, result.get(0).getTeamId());
        assertEquals(testTeamMemberId, result.get(0).getMemberId());
        assertEquals(testUuid, result.get(0).getUuid());
        assertEquals(isLead, result.get(0).getIsLead());
    }

    @Test
    public void testGetByBothIds() {
        when(mockTeamMemberRepository.findByTeamIdAndMemberId(testTeamId, testTeamMemberId))
                .thenReturn(Collections.singletonList(new TeamMember(testTeamId, testTeamMemberId, testUuid, isLead)));

        List<TeamMember> result = testObject.findByTeamAndMember(testTeamId, testTeamMemberId);

        assertEquals(1, result.size());
        assertEquals(testTeamId, result.get(0).getTeamId());
        assertEquals(testTeamMemberId, result.get(0).getMemberId());
        assertEquals(testUuid, result.get(0).getUuid());
        assertEquals(isLead, result.get(0).getIsLead());
    }

    @Test
    public void testSave() {
        TeamMember saveMe = new TeamMember(testTeamId, testTeamMemberId, null, isLead);
        TeamMember postSave = new TeamMember(testTeamId, testTeamMemberId, testUuid, isLead);
        when(mockTeamMemberRepository.save(saveMe))
                .thenReturn(postSave);

        TeamMember result = testObject.saveTeamMember(saveMe);

        assertEquals(testTeamId, result.getTeamId());
        assertEquals(testTeamMemberId, result.getMemberId());
        assertEquals(testUuid, result.getUuid());
        assertEquals(isLead, result.getIsLead());
    }

    @Test
    public void testUpdate() {
        TeamMember saveMe = new TeamMember(testTeamId, testTeamMemberId, testUuid, isLead);
        TeamMember postSave = new TeamMember(testTeamId, testTeamMemberId, testUuid, isLead);

        when(mockTeamMemberRepository.save(saveMe))
                .thenReturn(postSave);

        TeamMember result = testObject.saveTeamMember(saveMe);

        assertEquals(testTeamId, result.getTeamId());
        assertEquals(testTeamMemberId, result.getMemberId());
        assertEquals(testUuid, result.getUuid());
        assertEquals(isLead, result.getIsLead());
    }

}
