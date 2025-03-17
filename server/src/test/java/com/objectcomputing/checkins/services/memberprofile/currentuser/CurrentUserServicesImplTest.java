package com.objectcomputing.checkins.services.memberprofile.currentuser;

import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.fixture.RoleFixture;
import com.objectcomputing.checkins.services.fixture.MemberProfileFixture;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileRepository;
import com.objectcomputing.checkins.services.role.Role;
import com.objectcomputing.checkins.services.role.RoleServices;
import com.objectcomputing.checkins.services.role.RoleType;
import com.objectcomputing.checkins.services.role.member_roles.MemberRoleServices;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.inject.Inject;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CurrentUserServicesImplTest extends TestContainersSuite
                                  implements MemberProfileFixture, RoleFixture {

    @Inject
    CurrentUserServicesImpl testObject;

    @BeforeEach
    void createRolesAndPermissions() {
        createAndAssignRoles();
    }

    @Test
    void testFindOrSaveUserForNewUser() {
        MemberProfile expected = createADefaultMemberProfile();

        MemberProfile actual = testObject.findOrSaveUser(expected.getFirstName(), expected.getLastName(), expected.getWorkEmail());

        assertEquals(expected, actual);
    }

    @Test
    void testFindOrSaveUserForExistingUser() {
        MemberProfile expected = createADefaultMemberProfile();
        assignMemberRole(expected);

        MemberProfile actual = testObject.findOrSaveUser(expected.getFirstName(), expected.getLastName(), expected.getWorkEmail());

        assertEquals(expected, actual);
    }
}
