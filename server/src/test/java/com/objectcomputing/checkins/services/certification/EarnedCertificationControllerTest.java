package com.objectcomputing.checkins.services.certification;

import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.fixture.CertificationFixture;
import com.objectcomputing.checkins.services.fixture.MemberProfileFixture;
import com.objectcomputing.checkins.services.fixture.RoleFixture;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import jakarta.inject.Inject;
import jakarta.validation.constraints.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static com.objectcomputing.checkins.services.role.RoleType.Constants.ADMIN_ROLE;
import static com.objectcomputing.checkins.services.role.RoleType.Constants.MEMBER_ROLE;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EarnedCertificationControllerTest extends TestContainersSuite implements RoleFixture, MemberProfileFixture, CertificationFixture {

    @Inject
    @Client("/services/certification")
    private HttpClient certificationClient;

    @Inject
    @Client("/services/earned-certification")
    private HttpClient earnedCertificationClient;

    @BeforeEach
    void createRolesAndPermissions() {
        createAndAssignRoles();
    }

    @Test
    void testStartsEmpty() {
        List<EarnedCertification> retrieve = earnedCertificationClient.toBlocking().retrieve(HttpRequest.GET("/").basicAuth(MEMBER_ROLE, MEMBER_ROLE), Argument.listOf(EarnedCertification.class));
        assertTrue(retrieve.isEmpty());
    }

    @Test
    void canCreateEarnedCertificationForSelf() {
        MemberProfile member = createADefaultMemberProfile();
        Certification certification = createDefaultCertification();
        LocalDate earnedDate = LocalDate.now();
        EarnedCertificationDTO newEarnedCertification = new EarnedCertificationDTO(member.getId(), certification.getId(), "Description", earnedDate);

        EarnedCertification created = create(newEarnedCertification, member.getWorkEmail(), MEMBER_ROLE);
        assertNotNull(created.getId());
        assertEquals(member.getId(), created.getMemberId());
        assertEquals(certification.getId(), created.getCertificationId());
        assertEquals("Description", created.getDescription());
        assertEquals(earnedDate, created.getEarnedDate());
        assertNull(created.getExpirationDate());
        assertNull(created.getCertificateImageUrl());
    }

    @Test
    void canCreateEarnedCertificationForOtherIfRoleApplied() {
        MemberProfile member = createADefaultMemberProfile();
        Certification certification = createDefaultCertification();
        LocalDate earnedDate = LocalDate.now();
        EarnedCertificationDTO newEarnedCertification = new EarnedCertificationDTO(member.getId(), certification.getId(), "Description", earnedDate);

        EarnedCertification created = create(newEarnedCertification, ADMIN_ROLE, ADMIN_ROLE);
        assertNotNull(created.getId());
        assertEquals(member.getId(), created.getMemberId());
        assertEquals(certification.getId(), created.getCertificationId());
        assertEquals("Description", created.getDescription());
        assertEquals(earnedDate, created.getEarnedDate());
        assertNull(created.getExpirationDate());
        assertNull(created.getCertificateImageUrl());
    }

    @Test
    void cannotCreateEarnedCertificationForOther() {
        MemberProfile member = createADefaultMemberProfile();
        MemberProfile tim = memberWithoutBoss("Tim");
        Certification certification = createDefaultCertification();
        LocalDate earnedDate = LocalDate.now();
        EarnedCertificationDTO newEarnedCertification = new EarnedCertificationDTO(member.getId(), certification.getId(), "Description", earnedDate);

        HttpClientResponseException thrown = assertThrows(HttpClientResponseException.class, () -> create(newEarnedCertification, tim.getWorkEmail(), MEMBER_ROLE));
        assertEquals(HttpStatus.BAD_REQUEST, thrown.getStatus());
        assertEquals("User %s does not have permission to create Earned Certificate for user %s".formatted(tim.getId(), member.getId()), thrown.getMessage());
    }

    @Test
    void canUpdateEarnedCertification() {
        MemberProfile member = createADefaultMemberProfile();
        Certification certification = createDefaultCertification();
        LocalDate earnedDate = LocalDate.now();
        EarnedCertification earnedCertification = createEarnedCertification(member, certification, "Description", earnedDate);

        EarnedCertificationDTO update = new EarnedCertificationDTO(
                member.getId(),
                certification.getId(),
                "Updated description",
                earnedDate.minusDays(1),
                earnedDate.plusYears(2),
                "https://certificate.url"
        );

        EarnedCertification updated = earnedCertificationClient.toBlocking().retrieve(HttpRequest.PUT("/%s".formatted(earnedCertification.getId()), update).basicAuth(member.getWorkEmail(), MEMBER_ROLE), EarnedCertification.class);
        assertEquals(earnedCertification.getId(), updated.getId());
        assertEquals(member.getId(), updated.getMemberId());
        assertEquals(certification.getId(), updated.getCertificationId());
        assertEquals("Updated description", updated.getDescription());
        assertEquals(earnedDate.minusDays(1), updated.getEarnedDate());
        assertEquals(earnedDate.plusYears(2), updated.getExpirationDate());
        assertEquals("https://certificate.url", updated.getCertificateImageUrl());
    }

    @Test
    void canUpdateEarnedCertificationForOthersIfHavePermission() {
        MemberProfile member = createADefaultMemberProfile();
        Certification certification = createDefaultCertification();
        LocalDate earnedDate = LocalDate.now();
        EarnedCertification earnedCertification = createEarnedCertification(member, certification, "Description", earnedDate);

        EarnedCertificationDTO update = new EarnedCertificationDTO(
                member.getId(),
                certification.getId(),
                "Updated description",
                earnedDate.minusDays(1),
                earnedDate.plusYears(2),
                "https://certificate.url"
        );

        EarnedCertification updated = earnedCertificationClient.toBlocking().retrieve(HttpRequest.PUT("/%s".formatted(earnedCertification.getId()), update).basicAuth(ADMIN_ROLE, ADMIN_ROLE), EarnedCertification.class);
        assertEquals(earnedCertification.getId(), updated.getId());
        assertEquals(member.getId(), updated.getMemberId());
        assertEquals(certification.getId(), updated.getCertificationId());
        assertEquals("Updated description", updated.getDescription());
        assertEquals(earnedDate.minusDays(1), updated.getEarnedDate());
        assertEquals(earnedDate.plusYears(2), updated.getExpirationDate());
        assertEquals("https://certificate.url", updated.getCertificateImageUrl());
    }

    @Test
    void cannotUpdateEarnedCertificationForOthers() {
        MemberProfile member = createADefaultMemberProfile();
        MemberProfile tim = memberWithoutBoss("Tim");
        Certification certification = createDefaultCertification();
        LocalDate earnedDate = LocalDate.now();
        EarnedCertification earnedCertification = createEarnedCertification(member, certification, "Description", earnedDate);

        EarnedCertificationDTO update = new EarnedCertificationDTO(
                member.getId(),
                certification.getId(),
                "Updated description",
                earnedDate.minusDays(1),
                earnedDate.plusYears(2),
                "https://certificate.url"
        );
        HttpClientResponseException exception = assertThrows(HttpClientResponseException.class, () -> earnedCertificationClient.toBlocking().retrieve(HttpRequest.PUT("/%s".formatted(earnedCertification.getId()), update).basicAuth(tim.getWorkEmail(), MEMBER_ROLE)));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("User %s does not have permission to update Earned Certificate for user %s".formatted(tim.getId(), member.getId()), exception.getMessage());
    }

    @Test
    void canDeleteEarnedCertificationWithRole() {
        MemberProfile member = createADefaultMemberProfile();
        Certification certification = createDefaultCertification();
        LocalDate earnedDate = LocalDate.now();
        EarnedCertification earnedCertification = createEarnedCertification(member, certification, "Description", earnedDate);

        HttpResponse<Object> exchange = earnedCertificationClient.toBlocking().exchange(HttpRequest.DELETE("/%s".formatted(earnedCertification.getId())).basicAuth(ADMIN_ROLE, ADMIN_ROLE));
        assertEquals(HttpStatus.OK, exchange.getStatus());

        List<EarnedCertification> list = earnedCertificationClient.toBlocking().retrieve(HttpRequest.GET("/").basicAuth(member.getWorkEmail(), MEMBER_ROLE), Argument.listOf(EarnedCertification.class));
        assertTrue(list.isEmpty());
    }

    @Test
    void canDeleteOwnEarnedCertification() {
        MemberProfile member = createADefaultMemberProfile();
        Certification certification = createDefaultCertification();
        LocalDate earnedDate = LocalDate.now();
        EarnedCertification earnedCertification = createEarnedCertification(member, certification, "Description", earnedDate);

        HttpResponse<Object> exchange = earnedCertificationClient.toBlocking().exchange(HttpRequest.DELETE("/%s".formatted(earnedCertification.getId())).basicAuth(member.getWorkEmail(), MEMBER_ROLE));
        assertEquals(HttpStatus.OK, exchange.getStatus());

        List<EarnedCertification> list = earnedCertificationClient.toBlocking().retrieve(HttpRequest.GET("/").basicAuth(MEMBER_ROLE, MEMBER_ROLE), Argument.listOf(EarnedCertification.class));
        assertTrue(list.isEmpty());
    }

    @Test
    void cannotDeleteOthersEarnedCertification() {
        MemberProfile member = createADefaultMemberProfile();
        MemberProfile tim = memberWithoutBoss("Tim");
        Certification certification = createDefaultCertification();
        LocalDate earnedDate = LocalDate.now();
        EarnedCertification earnedCertification = createEarnedCertification(member, certification, "Description", earnedDate);

        HttpClientResponseException exception = assertThrows(HttpClientResponseException.class, () -> earnedCertificationClient.toBlocking().exchange(HttpRequest.DELETE("/%s".formatted(earnedCertification.getId())).basicAuth(tim.getWorkEmail(), MEMBER_ROLE)));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("User %s does not have permission to delete Earned Certificate for user %s".formatted(tim.getId(), earnedCertification.getMemberId()), exception.getMessage());
    }

    @Test
    void cannotInvalidEarnedCertification() {
        UUID uuid = UUID.randomUUID();
        HttpClientResponseException exception = assertThrows(HttpClientResponseException.class, () -> earnedCertificationClient.toBlocking().exchange(HttpRequest.DELETE("/%s".formatted(uuid)).basicAuth(MEMBER_ROLE, MEMBER_ROLE)));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Earned Certificate %s not found".formatted(uuid), exception.getMessage());
    }

    @Test
    void checkValidation() {
        MemberProfile member = createADefaultMemberProfile();
        Certification certification = createDefaultCertification();
        LocalDate earnedDate = LocalDate.now();
        EarnedCertificationDTO newEarnedCertification = new EarnedCertificationDTO(member.getId(), certification.getId(), "Description", earnedDate);

        // No member ID
        newEarnedCertification.setMemberId(null);
        HttpClientResponseException exception = assertThrows(HttpClientResponseException.class, () -> create(newEarnedCertification, member.getWorkEmail(), MEMBER_ROLE));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        String body = exception.getResponse().getBody(String.class).get();
        assertTrue(body.contains("certification.memberId: must not be null"), body + " should contain 'certification.memberId: must not be null'");

        // No certification ID
        newEarnedCertification.setMemberId(member.getId());
        newEarnedCertification.setCertificationId(null);
        exception = assertThrows(HttpClientResponseException.class, () -> create(newEarnedCertification, member.getWorkEmail(), MEMBER_ROLE));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        body = exception.getResponse().getBody(String.class).get();
        assertTrue(body.contains("certification.certificationId: must not be null"), body + " should contain 'certification.certificationId: must not be null'");

        // Null description
        newEarnedCertification.setCertificationId(certification.getId());
        newEarnedCertification.setDescription(null);
        exception = assertThrows(HttpClientResponseException.class, () -> create(newEarnedCertification, member.getWorkEmail(), MEMBER_ROLE));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        body = exception.getResponse().getBody(String.class).get();
        assertTrue(body.contains("certification.description: must not be blank"), body + " should contain 'certification.description: must not be null'");

        // Empty description
        newEarnedCertification.setDescription("");
        exception = assertThrows(HttpClientResponseException.class, () -> create(newEarnedCertification, member.getWorkEmail(), MEMBER_ROLE));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        body = exception.getResponse().getBody(String.class).get();
        assertTrue(body.contains("certification.description: must not be blank"), body + " should contain 'certification.description: must not be null'");

        // No earned date
        newEarnedCertification.setDescription("Description");
        newEarnedCertification.setEarnedDate(null);
        exception = assertThrows(HttpClientResponseException.class, () -> create(newEarnedCertification, member.getWorkEmail(), MEMBER_ROLE));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    void canFindAllEarnedCertifications() {
        MemberProfile tim = memberWithoutBoss("Tim");
        MemberProfile sarah = memberWithoutBoss("Sarah");
        Certification source = createCertification("Source");
        Certification target = createCertification("Target");
        createEarnedCertification(tim, source, "Tim's source certification", LocalDate.now().minusDays(1));
        createEarnedCertification(tim, target, "Tim's target certification", LocalDate.now().minusDays(10));
        createEarnedCertification(sarah, target, "Sarah's target certification", LocalDate.now());

        List<EarnedCertification> list = earnedCertificationClient.toBlocking().retrieve(HttpRequest.GET("/").basicAuth(MEMBER_ROLE, MEMBER_ROLE), Argument.listOf(EarnedCertification.class));

        assertEquals(3, list.size());
        assertEquals(List.of("Sarah's target certification", "Tim's source certification", "Tim's target certification"), list.stream().map(EarnedCertification::getDescription).toList());
    }

    @Test
    void sensibleOutputForInvalidMemberId() {
        Certification certification = createDefaultCertification();
        UUID memberId = UUID.randomUUID();
        EarnedCertificationDTO newEarnedCertification = new EarnedCertificationDTO(memberId, certification.getId(), "Description", LocalDate.now());

        HttpClientResponseException exception = assertThrows(HttpClientResponseException.class, () -> create(newEarnedCertification, ADMIN_ROLE, ADMIN_ROLE));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Member %s doesn't exist".formatted(memberId), exception.getMessage());
    }

    @Test
    void sensibleOutputForInvalidCertificateId() {
        MemberProfile member = createADefaultMemberProfile();
        UUID certificationId = UUID.randomUUID();
        EarnedCertificationDTO newEarnedCertification = new EarnedCertificationDTO(member.getId(), certificationId, "Description", LocalDate.now());

        HttpClientResponseException exception = assertThrows(HttpClientResponseException.class, () -> create(newEarnedCertification, member.getWorkEmail(), MEMBER_ROLE));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Certification %s doesn't exist".formatted(certificationId), exception.getMessage());
    }

    @Test
    void canFindEarnedCertificationsByCertification() {
        MemberProfile tim = memberWithoutBoss("Tim");
        MemberProfile sarah = memberWithoutBoss("Sarah");
        Certification source = createCertification("Source");
        Certification target = createCertification("Target");
        createEarnedCertification(tim, source, "Tim's source certification", LocalDate.now().minusDays(1));
        createEarnedCertification(tim, target, "Tim's target certification", LocalDate.now().minusDays(10));
        createEarnedCertification(sarah, target, "Sarah's target certification", LocalDate.now());

        List<EarnedCertification> sourceCertifications = earnedCertificationClient.toBlocking().retrieve(HttpRequest.GET("/?certificationId=%s".formatted(source.getId())).basicAuth(MEMBER_ROLE, MEMBER_ROLE), Argument.listOf(EarnedCertification.class));
        assertEquals(1, sourceCertifications.size());
        assertEquals("Tim's source certification", sourceCertifications.getFirst().getDescription());

        List<EarnedCertification> targetCertifications = earnedCertificationClient.toBlocking().retrieve(HttpRequest.GET("/?certificationId=%s".formatted(target.getId())).basicAuth(MEMBER_ROLE, MEMBER_ROLE), Argument.listOf(EarnedCertification.class));
        assertEquals(2, targetCertifications.size());
        assertEquals(List.of("Sarah's target certification", "Tim's target certification"), targetCertifications.stream().map(EarnedCertification::getDescription).toList());
    }

    @Test
    void canFindEarnedCertificationsByMember() {
        MemberProfile tim = memberWithoutBoss("Tim");
        MemberProfile sarah = memberWithoutBoss("Sarah");
        Certification source = createCertification("Source");
        Certification target = createCertification("Target");
        createEarnedCertification(tim, source, "Tim's source certification", LocalDate.now().minusDays(1));
        createEarnedCertification(tim, target, "Tim's target certification", LocalDate.now().minusDays(10));
        createEarnedCertification(sarah, target, "Sarah's target certification", LocalDate.now());

        List<EarnedCertification> timCertifications = earnedCertificationClient.toBlocking().retrieve(HttpRequest.GET("/?memberId=%s".formatted(tim.getId())).basicAuth(MEMBER_ROLE, MEMBER_ROLE), Argument.listOf(EarnedCertification.class));
        assertEquals(2, timCertifications.size());
        assertEquals(List.of("Tim's source certification", "Tim's target certification"), timCertifications.stream().map(EarnedCertification::getDescription).toList());

        List<EarnedCertification> sarahCertifications = earnedCertificationClient.toBlocking().retrieve(HttpRequest.GET("/?memberId=%s".formatted(sarah.getId())).basicAuth(MEMBER_ROLE, MEMBER_ROLE), Argument.listOf(EarnedCertification.class));
        assertEquals(1, sarahCertifications.size());
        assertEquals("Sarah's target certification", sarahCertifications.getFirst().getDescription());
    }

    @Test
    void canFindEarnedCertificationsByMemberAndCertification() {
        MemberProfile tim = memberWithoutBoss("Tim");
        MemberProfile sarah = memberWithoutBoss("Sarah");
        Certification source = createCertification("Source");
        Certification target = createCertification("Target");
        createEarnedCertification(tim, source, "Tim's source certification", LocalDate.now().minusDays(1));
        createEarnedCertification(tim, target, "Tim's target certification", LocalDate.now().minusDays(10));
        createEarnedCertification(sarah, target, "Sarah's target certification", LocalDate.now());

        List<EarnedCertification> timSource = earnedCertificationClient.toBlocking().retrieve(HttpRequest.GET("/?memberId=%s&certificationId=%s".formatted(tim.getId(), source.getId())).basicAuth(MEMBER_ROLE, MEMBER_ROLE), Argument.listOf(EarnedCertification.class));

        assertEquals(1, timSource.size());
        assertEquals("Tim's source certification", timSource.getFirst().getDescription());

        List<EarnedCertification> timTarget = earnedCertificationClient.toBlocking().retrieve(HttpRequest.GET("/?memberId=%s&certificationId=%s".formatted(tim.getId(), target.getId())).basicAuth(MEMBER_ROLE, MEMBER_ROLE), Argument.listOf(EarnedCertification.class));

        assertEquals(1, timTarget.size());
        assertEquals("Tim's target certification", timTarget.getFirst().getDescription());
    }

    @Test
    void canMerge() {
        MemberProfile tim = memberWithoutBoss("Tim");
        MemberProfile sarah = memberWithoutBoss("Sarah");
        Certification source = createCertification("Source");
        Certification target = createCertification("Target");
        createEarnedCertification(tim, source, "Tim's source certification", LocalDate.now().minusDays(1));
        createEarnedCertification(tim, target, "Tim's target certification", LocalDate.now().minusDays(10));
        createEarnedCertification(sarah, target, "Sarah's target certification", LocalDate.now());

        // Tim has one certification assigned from the source certification
        List<EarnedCertification> list = earnedCertificationClient.toBlocking().retrieve(HttpRequest.GET("/?memberId=%s".formatted(tim.getId())).basicAuth(tim.getWorkEmail(), MEMBER_ROLE), Argument.listOf(EarnedCertification.class));
        assertEquals(2, list.size());
        assertEquals(source.getId(), list.getFirst().getCertificationId());
        assertEquals(target.getId(), list.getLast().getCertificationId());
        assertEquals(List.of("Tim's source certification", "Tim's target certification"), list.stream().map(EarnedCertification::getDescription).toList());

        CertificationMergeDTO mergeDTO = new CertificationMergeDTO(source.getId(), target.getId());

        // We cannot merge without the correct permission
        HttpClientResponseException exception = assertThrows(HttpClientResponseException.class, () -> merge(mergeDTO, tim.getWorkEmail(), MEMBER_ROLE));
        assertEquals(HttpStatus.FORBIDDEN, exception.getStatus());

        // When we merge source into target
        Certification exchange = merge(mergeDTO, ADMIN_ROLE, ADMIN_ROLE);
        assertEquals(exchange.getId(), target.getId());

        // Both of Tim's certifications should now have the target certification with the same details
        list = earnedCertificationClient.toBlocking().retrieve(HttpRequest.GET("/?memberId=%s".formatted(tim.getId())).basicAuth(tim.getWorkEmail(), MEMBER_ROLE), Argument.listOf(EarnedCertification.class));
        assertEquals(2, list.size());
        assertEquals(target.getId(), list.getFirst().getCertificationId());
        assertEquals(target.getId(), list.getLast().getCertificationId());
        assertEquals(List.of("Tim's source certification", "Tim's target certification"), list.stream().map(EarnedCertification::getDescription).toList());

        // And when we list all of them, we should see that the source certification is gone
        List<Certification> certificationList = certificationClient.toBlocking().retrieve(HttpRequest.GET("/").basicAuth(tim.getWorkEmail(), MEMBER_ROLE), Argument.listOf(Certification.class));
        assertEquals(1, certificationList.size());
        assertEquals(target.getId(), certificationList.getFirst().getId());

        // And the earned certifications should be updated
        List<EarnedCertification> earnedCertifications = earnedCertificationClient.toBlocking().retrieve(HttpRequest.GET("/").basicAuth(tim.getWorkEmail(), MEMBER_ROLE), Argument.listOf(EarnedCertification.class));
        assertEquals(3, earnedCertifications.size());
        assertAll(earnedCertifications.stream().map(c -> () -> assertEquals(target.getId(), c.getCertificationId())));

        // And they are sorted by earned date descending
        assertEquals(List.of("Sarah's target certification", "Tim's source certification", "Tim's target certification"), earnedCertifications.stream().map(EarnedCertification::getDescription).toList());
    }

    @Test
    void mergeValidationTest() {
        CertificationMergeDTO merge = new CertificationMergeDTO(null, UUID.randomUUID());
        HttpClientResponseException exception = assertThrows(HttpClientResponseException.class, () -> merge(merge, ADMIN_ROLE, ADMIN_ROLE));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        String body = exception.getResponse().getBody(String.class).get();
        assertTrue(body.contains("certificationMergeDTO.sourceId: must not be null"), body + " should contain 'certificationMergeDTO.sourceId: must not be null'");

        CertificationMergeDTO merge2 = new CertificationMergeDTO(UUID.randomUUID(), null);
        exception = assertThrows(HttpClientResponseException.class, () -> merge(merge2, ADMIN_ROLE, ADMIN_ROLE));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        body = exception.getResponse().getBody(String.class).get();
        assertTrue(body.contains("certificationMergeDTO.targetId: must not be null"), body + " should contain 'certificationMergeDTO.targetId: must not be null'");
    }

    @Test
    void testUnknownIdsInMerge() {
        CertificationMergeDTO mergeDto = new CertificationMergeDTO(UUID.randomUUID(), UUID.randomUUID());
        HttpClientResponseException exception = assertThrows(HttpClientResponseException.class, () -> merge(mergeDto, ADMIN_ROLE, ADMIN_ROLE));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Target certification %s not found".formatted(mergeDto.getTargetId()), exception.getMessage());

        Certification target = createDefaultCertification();
        mergeDto.setTargetId(target.getId());
        exception = assertThrows(HttpClientResponseException.class, () -> merge(mergeDto, ADMIN_ROLE, ADMIN_ROLE));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Source certification %s not found".formatted(mergeDto.getSourceId()), exception.getMessage());
    }

    private Certification merge(CertificationMergeDTO mergeDTO, @NotNull String workEmail, String memberRole) {
        return certificationClient.toBlocking().retrieve(HttpRequest.POST("/merge", new CertificationMergeDTO(mergeDTO.getSourceId(), mergeDTO.getTargetId())).basicAuth(workEmail, memberRole), Certification.class);
    }

    private <T> EarnedCertification create(T body, String workEmail, String memberRole) {
        return earnedCertificationClient.toBlocking().retrieve(HttpRequest.POST("/", body).basicAuth(workEmail, memberRole), EarnedCertification.class);
    }
}
