package com.objectcomputing.checkins.services.memberprofile.csvreport;

import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.fixture.MemberProfileFixture;
import com.objectcomputing.checkins.services.fixture.RoleFixture;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileUtils;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.objectcomputing.checkins.services.role.RoleType.Constants.ADMIN_ROLE;
import static com.objectcomputing.checkins.services.role.RoleType.Constants.MEMBER_ROLE;
import static org.junit.jupiter.api.Assertions.*;

public class MemberProfileReportControllerTest extends TestContainersSuite implements MemberProfileFixture, RoleFixture {
    @Inject
    @Client("/services/reports/member")
    private HttpClient client;

    @BeforeEach
    void createRolesAndPermissions() {
        createAndAssignRoles();
    }

    @Test
    public void testGetReportWithAllMemberProfiles() {
        MemberProfile member1 = createADefaultMemberProfile();
        MemberProfile member2 = createASecondDefaultMemberProfile();
        MemberProfile member3 = createADefaultMemberProfileForPdl(member1);
        createAProfileWithSupervisorAndPDL(member2, member3);

        HttpRequest<?> request = HttpRequest
                .POST("/", null)
                .basicAuth(ADMIN_ROLE, ADMIN_ROLE);
        HttpResponse<File> response = client.toBlocking().exchange(request, File.class);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertTrue(response.getBody().isPresent());
    }

    @Test
    public void testGetReportWithSelectedMemberProfiles() {
        MemberProfile member1 = createADefaultMemberProfile();
        MemberProfile member2 = createASecondDefaultMemberProfile();
        MemberProfile member3 = createADefaultMemberProfileForPdl(member1);
        createAProfileWithSupervisorAndPDL(member2, member3);

        MemberProfileReportQueryDTO dto = new MemberProfileReportQueryDTO();
        dto.setMemberIds(List.of(member1.getId(), member3.getId()));

        HttpRequest<?> request = HttpRequest
                .POST("/", dto)
                .basicAuth(ADMIN_ROLE, ADMIN_ROLE);
        HttpResponse<File> response = client.toBlocking().exchange(request, File.class);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertTrue(response.getBody().isPresent());
    }

    @Test
    public void testGetReportNotAuthorized() {
        HttpRequest<?> request = HttpRequest
                .POST("/", null)
                .basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, File.class));

        assertEquals(HttpStatus.FORBIDDEN, responseException.getStatus());
    }

    @Test
    public void testGetAllMemberProfileRecords() {
        MemberProfile member1 = createADefaultMemberProfile();
        MemberProfile member2 = createASecondDefaultMemberProfile();
        MemberProfile member3 = createADefaultMemberProfileForPdl(member1);
        MemberProfile member4 = createAProfileWithSupervisorAndPDL(member2, member3);

        List<MemberProfileRecord> records = getMemberProfileReportRepository().findAll();

        assertEquals(4, records.size());
        assertMemberProfileMatchesRecord(member1, records.get(0));
        assertMemberProfileMatchesRecord(member2, records.get(1));
        assertMemberProfileMatchesRecord(member3, records.get(2));
        assertMemberProfileMatchesRecord(member4, records.get(3));
    }

    @Test
    public void testGetSelectedMemberProfileRecords() {
        MemberProfile member1 = createADefaultMemberProfile();
        MemberProfile member2 = createASecondDefaultMemberProfile();
        MemberProfile member3 = createADefaultMemberProfileForPdl(member1);
        MemberProfile member4 = createAProfileWithSupervisorAndPDL(member2, member3);

        List<String> selectedMemberIds = new ArrayList<>();
        selectedMemberIds.add(member2.getId().toString());
        selectedMemberIds.add(member4.getId().toString());
        List<MemberProfileRecord> records = getMemberProfileReportRepository().findAllByMemberIds(selectedMemberIds, key);

        assertEquals(2, records.size());
        assertMemberProfileMatchesRecord(member4, records.get(0));
        assertMemberProfileMatchesRecord(member2, records.get(1));
    }

    private void assertMemberProfileMatchesRecord(MemberProfile memberProfile, MemberProfileRecord record) {
        assertEquals(memberProfile.getFirstName(), record.getFirstName());
        assertEquals(memberProfile.getLastName(), record.getLastName());
        assertEquals(memberProfile.getTitle(), record.getTitle());
        assertEquals(memberProfile.getLocation(), record.getLocation());
        assertEquals(memberProfile.getWorkEmail(), record.getWorkEmail());
        assertEquals(memberProfile.getStartDate(), record.getStartDate());
        assertNotNull(record.getTenure());

        if (memberProfile.getPdlId() == null) {
            assertNull(record.getPdlName());
            assertNull(record.getPdlEmail());
        } else {
            MemberProfile pdl = getMemberProfileRepository().findById(memberProfile.getPdlId()).orElseThrow();
            assertEquals(MemberProfileUtils.getFullName(pdl), record.getPdlName());
            assertEquals(pdl.getWorkEmail(), record.getPdlEmail());
        }

        if (memberProfile.getSupervisorid() == null) {
            assertNull(record.getSupervisorName());
            assertNull(record.getSupervisorEmail());
        } else {
            MemberProfile supervisor = getMemberProfileRepository().findById(memberProfile.getSupervisorid()).orElseThrow();
            assertEquals(MemberProfileUtils.getFullName(supervisor), record.getSupervisorName());
            assertEquals(supervisor.getWorkEmail(), record.getSupervisorEmail());
        }
    }

}
