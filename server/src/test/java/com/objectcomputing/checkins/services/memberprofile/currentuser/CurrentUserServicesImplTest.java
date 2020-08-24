package com.objectcomputing.checkins.services.memberprofile.currentuser;

import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileRepository;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileServicesImpl;
import io.micronaut.test.annotation.MicronautTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static com.objectcomputing.checkins.services.memberprofile.MemberProfileTestUtil.mkMemberProfile;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@MicronautTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CurrentUserServicesImplTest {

    @Mock
    MemberProfileServicesImpl memberProfileServicesImpl;

    @Mock
    MemberProfileRepository memberProfileRepo;

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
        expected.setWorkEmail("test.email");

        when(memberProfileRepo.findByWorkEmail(expected.getWorkEmail())).thenReturn(java.util.Optional.empty());
        when(memberProfileServicesImpl.saveProfile(any())).thenReturn(expected);

        MemberProfile actual = testObject.findOrSaveUser(expected.getName(), expected.getWorkEmail());

        assertEquals(expected, actual);
    }
}
