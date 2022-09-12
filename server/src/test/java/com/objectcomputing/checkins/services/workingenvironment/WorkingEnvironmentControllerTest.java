package com.objectcomputing.checkins.services.workingenvironment;

import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.fixture.*;

import static com.objectcomputing.checkins.services.role.RoleType.Constants.HR_ROLE;
import static com.objectcomputing.checkins.services.role.RoleType.Constants.MEMBER_ROLE;

import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.onboard.workingenvironment.WorkingEnvironment;
import com.objectcomputing.checkins.services.onboard.workingenvironment.WorkingEnvironmentController;
import com.objectcomputing.checkins.services.onboard.workingenvironment.WorkingEnvironmentCreateDTO;
import com.objectcomputing.checkins.services.onboard.workingenvironment.WorkingEnvironmentDTO;
import com.objectcomputing.checkins.services.onboardeecreate.newhire.model.NewHireAccountEntity;
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
import reactor.core.publisher.Mono;


import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.*;

import static com.objectcomputing.checkins.services.workingenvironment.WorkingEnvironmentTestUtil.mkUpdateWorkingEnvironment;
import static com.objectcomputing.checkins.services.workingenvironment.WorkingEnvironmentTestUtil.toDto;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;

public class WorkingEnvironmentControllerTest extends TestContainersSuite
        implements WorkingEnvironmentFixture, RoleFixture, MemberProfileFixture, NewHireAccountFixture {
    private static final Logger LOG = LoggerFactory.getLogger(WorkingEnvironmentController.class);

    @Inject
    @Client("/services/working-environment")
    private HttpClient client;

     @Test
     public void testGETById() {
         NewHireAccountEntity newHire = createNewHireAccountEntity();
         WorkingEnvironment workingEnvironment = createWorkingEnvironment(newHire);

         final HttpRequest<Object> request = HttpRequest.GET(String.format("%s", workingEnvironment.getId()))
                 .basicAuth(HR_ROLE, HR_ROLE);

         final HttpResponse<WorkingEnvironmentDTO> response = client.toBlocking().exchange(request,
                 WorkingEnvironmentDTO.class);

         assertEquals(toDto(workingEnvironment), response.body());
         assertEquals(HttpStatus.OK, response.getStatus());
     }

//     @Test
//     public void testPOSTCreateANullEnvironment() {
//         WorkingEnvironmentCreateDTO workingEnvironmentCreateDTO = new WorkingEnvironmentCreateDTO();
//
//         final HttpRequest<WorkingEnvironmentCreateDTO> request = HttpRequest.POST("/", workingEnvironmentCreateDTO)
//                 .basicAuth(MEMBER_ROLE, MEMBER_ROLE);
//
//         HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
//                 () -> client.toBlocking().exchange(request, Map.class));
//
//         assertNotNull(responseException.getResponse());
//         assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
//     }
//
//     @Test
//     public void testPOSTCreateWorkingEnvironment() {
//         WorkingEnvironmentDTO dto = mkUpdateWorkingEnvironment();
//
//         final HttpRequest<?> request = HttpRequest.POST("/", dto).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
//         final HttpResponse<WorkingEnvironment> response = client.toBlocking().exchange(request,
//                 WorkingEnvironment.class);
//
//         assertNotNull(response);
//         assertEquals(HttpStatus.CREATED, response.getStatus());
//         assertEquals(dto.getKeyType(), response.body().getKeyType());
//         assertEquals(String.format("%s/%s", request.getPath(), response.body().getId()),
//                 "/services" + response.getHeaders().get("location"));
//     }
//
//     @Test
//     public void testPUTUpdateWorkingEnvironment() {
//         WorkingEnvironment workingEnvironment = createWorkingEnvironment();
//
//         WorkingEnvironmentDTO workingEnvironmentResponseDTO = toDto(workingEnvironment);
//
//         workingEnvironmentResponseDTO.setOsType("Linux");
//
//         final HttpRequest<WorkingEnvironmentDTO> request = HttpRequest.PUT("/", workingEnvironmentResponseDTO)
//                 .basicAuth(MEMBER_ROLE, MEMBER_ROLE);
//         final HttpResponse<WorkingEnvironmentDTO> response = client.toBlocking().exchange(request,
//                 WorkingEnvironmentDTO.class);
//
//         assertEquals(workingEnvironmentResponseDTO, response.body());
//         assertEquals(HttpStatus.OK, response.getStatus());
//         assertNotEquals(workingEnvironment.getOsType(), response.body().getOsType());
//         assertEquals(String.format("%s/%s", request.getPath(), workingEnvironmentResponseDTO.getId()),
//                 "/services" + response.getHeaders().get("location"));
//     }


}
