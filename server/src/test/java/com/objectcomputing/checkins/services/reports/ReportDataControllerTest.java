package com.objectcomputing.checkins.services.reports;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.fixture.RoleFixture;
import com.objectcomputing.checkins.services.fixture.MemberProfileFixture;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;

import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MutableHttpRequest;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.http.client.multipart.MultipartBody;

import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.objectcomputing.checkins.services.role.RoleType.Constants.ADMIN_ROLE;
import static com.objectcomputing.checkins.services.role.RoleType.Constants.MEMBER_ROLE;
import static io.micronaut.http.MediaType.MULTIPART_FORM_DATA;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ReportDataControllerTest extends TestContainersSuite implements MemberProfileFixture, RoleFixture {

  @Inject
  @Client("/services/report/data")
  HttpClient client;

  private MemberProfile regular;
  private MemberProfile admin;
  private final String basePath = "src/test/java/com/objectcomputing/checkins/services/reports/";

  @BeforeEach
  void createRolesAndPermissions() {
    createAndAssignRoles();
    regular = createADefaultMemberProfile();
    admin = createAThirdDefaultMemberProfile();
    assignAdminRole(admin);
  }

  @Test
  void uploadReportData() {
    final HttpRequest<?> request = postData(admin, ADMIN_ROLE);
    final String response = client.toBlocking().retrieve(request);
    assertNotNull(response);
  }

  @Test
  void uploadReportDataWithoutPermission() {
    final HttpRequest<?> request = postData(regular, MEMBER_ROLE);
    HttpClientResponseException responseException =
      assertThrows(HttpClientResponseException.class,
                   () -> client.toBlocking().retrieve(request));
  }

  @Test
  void getReportData() throws JsonProcessingException {
    HttpRequest<?> request = postData(admin, ADMIN_ROLE);
    final String response = client.toBlocking().retrieve(request);
    assertNotNull(response);

    request = HttpRequest.GET(
          String.format("/?memberIds=%s&reviewPeriodId=%s",
                        regular.getId(),
                        "12345678-e29c-4cf4-9ea4-6baa09405c57"))
                .basicAuth(admin.getWorkEmail(), ADMIN_ROLE);
    final String data = client.toBlocking().retrieve(request);
    ObjectMapper objectMapper = new ObjectMapper();
    assertNotNull(data);

    // Perform minimal validation of returned data
    JsonNode root = objectMapper.readTree(data);
    assertEquals(root.isArray(), true);
    assertEquals(root.isEmpty(), false);

    ArrayNode arrayNode = (ArrayNode)root;
    JsonNode first = arrayNode.get(0);
    assertNotNull(first.get("memberProfile"));
    assertNotNull(first.get("kudos"));
    assertNotNull(first.get("compensationHistory"));
    assertNotNull(first.get("currentInformation"));
    assertNotNull(first.get("positionHistory"));
    assertNotNull(first.get("selfReviews"));
    assertNotNull(first.get("reviews"));
    assertNotNull(first.get("feedback"));
    assertNotNull(first.get("hours"));
  }

  @Test
  void getReportDataWithoutPermission() {
    final HttpRequest<?> request = HttpRequest.GET(
          String.format("/?memberIds=%s&reviewPeriodId=%s",
                        regular.getId(),
                        "12345678-e29c-4cf4-9ea4-6baa09405c57"))
                .basicAuth(regular.getWorkEmail(), MEMBER_ROLE);
    HttpClientResponseException responseException =
      assertThrows(HttpClientResponseException.class,
                   () -> client.toBlocking().retrieve(request));
  }

  HttpRequest<?> postData(MemberProfile user, String role) {
    File compFile = new File(basePath + "data/compensationHistory.csv");
    File currFile = new File(basePath + "data/currentInformation.csv");
    File posFile  = new File(basePath + "data/positionHistory.csv");

    return HttpRequest.POST("/upload",
                MultipartBody.builder()
                .addPart("comp", compFile)
                .addPart("curr", currFile)
                .addPart("pos", posFile)
                .build())
                .basicAuth(user.getWorkEmail(), role)
                .contentType(MULTIPART_FORM_DATA);
  }
}
