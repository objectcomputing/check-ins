package com.objectcomputing.checkins.services.background_information;

import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.fixture.BackgroundInformationFixture;
import com.objectcomputing.checkins.services.fixture.RoleFixture;
import com.objectcomputing.checkins.services.onboard.background_information.BackgroundInformation;
import com.objectcomputing.checkins.services.onboard.background_information.BackgroundInformationDTO;
import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.objectcomputing.checkins.services.role.RoleType.Constants.ADMIN_ROLE;
import static com.objectcomputing.checkins.services.role.RoleType.Constants.MEMBER_ROLE;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


public class BackgroundInformationControllerTest extends TestContainersSuite implements BackgroundInformationFixture, RoleFixture {
    private static final Logger LOG = LoggerFactory.getLogger(BackgroundInformationControllerTest.class);

    @Inject
    @Client("/services/background-information")
    private HttpClient client;

//    @Test
//    public void testGETAllBackgroundInformation() {
//
//        createDefaultBackgroundInformation();
//        createSecondBackgroundInformation();
//
//        final HttpRequest<Object> request = HttpRequest.
//                GET("/").basicAuth(ADMIN_ROLE, ADMIN_ROLE);
//        final HttpResponse<List<BackgroundInformationDTO>> response = client.toBlocking().exchange(request, Argument.listOf(BackgroundInformationDTO.class));
//        final List<BackgroundInformationDTO> results = response.body();
//        assertEquals(HttpStatus.OK, response.getStatus());
//        assertEquals(2, results.size());
//    }

    @Test
    public void testGETGetByIdNotFound() {

        final HttpRequest<Object> request = HttpRequest.
                GET(String.format("/%s", UUID.randomUUID().toString())).basicAuth(MEMBER_ROLE, MEMBER_ROLE);

        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        assertNotNull(responseException.getResponse());
        assertEquals(HttpStatus.NOT_FOUND, responseException.getStatus());
    }

//    @Test
//    public void testPOSTCreateBackgroundInformation(){
//
//        BackgroundInformationDTO dto = new BackgroundInformationDTO();
//        dto.setUserId(UUID.randomUUID());
//        dto.setStepComplete(false);
//
//        final HttpRequest<?> request = HttpRequest
//                .POST("/", dto).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
//        final HttpResponse<BackgroundInformation> response = client.toBlocking().exchange(request, BackgroundInformation.class);
//
//        assertNotNull(response);
//        assertEquals(HttpStatus.CREATED, response.getStatus());
//        assertEquals(dto.getUserId(), response.body().getUserId());
//        assertEquals(dto.getStepComplete(), response.body().getStepComplete());
//        assertEquals(String.format("%s/%s", request.getPath(), response.body().getId()), "/services" + response.getHeaders().get("location"));
//    }
}
