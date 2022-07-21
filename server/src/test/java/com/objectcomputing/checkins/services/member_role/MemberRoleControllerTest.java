package com.objectcomputing.checkins.services.member_role;

import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.fixture.MemberProfileFixture;
import com.objectcomputing.checkins.services.fixture.RoleFixture;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.role.MemberRoleDTO;
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

        final HttpRequest<Object> request = HttpRequest.GET("/")
                .basicAuth(admin.getWorkEmail(), RoleType.Constants.ADMIN_ROLE);
        final HttpResponse<List<MemberRoleDTO>> response = client.toBlocking()
                .exchange(request, Argument.listOf(MemberRoleDTO.class));

        assertEquals(HttpStatus.OK, response.getStatus());
        assertTrue(response.getBody().isPresent());
        assertEquals(2, response.getBody().get().size());

        final MemberRoleDTO memberGroupResponse = response.getBody().get().get(0);
        assertEquals(memberRole.getId(), memberGroupResponse.getRoleId());
        assertEquals(memberRole.getRole(), memberGroupResponse.getRole());
        assertEquals(memberRole.getDescription(), memberGroupResponse.getDescription());
        assertEquals(2, memberGroupResponse.getMemberIds().size());
        assertTrue(memberGroupResponse.getMemberIds().contains(member.getId()));
        assertTrue(memberGroupResponse.getMemberIds().contains(admin.getId()));

        final MemberRoleDTO adminGroupResponse = response.getBody().get().get(1);
        assertEquals(adminRole.getId(), adminGroupResponse.getRoleId());
        assertEquals(adminRole.getRole(), adminGroupResponse.getRole());
        assertEquals(adminRole.getDescription(), adminGroupResponse.getDescription());
        assertEquals(1, adminGroupResponse.getMemberIds().size());
        assertTrue(adminGroupResponse.getMemberIds().contains(admin.getId()));
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
