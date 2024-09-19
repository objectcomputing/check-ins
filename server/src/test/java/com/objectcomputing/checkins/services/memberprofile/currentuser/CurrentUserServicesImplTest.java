package com.objectcomputing.checkins.services.memberprofile.currentuser;

import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileRepository;
import com.objectcomputing.checkins.services.role.Role;
import com.objectcomputing.checkins.services.role.RoleServices;
import com.objectcomputing.checkins.services.role.RoleType;
import com.objectcomputing.checkins.services.role.member_roles.MemberRoleServices;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledInNativeImage;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.UUID;

import static com.objectcomputing.checkins.services.memberprofile.MemberProfileTestUtil.mkMemberProfile;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

// Disabled in nativeTest, as we get an exception from Mockito
//    => java.lang.NoClassDefFoundError: Could not initialize class org.mockito.internal.configuration.plugins.Plugins
@DisabledInNativeImage
class CurrentUserServicesImplTest extends TestContainersSuite {

    @Mock
    MemberProfileRepository memberProfileRepo;

    @Mock
    RoleServices roleServices;

    @Mock
    MemberRoleServices memberRoleServices;

    @InjectMocks
    CurrentUserServicesImpl testObject;

    private AutoCloseable mockFinalizer;

    @BeforeAll
    public void before() {
        mockFinalizer = MockitoAnnotations.openMocks(this);
    }

    @AfterAll
    public void after() throws Exception {
        mockFinalizer.close();
    }

    @Test
    void testFindOrSaveUserForNewUser() {
        MemberProfile expected = mkMemberProfile();
        expected.setWorkEmail("test.email");

        when(memberProfileRepo.findByWorkEmail(expected.getWorkEmail())).thenReturn(java.util.Optional.of(expected));

        MemberProfile actual = testObject.findOrSaveUser(expected.getFirstName(), expected.getLastName(), expected.getWorkEmail());

        assertEquals(expected, actual);
    }

    @Test
    void testFindOrSaveUserForExistingUser() {
        MemberProfile expected = mkMemberProfile();
        expected.setId(UUID.randomUUID());
        expected.setWorkEmail("test.email");
        Role mockRole = new Role(RoleType.MEMBER.name(), "role description");

        when(memberProfileRepo.findByWorkEmail(expected.getWorkEmail())).thenReturn(java.util.Optional.empty());
        when(memberProfileRepo.save(any())).thenReturn(expected);
        when(roleServices.save(mockRole)).thenReturn(mockRole);

        MemberProfile actual = testObject.findOrSaveUser(expected.getFirstName(), expected.getLastName(), expected.getWorkEmail());

        assertEquals(expected, actual);
        verify(roleServices, times(1)).save(any(Role.class));
    }
}
