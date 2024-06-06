package com.objectcomputing.checkins.services.certification;

import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.fixture.CertificationFixture;
import com.objectcomputing.checkins.services.fixture.MemberProfileFixture;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static com.objectcomputing.checkins.services.role.RoleType.Constants.MEMBER_ROLE;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CertificationControllerTest extends TestContainersSuite implements MemberProfileFixture, CertificationFixture {

    @Inject
    @Client("/services/certification")
    private HttpClient certificationClient;

    @Inject
    @Client("/services/earned-certification")
    private HttpClient earnedCertificationClient;

    @Test
    void testStartsEmpty() {
        List<Certification> retrieve = certificationClient.toBlocking().retrieve(HttpRequest.GET("/").basicAuth(MEMBER_ROLE, MEMBER_ROLE), Argument.listOf(Certification.class));
        assertTrue(retrieve.isEmpty());
    }

    @Test
    void canCreateCertification() {
        createDefaultCertification();
        CertificationDTO newCertification = new CertificationDTO("New Certification", "https://badge.url");
        Certification createdCertification = certificationClient.toBlocking().retrieve(HttpRequest.POST("/", newCertification).basicAuth(MEMBER_ROLE, MEMBER_ROLE), Certification.class);

        assertNotNull(createdCertification.getId());
        assertEquals(newCertification.getName(), createdCertification.getName());
        assertEquals(newCertification.getBadgeUrl(), createdCertification.getBadgeUrl());

        List<Certification> retrieve = certificationClient.toBlocking().retrieve(HttpRequest.GET("/").basicAuth(MEMBER_ROLE, MEMBER_ROLE), Argument.listOf(Certification.class));
        assertEquals(2, retrieve.size());
    }

    @Test
    void listIsOrdered() {
        createCertification("Donkey husbandry");
        createCertification("Aardvark upkeep");
        createCertification("Zebu and you", "https://badge.url");

        List<Certification> list = certificationClient.toBlocking().retrieve(HttpRequest.GET("/").basicAuth(MEMBER_ROLE, MEMBER_ROLE), Argument.listOf(Certification.class));

        assertEquals(3, list.size());
        assertEquals(List.of("Aardvark upkeep", "Donkey husbandry", "Zebu and you"), list.stream().map(Certification::getName).toList());
    }

    @Test
    void canDelete() {
        createCertification("To keep");
        Certification certification = createCertification("To delete");

        List<Certification> list = certificationClient.toBlocking().retrieve(HttpRequest.GET("/").basicAuth(MEMBER_ROLE, MEMBER_ROLE), Argument.listOf(Certification.class));
        assertEquals(2, list.size());

        certificationClient.toBlocking().exchange(HttpRequest.DELETE("/" + certification.getId()).basicAuth(MEMBER_ROLE, MEMBER_ROLE));

        list = certificationClient.toBlocking().retrieve(HttpRequest.GET("/").basicAuth(MEMBER_ROLE, MEMBER_ROLE), Argument.listOf(Certification.class));
        assertEquals(1, list.size());
        assertEquals("To keep", list.getFirst().getName());
    }

    @Test
    void canUpdate() {
        Certification certification = createCertification("To update");

        CertificationDTO update = new CertificationDTO("Updated", "https://badge.url");
        Certification updated = certificationClient.toBlocking().retrieve(HttpRequest.PUT("/" + certification.getId(), update).basicAuth(MEMBER_ROLE, MEMBER_ROLE), Certification.class);

        assertEquals(certification.getId(), updated.getId());
        assertEquals(update.getName(), updated.getName());
        assertEquals(update.getBadgeUrl(), updated.getBadgeUrl());

        List<Certification> list = certificationClient.toBlocking().retrieve(HttpRequest.GET("/").basicAuth(MEMBER_ROLE, MEMBER_ROLE), Argument.listOf(Certification.class));
        assertEquals(1, list.size());
    }

    @Test
    void canMerge() {
        MemberProfile tim = memberWithoutBoss("Tim");
        MemberProfile sarah = memberWithoutBoss("Sarah");

        Certification source = createCertification("Source");
        Certification target = createCertification("Target");

        createEarnedCertification(tim, source, "Tim's source certification", LocalDate.now().minusDays(1));
        createEarnedCertification(sarah, target, "Sarah's target certification", LocalDate.now());

        // Tim has one certification assigned from the source certification
        List<EarnedCertification> list = earnedCertificationClient.toBlocking().retrieve(HttpRequest.GET("/?memberId=%s".formatted(tim.getId())).basicAuth(MEMBER_ROLE, MEMBER_ROLE), Argument.listOf(EarnedCertification.class));
        assertEquals(1, list.size());
        assertEquals(source.getId(), list.getFirst().getCertificationId());
        assertEquals("Tim's source certification", list.getFirst().getDescription());

        // When we merge source into target
        CertificationMergeDTO merge = new CertificationMergeDTO(source.getId(), target.getId());
        HttpResponse<Object> exchange = certificationClient.toBlocking().exchange(HttpRequest.POST("/merge", merge).basicAuth(MEMBER_ROLE, MEMBER_ROLE));
        assertEquals(HttpStatus.OK, exchange.getStatus());

        // Tim should now have the target certification with the same details
        list = earnedCertificationClient.toBlocking().retrieve(HttpRequest.GET("/?memberId=%s".formatted(tim.getId())).basicAuth(MEMBER_ROLE, MEMBER_ROLE), Argument.listOf(EarnedCertification.class));
        assertEquals(1, list.size());
        assertEquals(target.getId(), list.getFirst().getCertificationId());
        assertEquals("Tim's source certification", list.getFirst().getDescription());

        // And when we list all of them, we should see that the source certification is gone
        List<Certification> certificationList = certificationClient.toBlocking().retrieve(HttpRequest.GET("/").basicAuth(MEMBER_ROLE, MEMBER_ROLE), Argument.listOf(Certification.class));
        assertEquals(1, certificationList.size());
        assertEquals(target.getId(), certificationList.getFirst().getId());

        // And the earned certifications should be updated
        List<EarnedCertification> earnedCertifications = earnedCertificationClient.toBlocking().retrieve(HttpRequest.GET("/").basicAuth(MEMBER_ROLE, MEMBER_ROLE), Argument.listOf(EarnedCertification.class));
        assertEquals(2, earnedCertifications.size());
        assertAll(earnedCertifications.stream().map(c -> () -> assertEquals(target.getId(), c.getCertificationId())));

        // And they are sorted by earned date descending
        assertEquals(List.of("Sarah's target certification", "Tim's source certification"), earnedCertifications.stream().map(EarnedCertification::getDescription).toList());
    }
}
