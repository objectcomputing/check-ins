package com.objectcomputing.checkins.services.certification;

import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.fixture.CertificationFixture;
import com.objectcomputing.checkins.services.fixture.MemberProfileFixture;
import com.objectcomputing.checkins.services.fixture.RoleFixture;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static com.objectcomputing.checkins.services.role.RoleType.Constants.ADMIN_ROLE;
import static com.objectcomputing.checkins.services.role.RoleType.Constants.MEMBER_ROLE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CertificationControllerTest extends TestContainersSuite implements RoleFixture, MemberProfileFixture, CertificationFixture {

    @Inject
    @Client("/services/certification")
    private HttpClient certificationClient;

    @BeforeEach
    void createRolesAndPermissions() {
        createAndAssignRoles();
    }

    @Test
    void testStartsEmpty() {
        List<Certification> retrieve = list();
        assertTrue(retrieve.isEmpty());
    }

    @Test
    void canCreateCertification() {
        createDefaultCertification();
        CertificationDTO newCertification = new CertificationDTO("New Certification", "https://badge.url");
        Certification createdCertification = create(newCertification);

        assertNotNull(createdCertification.getId());
        assertEquals(newCertification.getName(), createdCertification.getName());
        assertEquals(newCertification.getBadgeUrl(), createdCertification.getBadgeUrl());

        List<Certification> retrieve = certificationClient.toBlocking().retrieve(HttpRequest.GET("/").basicAuth(ADMIN_ROLE, ADMIN_ROLE), Argument.listOf(Certification.class));
        assertEquals(2, retrieve.size());
    }

    @Test
    void canCreateCertificationWithoutThePermission() {
        MemberProfile tim = createASecondDefaultMemberProfile();
        createDefaultCertification();
        CertificationDTO newCertification = new CertificationDTO("New Certification", "https://badge.url");
        Certification createdCertification = create(newCertification, tim.getWorkEmail(), MEMBER_ROLE);

        assertNotNull(createdCertification.getId());
        assertEquals(newCertification.getName(), createdCertification.getName());
        assertEquals(newCertification.getBadgeUrl(), createdCertification.getBadgeUrl());

        List<Certification> retrieve = certificationClient.toBlocking().retrieve(HttpRequest.GET("/").basicAuth(ADMIN_ROLE, ADMIN_ROLE), Argument.listOf(Certification.class));
        assertEquals(2, retrieve.size());
    }

    @Test
    void listIsOrdered() {
        createCertification("Donkey husbandry");
        createCertification("Aardvark upkeep");
        createCertification("Zebu and you", "https://badge.url");

        List<Certification> list = list();

        assertEquals(3, list.size());
        assertEquals(List.of("Aardvark upkeep", "Donkey husbandry", "Zebu and you"), list.stream().map(Certification::getName).toList());
    }

    @Test
    void canUpdate() {
        Certification certification = createCertification("To update");

        CertificationDTO update = new CertificationDTO("Updated", "https://badge.url");

        Certification updated = update(certification.getId(), update);

        assertEquals(certification.getId(), updated.getId());
        assertEquals(update.getName(), updated.getName());
        assertEquals(update.getBadgeUrl(), updated.getBadgeUrl());

        List<Certification> list = list();
        assertEquals(1, list.size());
    }

    @Test
    void cannotUpdateWithoutThePermission() {
        MemberProfile tim = createASecondDefaultMemberProfile();
        Certification certification = createCertification("To update");

        CertificationDTO update = new CertificationDTO("Updated", "https://badge.url");

        HttpClientResponseException e = assertThrows(HttpClientResponseException.class, () -> update(certification.getId(), update, tim.getWorkEmail(), MEMBER_ROLE));
        assertEquals(HttpStatus.FORBIDDEN, e.getStatus());
    }

    @Test
    void certificationNameRequired() {
        HttpClientResponseException exception = assertThrows(HttpClientResponseException.class, () -> create("""
                {"badgeUrl":"badge"}"""));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getResponse().status());
        assertTrue(exception.getResponse().getBody(String.class).get().contains("certification.name: must not be null"), "Validation message is as expected");
    }

    @Test
    void certificationNameUniquenessTest() {
        Certification original = createCertification("Default");

        // Cannot create a certification with the same name
        String postBody = """
                {"name":"%s"}""".formatted(original.getName());
        HttpClientResponseException exception = assertThrows(HttpClientResponseException.class, () -> create(postBody));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getResponse().status());
        assertEquals("Certification with name Default already exists", exception.getMessage());

        // Cannot update a certification to have the same name as another
        Certification other = createCertification("Other");
        UUID otherId = other.getId();
        HttpClientResponseException exception2 = assertThrows(HttpClientResponseException.class, () -> update(otherId, postBody));

        assertEquals(HttpStatus.BAD_REQUEST, exception2.getResponse().status());
        assertEquals("Certification with name Default already exists", exception2.getMessage());

        // But we can successfully rename the certification
        update(other.getId(), """
                {"name":"Updated"}""");

        // and we can update to keep the same name
        update(other.getId(), """
                {"name":"Updated","badgeUrl":"woo"}""");

        // Check the list of exceptions are as we expect
        List<Certification> list = list();
        assertEquals(2, list.size());
        assertEquals(List.of("Default", "Updated"), list.stream().map(Certification::getName).toList());
        assertEquals(Arrays.asList(null, "woo"), list.stream().map(Certification::getBadgeUrl).toList());
    }

    @Test
    void certificationsDefaultToEnabled() {
        Certification nameAndUrlActive = create("""
                {"name":"a","badgeUrl":"badge"}""");

        assertEquals("a", nameAndUrlActive.getName());
        assertEquals("badge", nameAndUrlActive.getBadgeUrl());
        assertTrue(nameAndUrlActive.isActive());

        Certification nameNoUrlActive = create("""
                {"name":"b"}""");

        assertEquals("b", nameNoUrlActive.getName());
        assertNull(nameNoUrlActive.getBadgeUrl());
        assertTrue(nameNoUrlActive.isActive());

        Certification nameNoUrlInactive = create("""
                {"name":"c","active":"false"}""");

        assertEquals("c", nameNoUrlInactive.getName());
        assertNull(nameNoUrlInactive.getBadgeUrl());
        assertFalse(nameNoUrlInactive.isActive());

        Certification nameNoUrlInactiveBool = create("""
                {"name":"d","active":false}""");

        assertEquals("d", nameNoUrlInactiveBool.getName());
        assertNull(nameNoUrlInactiveBool.getBadgeUrl());
        assertFalse(nameNoUrlInactiveBool.isActive());

        Certification nameUrlSetActive = create("""
                {"name":"e","badgeUrl":"badge2","active":true}""");

        assertEquals("e", nameUrlSetActive.getName());
        assertEquals("badge2", nameUrlSetActive.getBadgeUrl());
        assertTrue(nameUrlSetActive.isActive());
    }

    private List<Certification> list() {
        return certificationClient.toBlocking().retrieve(HttpRequest.GET("/").basicAuth(ADMIN_ROLE, ADMIN_ROLE), Argument.listOf(Certification.class));
    }

    private <T> Certification create(T body) {
        return create(body, ADMIN_ROLE, ADMIN_ROLE);
    }

    private <T> Certification create(T body, String user, String password) {
        return certificationClient.toBlocking().retrieve(HttpRequest.POST("/", body).basicAuth(user, password), Certification.class);
    }

    private <T> Certification update(UUID uuid, T body) {
        return update(uuid, body, ADMIN_ROLE, ADMIN_ROLE);
    }

    private <T> Certification update(UUID uuid, T body, String user, String password) {
        return certificationClient.toBlocking().retrieve(HttpRequest.PUT("/%s".formatted(uuid), body).basicAuth(user, password), Certification.class);
    }
}
