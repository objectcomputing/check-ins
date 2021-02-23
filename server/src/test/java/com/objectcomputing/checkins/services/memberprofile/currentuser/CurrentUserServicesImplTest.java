package com.objectcomputing.checkins.services.memberprofile.currentuser;

import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileRepository;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileServicesImpl;
import com.objectcomputing.checkins.services.role.Role;
import com.objectcomputing.checkins.services.role.RoleServices;
import com.objectcomputing.checkins.services.role.RoleType;
import io.micronaut.test.annotation.MicronautTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.UUID;

import static com.objectcomputing.checkins.services.memberprofile.MemberProfileTestUtil.mkMemberProfile;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@MicronautTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CurrentUserServicesImplTest {

    @Mock
    MemberProfileRepository memberProfileRepo;

    @Mock
    RoleServices roleServices;

    @InjectMocks
    CurrentUserServicesImpl testObject;

    @BeforeAll
    public void before() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testFindOrSaveUserForNewUser() {
        MemberProfile expected = mkMemberProfile();
        expected.setWorkEmail("test.email");

        when(memberProfileRepo.findByWorkEmail(expected.getWorkEmail())).thenReturn(java.util.Optional.of(expected));

        MemberProfile actual = testObject.findOrSaveUser(expected.getName(), expected.getWorkEmail());

        assertEquals(expected, actual);
    }

    @Test
    public void testFindOrSaveUserForExistingUser() {
        MemberProfile expected = mkMemberProfile();
        expected.setId(UUID.randomUUID());
        expected.setWorkEmail("test.email");
        Role mockRole = new Role(RoleType.MEMBER, expected.getId());

        when(memberProfileRepo.findByWorkEmail(expected.getWorkEmail())).thenReturn(java.util.Optional.empty());
        when(memberProfileRepo.save(any())).thenReturn(expected);
        when(roleServices.save(mockRole)).thenReturn(mockRole);

        MemberProfile actual = testObject.findOrSaveUser(expected.getName(), expected.getWorkEmail());

        assertEquals(expected, actual);
        verify(roleServices, times(1)).save(any(Role.class));
    }
}
