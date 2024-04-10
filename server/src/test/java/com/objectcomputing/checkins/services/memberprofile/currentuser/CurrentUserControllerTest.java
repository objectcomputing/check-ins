package com.objectcomputing.checkins.services.memberprofile.currentuser;

import com.objectcomputing.checkins.services.fixture.MemberProfileFixture;
import com.objectcomputing.checkins.services.fixture.RoleFixture;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.role.Role;
import com.objectcomputing.checkins.services.role.RoleType;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.runtime.server.EmbeddedServer;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.validation.constraints.NotNull;
import java.util.*;

import static com.objectcomputing.checkins.services.memberprofile.MemberProfileTestUtil.mkMemberProfile;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@MicronautTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CurrentUserControllerTest implements MemberProfileFixture, RoleFixture {

    private static Map<String, Object> userAttributes = new HashMap<>();
    private static String firstName = "some.first.name";
    private static String lastName = "some.last.name";
    private static String userEmail = "some.email.address";
    private static String imageUrl = "some.picture.url";

    @Mock
    CurrentUserServices currentUserServices;


    @Inject
    CurrentUserController currentUserController;

    @Inject
    EmbeddedServer embeddedServer;

    @BeforeAll
    void setup() {

        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testCurrentUserReturnsUnauthorizedWhenAuthenticationFails() {
        HttpResponse<CurrentUserDTO> response = currentUserController.currentUser(null);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatus());
    }

    @Test
    public void testCurrentUserReturnsValidDTO() {
        Authentication auth = new Authentication() {
            @NotNull
            @Override
            public Map<String, Object> getAttributes() {
                userAttributes.put("name", firstName + ' ' + lastName);
                userAttributes.put("email", userEmail);
                userAttributes.put("picture", imageUrl);
                return userAttributes;
            }

            @Override
            public String getName() {
                return null;
            }
        };

        MemberProfile expected = mkMemberProfile();
        expected.setWorkEmail(userEmail);
        expected.setFirstName(firstName);
        expected.setLastName(lastName);

        when(currentUserServices.findOrSaveUser(firstName, lastName, userEmail)).thenReturn(expected);

        HttpResponse<CurrentUserDTO> actual = currentUserController.currentUser(auth);

        assertEquals(HttpStatus.OK, actual.getStatus());
        CurrentUserDTO currentUserDTO = actual.body();
        assertNotNull(currentUserDTO);
        assertEquals(userEmail, currentUserDTO.getMemberProfile().getWorkEmail());
        assertEquals(firstName, currentUserDTO.getFirstName());
        assertEquals(lastName, currentUserDTO.getLastName());
        assertEquals(imageUrl, currentUserDTO.getImageUrl());
        assertNotNull(actual.getHeaders().get("location"));
    }


    @Test
    public void testCurrentUserReturnsCorrectPermissions() {


        Role memberRole = createRole(new Role(RoleType.MEMBER.name(), "Member Role"));
        setPermissionsForMember(memberRole.getId());
        MemberProfile member = createADefaultMemberProfile();
        assignMemberRole(member);
        Authentication auth = new Authentication() {
            @NotNull
            @Override
            public Map<String, Object> getAttributes() {
                userAttributes.put("name", member.getFirstName() + ' ' + member.getLastName());
                userAttributes.put("email", member.getWorkEmail());
                userAttributes.put("picture", imageUrl);
                return userAttributes;
            }
            @Override
            public String getName() {
                return null;
            }
        };

        HttpResponse<CurrentUserDTO> actual = currentUserController.currentUser(auth);

        assertEquals(HttpStatus.OK, actual.getStatus());
        CurrentUserDTO currentUserDTO = actual.body();
        assertNotNull(currentUserDTO);
        assertEquals(member.getWorkEmail(), currentUserDTO.getMemberProfile().getWorkEmail());
        assertEquals(member.getFirstName(), currentUserDTO.getFirstName());
        assertEquals(member.getLastName(), currentUserDTO.getLastName());
        assertEquals(imageUrl, currentUserDTO.getImageUrl());
        assertEquals(List.of("MEMBER"), currentUserDTO.getRole());
        assertEquals(memberPermissions, currentUserDTO.getPermissions());
        assertNotNull(actual.getHeaders().get("location"));
    }


    @Test
    public void testCurrentUserReturnsCorrectPermissionsAdmn() {


        Role adminRole = createRole(new Role(RoleType.ADMIN.name(), "Member Role"));
        setPermissionsForAdmin(adminRole.getId());
        MemberProfile admin = createADefaultMemberProfile();
        assignAdminRole(admin);
        Authentication auth = new Authentication() {
            @NotNull
            @Override
            public Map<String, Object> getAttributes() {
                userAttributes.put("name", admin.getFirstName() + ' ' + admin.getLastName());
                userAttributes.put("email", admin.getWorkEmail());
                userAttributes.put("picture", imageUrl);
                return userAttributes;
            }

            @Override
            public String getName() {
                return null;
            }
        };

        HttpResponse<CurrentUserDTO> actual = currentUserController.currentUser(auth);

        assertEquals(HttpStatus.OK, actual.getStatus());
        CurrentUserDTO currentUserDTO = actual.body();
        assertNotNull(currentUserDTO);
        assertEquals(admin.getWorkEmail(), currentUserDTO.getMemberProfile().getWorkEmail());
        assertEquals(admin.getFirstName(), currentUserDTO.getFirstName());
        assertEquals(admin.getLastName(), currentUserDTO.getLastName());
        assertEquals(imageUrl, currentUserDTO.getImageUrl());
        assertEquals(List.of("ADMIN"), currentUserDTO.getRole());
        assertEquals(adminPermissions, currentUserDTO.getPermissions());
        assertNotNull(actual.getHeaders().get("location"));
    }


    @Override
    public EmbeddedServer getEmbeddedServer() {
        return embeddedServer;
    }
}
