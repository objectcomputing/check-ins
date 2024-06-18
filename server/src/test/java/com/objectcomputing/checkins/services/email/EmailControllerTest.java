package com.objectcomputing.checkins.services.email;

import com.objectcomputing.checkins.notifications.email.EmailSender;
import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.fixture.MemberProfileFixture;
import com.objectcomputing.checkins.services.fixture.RoleFixture;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.role.RoleType;
import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import jakarta.inject.Inject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EmailControllerTest extends TestContainersSuite implements MemberProfileFixture, RoleFixture {

    @Inject
    @Client("/services/email")
    private HttpClient client;

    @Mock
    private final EmailSender htmlEmailSender = mock(EmailSender.class);

    @Mock
    private final EmailSender textEmailSender = mock(EmailSender.class);

    @Inject
    private EmailServicesImpl emailServicesImpl;

    @BeforeEach
    void resetMocks() {
        Mockito.reset(htmlEmailSender);
        Mockito.reset(textEmailSender);
        emailServicesImpl.setHtmlEmailSender(htmlEmailSender);
        emailServicesImpl.setTextEmailSender(textEmailSender);
    }

    @Test
    void testSaveEmail() {
        MemberProfile admin = createADefaultMemberProfile();
        createAndAssignAdminRole(admin);
        MemberProfile recipient = createASecondDefaultMemberProfile();

        Email email = new Email("Email Subject", "Email contents", admin.getId(), recipient.getId());
        Email savedEmail = getEmailRepository().save(email);

        assertNotNull(savedEmail);
        assertEquals(email, savedEmail);
    }

    @Test
    void testSendAndSaveHtmlEmail() {
        MemberProfile admin = createADefaultMemberProfile();
        createAndAssignAdminRole(admin);
        MemberProfile recipient1 = createASecondDefaultMemberProfile();
        MemberProfile recipient2 = createAThirdDefaultMemberProfile();

        Map<String, Object> email = new HashMap<>();
        email.put("subject", "Email Subject");
        email.put("content", "<p>Email content</p>");
        email.put("html", true);
        email.put("recipients", List.of(recipient1.getWorkEmail(), recipient2.getWorkEmail()));

        when(htmlEmailSender.sendEmailReceivesStatus(anyString(), anyString(), anyString(), anyString(), anyString(), anyString())).thenReturn(true);

        final HttpRequest<?> request = HttpRequest.POST("", email)
                .basicAuth(admin.getWorkEmail(), RoleType.Constants.ADMIN_ROLE);
        final HttpResponse<List<Email>> response = client.toBlocking()
                .exchange(request, Argument.listOf(Email.class));

        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertTrue(response.getBody().isPresent());
        assertEquals(2, response.getBody().get().size());

        Email firstEmailRes = response.getBody().get().get(0);
        assertEquals(email.get("subject"), firstEmailRes.getSubject());
        assertEquals(email.get("content"), firstEmailRes.getContents());
        assertEquals(admin.getId(), firstEmailRes.getSentBy());
        assertEquals(recipient1.getId(), firstEmailRes.getRecipient());
        assertTrue(firstEmailRes.getTransmissionDate().isAfter(firstEmailRes.getSendDate()));

        Email secondEmailRes = response.getBody().get().get(1);
        assertEquals(email.get("subject"), secondEmailRes.getSubject());
        assertEquals(email.get("content"), secondEmailRes.getContents());
        assertEquals(admin.getId(), secondEmailRes.getSentBy());
        assertEquals(recipient2.getId(), secondEmailRes.getRecipient());
        assertTrue(secondEmailRes.getTransmissionDate().isAfter(secondEmailRes.getSendDate()));

        verify(htmlEmailSender).sendEmailReceivesStatus(admin.getFirstName()+" "+admin.getLastName(), admin.getWorkEmail(),"Email Subject", "<p>Email content</p>", recipient1.getWorkEmail(), recipient2.getWorkEmail());
        verify(textEmailSender, never()).sendEmailReceivesStatus(anyString(), anyString(), anyString(), anyString(), anyString(), anyString());
    }

    @Test
    void testSendAndSaveTextEmail() {
        MemberProfile admin = createADefaultMemberProfile();
        createAndAssignAdminRole(admin);
        MemberProfile recipient1 = createASecondDefaultMemberProfile();
        MemberProfile recipient2 = createAThirdDefaultMemberProfile();

        Map<String, Object> email = new HashMap<>();
        email.put("subject", "Email Subject");
        email.put("content", "Email content");
        email.put("html", false);
        email.put("recipients", List.of(recipient1.getWorkEmail(), recipient2.getWorkEmail()));

        when(textEmailSender.sendEmailReceivesStatus(anyString(), anyString(), anyString(), anyString(), anyString(), anyString())).thenReturn(true);

        final HttpRequest<?> request = HttpRequest.POST("", email)
                .basicAuth(admin.getWorkEmail(), RoleType.Constants.ADMIN_ROLE);
        final HttpResponse<List<Email>> response = client.toBlocking()
                .exchange(request, Argument.listOf(Email.class));

        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertTrue(response.getBody().isPresent());
        assertEquals(2, response.getBody().get().size());

        Email firstEmailRes = response.getBody().get().get(0);
        assertEquals(email.get("subject"), firstEmailRes.getSubject());
        assertEquals(email.get("content"), firstEmailRes.getContents());
        assertEquals(admin.getId(), firstEmailRes.getSentBy());
        assertEquals(recipient1.getId(), firstEmailRes.getRecipient());
        assertTrue(firstEmailRes.getTransmissionDate().isAfter(firstEmailRes.getSendDate()));

        Email secondEmailRes = response.getBody().get().get(1);
        assertEquals(email.get("subject"), secondEmailRes.getSubject());
        assertEquals(email.get("content"), secondEmailRes.getContents());
        assertEquals(admin.getId(), secondEmailRes.getSentBy());
        assertEquals(recipient2.getId(), secondEmailRes.getRecipient());
        assertTrue(secondEmailRes.getTransmissionDate().isAfter(secondEmailRes.getSendDate()));

        verify(textEmailSender).sendEmailReceivesStatus(admin.getFirstName()+" "+admin.getLastName(), admin.getWorkEmail(), "Email Subject", "Email content", recipient1.getWorkEmail(), recipient2.getWorkEmail());
        verify(htmlEmailSender, never()).sendEmailReceivesStatus(anyString(), anyString(), anyString(), anyString(), anyString(), anyString());
    }

    @Test
    void testSendAndSaveEmailUnauthorized() {
        MemberProfile member = createAnUnrelatedUser();

        MemberProfile recipient1 = createASecondDefaultMemberProfile();
        MemberProfile recipient2 = createAThirdDefaultMemberProfile();

        Map<String, Object> email = new HashMap<>();
        email.put("subject", "Email Subject");
        email.put("content", "Email content");
        email.put("html", false);
        email.put("recipients", List.of(recipient1.getWorkEmail(), recipient2.getWorkEmail()));

        final HttpRequest<?> request = HttpRequest.POST("", email)
                .basicAuth(member.getWorkEmail(), RoleType.Constants.MEMBER_ROLE);
        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Argument.listOf(Email.class)));

        assertEquals(HttpStatus.FORBIDDEN, responseException.getStatus());
        assertEquals("You are not authorized to do this operation", responseException.getMessage());
    }


}
