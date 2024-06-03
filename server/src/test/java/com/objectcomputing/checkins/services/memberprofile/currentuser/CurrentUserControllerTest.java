package com.objectcomputing.checkins.services.memberprofile.currentuser;

import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.fixture.MemberProfileFixture;
import com.objectcomputing.checkins.services.fixture.RoleFixture;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.role.Role;
import com.objectcomputing.checkins.services.role.RoleType;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.runtime.server.EmbeddedServer;
import io.micronaut.security.authentication.Authentication;
import jakarta.inject.Inject;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.objectcomputing.checkins.services.memberprofile.MemberProfileTestUtil.mkMemberProfile;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

class CurrentUserControllerTest extends TestContainersSuite implements MemberProfileFixture, RoleFixture {

    private static final Map<String, Object> userAttributes = new HashMap<>();
    private static final String firstName = "some.first.name";
    private static final String lastName = "some.last.name";
    private static final String userEmail = "some.email.address";
    private static final String imageUrl = "some.picture.url";

    @Mock
    CurrentUserServices currentUserServices;

    @Inject
    CurrentUserController currentUserController;

    @Inject
    EmbeddedServer embeddedServer;
    private AutoCloseable mockFinalizer;

    @BeforeAll
    void setupMocks() {
        mockFinalizer = MockitoAnnotations.openMocks(this);
    }

    @AfterAll
    void close() throws Exception {
        mockFinalizer.close();
    }

    @Test
    void testCurrentUserReturnsUnauthorizedWhenAuthenticationFails() {
        HttpResponse<CurrentUserDTO> response = currentUserController.currentUser(null);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatus());
    }

    @Test
    void testCurrentUserReturnsValidDTO() {
        Authentication auth = new Authentication() {
            @NonNull
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
    void testCurrentUserReturnsCorrectPermissions() {
        Role memberRole = createRole(new Role(RoleType.MEMBER.name(), "Member Role"));
        setPermissionsForMember(memberRole.getId());
        MemberProfile member = createADefaultMemberProfile();
        assignMemberRole(member);
        Authentication auth = new Authentication() {
            @NonNull
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
    void testCurrentUserReturnsCorrectPermissionsAdmin() {
        Role adminRole = createRole(new Role(RoleType.ADMIN.name(), "Member Role"));
        setPermissionsForAdmin(adminRole.getId());
        MemberProfile admin = createADefaultMemberProfile();
        assignAdminRole(admin);
        Authentication auth = new Authentication() {
            @NonNull
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
