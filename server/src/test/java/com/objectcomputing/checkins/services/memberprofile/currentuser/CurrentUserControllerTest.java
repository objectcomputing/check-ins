package com.objectcomputing.checkins.services.memberprofile.currentuser;

import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.test.annotation.MicronautTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

import static com.objectcomputing.checkins.services.memberprofile.MemberProfileTestUtil.mkMemberProfile;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@MicronautTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CurrentUserControllerTest {

    private static Map<String, Object> userAttributes = new HashMap<>();
    private static String userName = "some.user.name";
    private static String userEmail = "some.email.address";
    private static String imageUrl = "some.picture.url";
    @Inject
    CurrentUserController currentUserController;
    @Mock
    CurrentUserServices currentUserServices;

    @BeforeAll
    void setup() {
        userAttributes.put("name", userName);
        userAttributes.put("email", userEmail);
        userAttributes.put("picture", imageUrl);

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
                return userAttributes;
            }

            @Override
            public String getName() {
                return null;
            }
        };

        MemberProfile expected = mkMemberProfile();
        expected.setWorkEmail(userEmail);

        when(currentUserServices.findOrSaveUser(userName, userEmail)).thenReturn(expected);

        HttpResponse<CurrentUserDTO> actual = currentUserController.currentUser(auth);

        assertEquals(HttpStatus.OK, actual.getStatus());
        CurrentUserDTO currentUserDTO = actual.body();
        assertNotNull(currentUserDTO);
        assertEquals(userEmail, currentUserDTO.getMemberProfile().getWorkEmail());
        assertEquals(userName, currentUserDTO.getName());
        assertEquals(imageUrl, currentUserDTO.getImageUrl());
        assertNotNull(actual.getHeaders().get("location"));
    }
}
