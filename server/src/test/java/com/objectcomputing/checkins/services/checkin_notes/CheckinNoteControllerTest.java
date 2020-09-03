package com.objectcomputing.checkins.services.checkin_notes;		

  import com.fasterxml.jackson.databind.JsonNode;		
 import com.objectcomputing.checkins.services.TestContainersSuite;		
 import com.objectcomputing.checkins.services.checkins.CheckIn;		
 import com.objectcomputing.checkins.services.fixture.CheckInFixture;		
 import com.objectcomputing.checkins.services.fixture.CheckInNoteFixture;		
 import com.objectcomputing.checkins.services.fixture.MemberProfileFixture;		
 import com.objectcomputing.checkins.services.memberprofile.MemberProfile;		
 import io.micronaut.core.type.Argument;		
 import io.micronaut.http.HttpRequest;		
 import io.micronaut.http.HttpResponse;		
 import io.micronaut.http.HttpStatus;		
 import io.micronaut.http.client.HttpClient;		
 import io.micronaut.http.client.annotation.Client;		
 import io.micronaut.http.client.exceptions.HttpClientResponseException;		
 import org.junit.jupiter.api.Test;		

  import javax.inject.Inject;		
 import java.util.*;		
 import java.util.stream.Collectors;		

  import static com.objectcomputing.checkins.services.role.RoleType.Constants.PDL_ROLE;		
 import static org.junit.Assert.assertNotNull;		
 import static org.junit.jupiter.api.Assertions.assertEquals;		
 import static org.junit.jupiter.api.Assertions.assertThrows;		

 
  public class CheckinNoteControllerTest extends TestContainersSuite implements MemberProfileFixture, CheckInFixture, CheckInNoteFixture {		

      @Inject		
     @Client("/services/checkin-note")		
     HttpClient client ;		

 
      @Test		
     void testCreateCheckinNote(){		
      MemberProfile memberProfile = createADefaultMemberProfile();		
      MemberProfile memberProfileForPDL = createADefaultMemberProfileForPdl(memberProfile);		

       CheckIn checkIn = createADefaultCheckIn(memberProfile,memberProfileForPDL);		

       CheckinNoteCreateDTO checkinNoteCreateDTO = new CheckinNoteCreateDTO();		
      checkinNoteCreateDTO.setCheckinid(checkIn.getId());		
      checkinNoteCreateDTO.setCreatedbyid(memberProfile.getId());
      checkinNoteCreateDTO.setDescription("test");		

       final HttpRequest<CheckinNoteCreateDTO> request = HttpRequest.POST("", checkinNoteCreateDTO).basicAuth(PDL_ROLE,PDL_ROLE);		
      final HttpResponse<CheckinNote> response = client.toBlocking().exchange(request, CheckinNote.class);		

       CheckinNote checkinNote = response.body();		

       assertNotNull(response);		
      assertEquals(HttpStatus.CREATED, response.getStatus());		
      assertEquals(checkinNoteCreateDTO.getCheckinid(),checkinNote.getCheckinid());		
      assertEquals(checkinNoteCreateDTO.getCreatedbyid(),checkinNote.getCreatedbyid());		
      assertEquals(String.format("%s/%s", request.getPath(), checkinNote.getId()), response.getHeaders().get("location"));		
     }		

      @Test		
     void testCreateInvalidCheckinNote(){		
      CheckinNoteCreateDTO checkinNoteCreateDTO = new CheckinNoteCreateDTO();		

       final HttpRequest<CheckinNoteCreateDTO> request = HttpRequest.POST("", checkinNoteCreateDTO).basicAuth(PDL_ROLE,PDL_ROLE);		
      HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,		
      () -> client.toBlocking().exchange(request, Map.class));		

       JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);		
      JsonNode errors = Objects.requireNonNull(body).get("_embedded").get("errors");		
      JsonNode href = Objects.requireNonNull(body).get("_links").get("self").get("href");		
      List<String> errorList = List.of(errors.get(0).get("message").asText(), errors.get(1).get("message").asText())		
              .stream().sorted().collect(Collectors.toList());		
      assertEquals("checkinNote.checkinid: must not be null", errorList.get(0));		
      assertEquals("checkinNote.createdbyid: must not be null", errorList.get(1));		
      assertEquals(request.getPath(), href.asText());		
      assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());		

      }		

      @Test		
     void testCreateNullCheckinNote(){		

          final HttpRequest<String> request = HttpRequest.POST("", "").basicAuth(PDL_ROLE,PDL_ROLE);		
         HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,		
         () -> client.toBlocking().exchange(request, Map.class));		

          JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);		
         JsonNode errors = Objects.requireNonNull(body).get("message");		
         JsonNode href = Objects.requireNonNull(body).get("_links").get("self").get("href");		

          assertEquals("Required Body [checkinNote] not specified", errors.asText());		
         assertEquals(request.getPath(), href.asText());		
         assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());		

      }		

      @Test		
     void testCreateACheckInNoteForExistingCheckInId() {		
         MemberProfile memberProfile = createADefaultMemberProfile();		
         MemberProfile memberProfileForPDL = createADefaultMemberProfileForPdl(memberProfile);		

          CheckIn checkIn = createADefaultCheckIn(memberProfile,memberProfileForPDL);		

          CheckinNoteCreateDTO checkinNoteCreateDTO = new CheckinNoteCreateDTO();		
         checkinNoteCreateDTO.setCheckinid(UUID.randomUUID());		
         checkinNoteCreateDTO.setCreatedbyid(memberProfile.getId());
         checkinNoteCreateDTO.setDescription("test");		

          final HttpRequest<CheckinNoteCreateDTO> request = HttpRequest.POST("",checkinNoteCreateDTO).basicAuth(PDL_ROLE,PDL_ROLE);		
         HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,		
                 () -> client.toBlocking().exchange(request, Map.class));		
         JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);		
         String error = Objects.requireNonNull(body).get("message").asText();		
         String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();		

          assertEquals(request.getPath(),href);		
         assertEquals(String.format("CheckIn %s doesn't exist",checkinNoteCreateDTO.getCheckinid()),error);		

      }		

      @Test		
     void testCreateACheckInNoteForExistingMemberIdId() {		
         MemberProfile memberProfile = createADefaultMemberProfile();		
         MemberProfile memberProfileForPDL = createADefaultMemberProfileForPdl(memberProfile);		

          CheckIn checkIn = createADefaultCheckIn(memberProfile,memberProfileForPDL);		

          CheckinNoteCreateDTO checkinNoteCreateDTO = new CheckinNoteCreateDTO();		
         checkinNoteCreateDTO.setCheckinid(checkIn.getId());		
         checkinNoteCreateDTO.setCreatedbyid(UUID.randomUUID());		
         checkinNoteCreateDTO.setDescription("test");		

          final HttpRequest<CheckinNoteCreateDTO> request = HttpRequest.POST("",checkinNoteCreateDTO).basicAuth(PDL_ROLE,PDL_ROLE);		
         HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,		
                 () -> client.toBlocking().exchange(request, Map.class));		
         JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);		
         String error = Objects.requireNonNull(body).get("message").asText();		
         String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();		

          assertEquals(request.getPath(),href);		
         assertEquals(String.format("Member %s doesn't exist",checkinNoteCreateDTO.getCreatedbyid()),error);		

      }		


      @Test		
     void testReadCheckinNote(){		
         MemberProfile memberProfile = createADefaultMemberProfile();		
         MemberProfile memberProfileForPDL = createADefaultMemberProfileForPdl(memberProfile);		

          CheckIn checkIn = createADefaultCheckIn(memberProfile,memberProfileForPDL);		

          CheckinNote checkinNote = createADeafultCheckInNote(checkIn,memberProfile);		

          final HttpRequest<?> request = HttpRequest.GET(String.format("/%s",checkinNote.getId())).basicAuth(PDL_ROLE,PDL_ROLE);		
         final HttpResponse<Set<CheckinNote>> response = client.toBlocking().exchange(request, Argument.setOf(CheckinNote.class));		

          assertEquals(Set.of(checkinNote), response.body());		
         assertEquals(HttpStatus.OK, response.getStatus());		
     }		

      @Test		
     void testReadCheckinNoteNotFound(){		

          final HttpRequest<?> request = HttpRequest.GET(String.format("/%s",UUID.randomUUID())).basicAuth(PDL_ROLE,PDL_ROLE);		
         HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () -> client.toBlocking().exchange(request, CheckinNote.class));		

          assertEquals(HttpStatus.NOT_FOUND, responseException.getStatus());		

      }		

      @Test		
     void testFindCheckinNote() {		
         MemberProfile memberProfile = createADefaultMemberProfile();		
         MemberProfile memberProfileForPDL = createADefaultMemberProfileForPdl(memberProfile);		

          CheckIn checkIn = createADefaultCheckIn(memberProfile,memberProfileForPDL);		

          CheckinNote checkinNote = createADeafultCheckInNote(checkIn,memberProfile);		

          final HttpRequest<?> request = HttpRequest.GET(String.format("/?checkinid=%s&createdbyid=%s",checkinNote.getCheckinid(),checkinNote.getCreatedbyid()))		
                 .basicAuth(PDL_ROLE,PDL_ROLE);		
         final HttpResponse<Set<CheckinNote>> response = client.toBlocking().exchange(request, Argument.setOf(CheckinNote.class));		

          assertEquals(Set.of(checkinNote), response.body());		
         assertEquals(HttpStatus.OK, response.getStatus());		

      }		

 
      @Test		
     void testFindCheckinNoteByMemberId() {		
         MemberProfile memberProfile = createADefaultMemberProfile();		
         MemberProfile memberProfileForPDL = createADefaultMemberProfileForPdl(memberProfile);		

          CheckIn checkIn = createADefaultCheckIn(memberProfile,memberProfileForPDL);		

          CheckinNote checkinNote = createADeafultCheckInNote(checkIn,memberProfile);		

          final HttpRequest<?> request = HttpRequest.GET(String.format("/?createdbyid=%s",checkinNote.getCreatedbyid()))		
                 .basicAuth(PDL_ROLE,PDL_ROLE);		
         final HttpResponse<Set<CheckinNote>> response = client.toBlocking().exchange(request, Argument.setOf(CheckinNote.class));		

          assertEquals(Set.of(checkinNote), response.body());		
         assertEquals(HttpStatus.OK, response.getStatus());		

      }		

 
      @Test		
     void testUpdateCheckinNote(){		
         MemberProfile memberProfile = createADefaultMemberProfile();		
         MemberProfile memberProfileForPDL = createADefaultMemberProfileForPdl(memberProfile);		

          CheckIn checkIn = createADefaultCheckIn(memberProfile,memberProfileForPDL);		

          CheckinNote checkinNote = createADeafultCheckInNote(checkIn,memberProfile);		

      final HttpRequest<?> request = HttpRequest.PUT("",checkinNote).basicAuth(PDL_ROLE,PDL_ROLE);		
     final HttpResponse<CheckinNote> response = client.toBlocking().exchange(request, CheckinNote.class);		

      assertEquals(checkinNote, response.body());		
     assertEquals(HttpStatus.OK, response.getStatus());		
     }		

      @Test		
     void testUpdateInvalidCheckinNote(){		
         MemberProfile memberProfile = createADefaultMemberProfile();		
         MemberProfile memberProfileForPDL = createADefaultMemberProfileForPdl(memberProfile);		

          CheckIn checkIn = createADefaultCheckIn(memberProfile,memberProfileForPDL);		

          CheckinNote checkinNote = createADeafultCheckInNote(checkIn,memberProfile);		
         checkinNote.setCreatedbyid(null);		
         checkinNote.setCheckinid(null);		

          final HttpRequest<CheckinNote> request = HttpRequest.PUT("",checkinNote).basicAuth(PDL_ROLE,PDL_ROLE);		
         HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,		
                 () -> client.toBlocking().exchange(request, Map.class));		

 
          JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);		
         JsonNode errors = Objects.requireNonNull(body).get("_embedded").get("errors");		
         JsonNode href = Objects.requireNonNull(body).get("_links").get("self").get("href");		
         List<String> errorList = List.of(errors.get(0).get("message").asText(), errors.get(1).get("message").asText())		
                         .stream().sorted().collect(Collectors.toList());		
         assertEquals("checkinNote.checkinid: must not be null", errorList.get(0));		
         assertEquals("checkinNote.createdbyid: must not be null", errorList.get(1));		
         assertEquals(request.getPath(), href.asText());		
         assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());		
     }		

      @Test		
     void testUpdateNullCheckinNote(){		
       final HttpRequest<?> request = HttpRequest.PUT("","").basicAuth(PDL_ROLE,PDL_ROLE);		
       HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,		
               () -> client.toBlocking().exchange(request, Map.class));		

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);		
       JsonNode errors = Objects.requireNonNull(body).get("message");		
       JsonNode href = Objects.requireNonNull(body).get("_links").get("self").get("href");		
       assertEquals("Required Body [checkinNote] not specified", errors.asText());		
       assertEquals(request.getPath(), href.asText());		
       assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());		

      }		

     @Test		
    void testUpdateUnAuthorized() {		
     CheckinNote cNote = new CheckinNote(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),"test");		

         final HttpRequest<CheckinNote> request = HttpRequest.PUT("", cNote);		
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->		
                client.toBlocking().exchange(request, String.class));		

         assertEquals(HttpStatus.UNAUTHORIZED, responseException.getStatus());		
        assertEquals("Unauthorized", responseException.getMessage());		
    }		

      @Test		
     void testUpdateNonExistingCheckInNote(){		
         MemberProfile memberProfile = createADefaultMemberProfile();		
         MemberProfile memberProfileForPDL = createADefaultMemberProfileForPdl(memberProfile);		

          CheckIn checkIn  = createADefaultCheckIn(memberProfile,memberProfileForPDL);		

          CheckinNote checkinNote = createADeafultCheckInNote(checkIn,memberProfile);		
         checkinNote.setId(UUID.randomUUID());		

          final HttpRequest<CheckinNote> request = HttpRequest.PUT("", checkinNote)		
                 .basicAuth(PDL_ROLE, PDL_ROLE);		
         final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->		
                 client.toBlocking().exchange(request, Map.class));		

          JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);		
         String error = Objects.requireNonNull(body).get("message").asText();		
         String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();		

          assertEquals(String.format("Unable to locate checkin note to update with id %s", checkinNote.getId()), error);		
         assertEquals(request.getPath(), href);		

      }		

      @Test		
     void testUpdateNonExistingCheckInNoteForCheckInId(){		
         MemberProfile memberProfile = createADefaultMemberProfile();		
         MemberProfile memberProfileForPDL = createADefaultMemberProfileForPdl(memberProfile);		

          CheckIn checkIn  = createADefaultCheckIn(memberProfile,memberProfileForPDL);		

          CheckinNote checkinNote = createADeafultCheckInNote(checkIn,memberProfile);		
         checkinNote.setCheckinid(UUID.randomUUID());		

          final HttpRequest<CheckinNote> request = HttpRequest.PUT("", checkinNote)		
                 .basicAuth(PDL_ROLE, PDL_ROLE);		
         final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->		
                 client.toBlocking().exchange(request, Map.class));		

          JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);		
         String error = Objects.requireNonNull(body).get("message").asText();		
         String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();		

          assertEquals(String.format("CheckIn %s doesn't exist", checkinNote.getCheckinid()), error);		
         assertEquals(request.getPath(), href);		

      }		

      @Test		
     void testUpdateNonExistingCheckInNoteForMemberId(){		
         MemberProfile memberProfile = createADefaultMemberProfile();		
         MemberProfile memberProfileForPDL = createADefaultMemberProfileForPdl(memberProfile);		

          CheckIn checkIn  = createADefaultCheckIn(memberProfile,memberProfileForPDL);		

          CheckinNote checkinNote = createADeafultCheckInNote(checkIn,memberProfile);		
         checkinNote.setCreatedbyid(UUID.randomUUID());		

          final HttpRequest<CheckinNote> request = HttpRequest.PUT("", checkinNote)		
                 .basicAuth(PDL_ROLE, PDL_ROLE);		
         final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->		
                 client.toBlocking().exchange(request, Map.class));		

          JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);		
         String error = Objects.requireNonNull(body).get("message").asText();		
         String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();		

          assertEquals(String.format("Member %s doesn't exist", checkinNote.getCreatedbyid()), error);		
         assertEquals(request.getPath(), href);		

      }		
 } 