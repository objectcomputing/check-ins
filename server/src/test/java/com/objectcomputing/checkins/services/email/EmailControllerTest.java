package com.objectcomputing.checkins.services.email;

import com.objectcomputing.checkins.notifications.email.MailJetFactory;
import com.objectcomputing.checkins.services.MailJetFactoryReplacement;
import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.fixture.MemberProfileFixture;
import com.objectcomputing.checkins.services.fixture.RoleFixture;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.role.RoleType;
import io.micronaut.context.annotation.Property;
import io.micronaut.core.type.Argument;
import io.micronaut.core.util.StringUtils;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.objectcomputing.checkins.services.validate.PermissionsValidation.NOT_AUTHORIZED_MSG;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Property(name = "replace.mailjet.factory", value = StringUtils.TRUE)
class EmailControllerTest extends TestContainersSuite implements MemberProfileFixture, RoleFixture {

    @Inject
    @Client("/services/email")
    private HttpClient client;

    @Inject
    @Named(MailJetFactory.HTML_FORMAT)
    private MailJetFactoryReplacement.MockEmailSender htmlEmailSender;

    @Inject
    @Named(MailJetFactory.TEXT_FORMAT)
    private MailJetFactoryReplacement.MockEmailSender textEmailSender;

    @BeforeEach
    void resetMocks() {
        htmlEmailSender.reset();
        textEmailSender.reset();
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
        assertTrue(firstEmailRes.getTransmissionDate().isAfter(firstEmailRes.getSendDate()) ||
                   firstEmailRes.getTransmissionDate().isEqual(firstEmailRes.getSendDate()));

        Email secondEmailRes = response.getBody().get().get(1);
        assertEquals(email.get("subject"), secondEmailRes.getSubject());
        assertEquals(email.get("content"), secondEmailRes.getContents());
        assertEquals(admin.getId(), secondEmailRes.getSentBy());
        assertEquals(recipient2.getId(), secondEmailRes.getRecipient());
        assertTrue(secondEmailRes.getTransmissionDate().isAfter(secondEmailRes.getSendDate()) ||
                   secondEmailRes.getTransmissionDate().isEqual(secondEmailRes.getSendDate()));

        assertEquals(1, htmlEmailSender.events.size());
        assertEquals(
                List.of("SEND_EMAIL_RECEIVES_STATUS", admin.getFirstName()+" "+admin.getLastName(), admin.getWorkEmail(),"Email Subject", "<p>Email content</p>", recipient1.getWorkEmail() + "," + recipient2.getWorkEmail()),
                htmlEmailSender.events.getFirst()
        );
        assertTrue(textEmailSender.events.isEmpty());
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

        assertEquals(1, textEmailSender.events.size());
        assertEquals(
                List.of("SEND_EMAIL_RECEIVES_STATUS", admin.getFirstName()+" "+admin.getLastName(), admin.getWorkEmail(),"Email Subject", "Email content", recipient1.getWorkEmail() + "," + recipient2.getWorkEmail()),
                textEmailSender.events.getFirst()
        );
        assertTrue(htmlEmailSender.events.isEmpty());
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
        assertEquals("Forbidden", responseException.getMessage());
    }
}
