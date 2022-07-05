package com.objectcomputing.checkins.services.role.member_role;

import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.fixture.MemberProfileFixture;
import com.objectcomputing.checkins.services.fixture.RoleFixture;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.role.Role;
import com.objectcomputing.checkins.services.role.member_roles.MemberRole;
import com.objectcomputing.checkins.services.role.member_roles.MemberRoleId;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import org.junit.jupiter.api.Test;

import jakarta.inject.Inject;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MemberRoleControllerTest extends TestContainersSuite implements MemberProfileFixture, RoleFixture {

    @Inject
    @Client("/services/roles/members")
    HttpClient client;

    @Test
    void testCreateMemberRole() {
        MemberProfile admin = createAnUnrelatedUser();
        Role adminRole = createAndAssignAdminRole(admin);
        MemberProfile member = createADefaultMemberProfile();
        Role role = createRole(new Role("Test Role", "Testing"));

        MemberRoleId memberRoleId = new MemberRoleId(member.getId(), role.getId());

        final HttpRequest<MemberRoleId> request = HttpRequest.POST("", memberRoleId)
                .basicAuth(admin.getWorkEmail(), adminRole.getRole());
        final HttpResponse<MemberRole> response = client.toBlocking().exchange(request, MemberRole.class);

        assertTrue(response.getBody().isPresent());
        assertEquals(memberRoleId, response.getBody().get().getMemberRoleId());
        assertEquals(HttpStatus.OK, response.getStatus());
    }

}
