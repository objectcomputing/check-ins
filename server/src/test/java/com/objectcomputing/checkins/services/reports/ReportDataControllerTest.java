package com.objectcomputing.checkins.services.reports;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.objectcomputing.checkins.services.kudos.Kudos;
import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.fixture.KudosFixture;
import com.objectcomputing.checkins.services.fixture.RoleFixture;
import com.objectcomputing.checkins.services.fixture.MemberProfileFixture;
import com.objectcomputing.checkins.services.fixture.EmployeeHoursFixture;
import com.objectcomputing.checkins.services.fixture.ReviewPeriodFixture;
import com.objectcomputing.checkins.services.fixture.FeedbackTemplateFixture;
import com.objectcomputing.checkins.services.fixture.FeedbackRequestFixture;
import com.objectcomputing.checkins.services.fixture.TemplateQuestionFixture;
import com.objectcomputing.checkins.services.fixture.FeedbackAnswerFixture;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.reviews.ReviewPeriod;
import com.objectcomputing.checkins.services.feedback_template.FeedbackTemplate;
import com.objectcomputing.checkins.services.feedback_request.FeedbackRequest;
import com.objectcomputing.checkins.services.feedback_template.template_question.TemplateQuestion;
import com.objectcomputing.checkins.services.employee_hours.EmployeeHours;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.http.client.multipart.MultipartBody;

import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.time.LocalDate;

import static com.objectcomputing.checkins.services.role.RoleType.Constants.ADMIN_ROLE;
import static com.objectcomputing.checkins.services.role.RoleType.Constants.MEMBER_ROLE;
import static io.micronaut.http.MediaType.MULTIPART_FORM_DATA;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ReportDataControllerTest extends TestContainersSuite implements MemberProfileFixture, RoleFixture, KudosFixture, ReviewPeriodFixture, FeedbackTemplateFixture, FeedbackRequestFixture, TemplateQuestionFixture, FeedbackAnswerFixture, EmployeeHoursFixture {

  @Inject
  @Client("/services/report/data")
  HttpClient client;

  private EmployeeHours employeeHours;
  private FeedbackTemplate feedbackTemplate;
  private ReviewPeriod reviewPeriod;
  private FeedbackRequest feedbackRequest;
  private TemplateQuestion questionOne;
  private TemplateQuestion questionTwo;
  private MemberProfile regular;
  private MemberProfile admin;
  private final String basePath = "src/test/java/com/objectcomputing/checkins/services/reports/";

  @BeforeEach
  void createRolesAndPermissions() {
    createAndAssignRoles();
    regular = createADefaultMemberProfile();
    admin = createAThirdDefaultMemberProfile();
    assignAdminRole(admin);

    feedbackTemplate = saveFeedbackTemplate(admin.getId());
    reviewPeriod = createAClosedReviewPeriod(
                       LocalDate.now().minusDays(30).atStartOfDay(),
                       LocalDate.now().plusDays(1).atStartOfDay());
    questionOne = saveTemplateQuestion(feedbackTemplate, 1);
    questionTwo = saveAnotherTemplateQuestion(feedbackTemplate, 2);
    feedbackRequest = saveSampleFeedbackRequest(admin, regular, admin,
                                                feedbackTemplate.getId(),
                                                reviewPeriod);
    saveSampleFeedbackAnswer(questionOne.getId(), feedbackRequest.getId());
    saveSampleFeedbackAnswer(questionTwo.getId(), feedbackRequest.getId());

    Kudos kudos = createApprovedKudos(admin.getId());
    createKudosRecipient(kudos.getId(), regular.getId());

    employeeHours = new EmployeeHours(regular.getEmployeeId(),
                                      Float.valueOf(1413), Float.valueOf(1371),
                                      Float.valueOf(0), LocalDate.now(),
                                      Float.valueOf(1850), LocalDate.now(),
                                      Float.valueOf(90), Float.valueOf(10));
    saveEmployeeHours(employeeHours);
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
    MemberProfile target = regular;
    HttpRequest<?> request = postData(admin, ADMIN_ROLE);
    final String response = client.toBlocking().retrieve(request);
    assertNotNull(response);

    request = HttpRequest.GET(
          String.format("/?memberIds=%s&reviewPeriodId=%s",
                        target.getId(),
                        reviewPeriod.getId().toString()))
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

    validateReportData(first, target);
  }

  @Test
  void getReportDataWithoutPermission() {
    final HttpRequest<?> request = HttpRequest.GET(
          String.format("/?memberIds=%s&reviewPeriodId=%s",
                        regular.getId(),
                        reviewPeriod.getId()))
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

  void validateReportData(JsonNode node, MemberProfile user) {
    final String memberId = user.getId().toString();

    // Member Info
    assertEquals(memberId, node.get("memberId").asText());
    JsonNode profile = node.get("memberProfile");
    assertEquals(user.getFirstName(), profile.get("firstName").asText());
    assertEquals(user.getLastName(), profile.get("lastName").asText());
    assertEquals(user.getTitle(), profile.get("title").asText());

    // Kudos
    ArrayNode kudos = (ArrayNode)node.get("kudos");
    assertEquals(1, kudos.size());
    assertEquals("Default Kudos", kudos.get(0).get("message").asText());

    // Compensation History
    ArrayNode comp = (ArrayNode)node.get("compensationHistory");
    assertEquals(5, comp.size());
    assertEquals(memberId, comp.get(0).get("memberId").asText());
    assertTrue(comp.get(0).get("amount").asDouble() > 0);

    // Current Information
    JsonNode curr = node.get("currentInformation");
    assertEquals(memberId, curr.get("memberId").asText());
    assertTrue(curr.get("salary").asDouble() > 0);
    assertEquals("$90000 - $150000", curr.get("range").asText());
    assertEquals("$89000 - $155000", curr.get("nationalRange").asText());

    // Position History
    ArrayNode pos = (ArrayNode)node.get("positionHistory");
    assertEquals(3, pos.size());
    assertEquals(memberId, pos.get(2).get("memberId").asText());
    assertEquals("Software Engineer", pos.get(2).get("title").asText());

    // Feedback
    ArrayNode feedback = (ArrayNode)node.get("feedback");
    assertEquals(1, feedback.size());
    ArrayNode answers = (ArrayNode)feedback.get(0).get("answers");
    assertEquals(2, answers.size());
    assertEquals("TEXT", answers.get(0).get("type").asText());
    assertEquals(1, answers.get(0).get("number").asInt());

    // Hours
    JsonNode hours = node.get("hours");
    assertTrue(employeeHours.getContributionHours() ==
               hours.get("contributionHours").asDouble());
    assertTrue(employeeHours.getPtoHours() ==
               hours.get("ptoHours").asDouble());
    assertTrue(employeeHours.getOvertimeWorked() ==
               hours.get("overtimeHours").asDouble());
    assertTrue(employeeHours.getBillableUtilization() ==
               hours.get("billableUtilization").asDouble());
  }
}
