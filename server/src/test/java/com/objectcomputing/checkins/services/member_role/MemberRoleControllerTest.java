package com.objectcomputing.checkins.services.member_role;

import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.fixture.MemberProfileFixture;
import com.objectcomputing.checkins.services.fixture.RoleFixture;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.role.Role;
import com.objectcomputing.checkins.services.role.RoleType;
import com.objectcomputing.checkins.services.role.member_roles.MemberRole;
import com.objectcomputing.checkins.services.role.member_roles.MemberRoleId;
import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MemberRoleControllerTest extends TestContainersSuite implements MemberProfileFixture, RoleFixture {

    @Inject
    @Client("/services/roles/members")
    HttpClient client;

    @Test
    void testGetAllAssignedMemberRoles() {

        MemberProfile member = createADefaultMemberProfile();
        MemberProfile admin = createAnUnrelatedUser();

        Role memberRole = createAndAssignRole(RoleType.MEMBER, member);
        Role adminRole = createAndAssignAdminRole(admin);

        final List<MemberRoleId> memberRoleIds = new ArrayList<>();
        memberRoleIds.add(new MemberRoleId(member.getId(), memberRole.getId()));
        memberRoleIds.add(new MemberRoleId(admin.getId(), adminRole.getId()));
        memberRoleIds.add(new MemberRoleId(admin.getId(), memberRole.getId()));

        final HttpRequest<Object> request = HttpRequest.GET("/")
                .basicAuth(admin.getWorkEmail(), RoleType.Constants.ADMIN_ROLE);
        final HttpResponse<List<MemberRole>> response = client.toBlocking()
                .exchange(request, Argument.listOf(MemberRole.class));

        assertEquals(HttpStatus.OK, response.getStatus());
        assertTrue(response.getBody().isPresent());
        for (int i = 0; i < response.getBody().get().size(); i++) {
            assertEquals(memberRoleIds.get(i), response.getBody().get().get(i).getMemberRoleId());
        }
    }

    @Test
    void testDeleteMemberRole() {
        MemberProfile member = createADefaultMemberProfile();
        MemberProfile admin = createAnUnrelatedUser();

        Role memberRole = createAndAssignRole(RoleType.MEMBER, member);
        createAndAssignAdminRole(admin);

        final HttpRequest<Object> request = HttpRequest.DELETE(String.format("/%s/%s", memberRole.getId(), member.getId()))
                .basicAuth(admin.getWorkEmail(), RoleType.Constants.ADMIN_ROLE);
        final HttpResponse<MemberRole> response = client.toBlocking().exchange(request, MemberRole.class);

        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void testSaveMemberRole() {
        MemberProfile member = createADefaultMemberProfile();
        MemberProfile admin = createAnUnrelatedUser();

        createAndAssignRole(RoleType.MEMBER, member);
        Role adminRole = createAndAssignAdminRole(admin);

        MemberRoleId memberRoleId = new MemberRoleId(member.getId(), adminRole.getId());

        final HttpRequest<?> request = HttpRequest.POST("", memberRoleId)
                .basicAuth(admin.getWorkEmail(), RoleType.Constants.ADMIN_ROLE);
        final HttpResponse<MemberRole> response = client.toBlocking().exchange(request, MemberRole.class);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertTrue(response.getBody().isPresent());
        assertEquals(member.getId(), response.getBody().get().getMemberRoleId().getMemberId());
        assertEquals(adminRole.getId(), response.getBody().get().getMemberRoleId().getRoleId());
    }

}
