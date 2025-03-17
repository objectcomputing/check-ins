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
        EarnedCertificationDTO newEarnedCertification = new EarnedCertificationDTO(member.getId(), certification.getId(), earnedDate);

        EarnedCertification created = create(newEarnedCertification, member.getWorkEmail(), MEMBER_ROLE);
        assertNotNull(created.getId());
        assertEquals(member.getId(), created.getMemberId());
        assertEquals(certification.getId(), created.getCertificationId());
        assertEquals(earnedDate, created.getEarnedDate());
        assertNull(created.getExpirationDate());
        assertNull(created.getValidationUrl());
    }

    @Test
    void canCreateEarnedCertificationForOtherIfRoleApplied() {
        MemberProfile member = createADefaultMemberProfile();
        Certification certification = createDefaultCertification();
        LocalDate earnedDate = LocalDate.now();
        EarnedCertificationDTO newEarnedCertification = new EarnedCertificationDTO(member.getId(), certification.getId(), earnedDate);

        EarnedCertification created = create(newEarnedCertification, ADMIN_ROLE, ADMIN_ROLE);
        assertNotNull(created.getId());
        assertEquals(member.getId(), created.getMemberId());
        assertEquals(certification.getId(), created.getCertificationId());
        assertEquals(earnedDate, created.getEarnedDate());
        assertNull(created.getExpirationDate());
        assertNull(created.getValidationUrl());
    }

    @Test
    void cannotCreateEarnedCertificationForOther() {
        MemberProfile member = createADefaultMemberProfile();
        MemberProfile tim = memberWithoutBoss("Tim");
        Certification certification = createDefaultCertification();
        LocalDate earnedDate = LocalDate.now();
        EarnedCertificationDTO newEarnedCertification = new EarnedCertificationDTO(member.getId(), certification.getId(), earnedDate);

        HttpClientResponseException thrown = assertThrows(HttpClientResponseException.class, () -> create(newEarnedCertification, tim.getWorkEmail(), MEMBER_ROLE));
        assertEquals(HttpStatus.BAD_REQUEST, thrown.getStatus());
        assertEquals("User %s does not have permission to create Earned Certificate for user %s".formatted(tim.getId(), member.getId()), thrown.getMessage());
    }

    @Test
    void canUpdateEarnedCertification() {
        MemberProfile member = createADefaultMemberProfile();
        Certification certification = createDefaultCertification();
        LocalDate earnedDate = LocalDate.now();
        EarnedCertification earnedCertification = createEarnedCertification(member, certification, earnedDate);

        EarnedCertificationDTO update = new EarnedCertificationDTO(
                member.getId(),
                certification.getId(),
                earnedDate.minusDays(1),
                earnedDate.plusYears(2),
                "https://certificate.url"
        );

        EarnedCertification updated = earnedCertificationClient.toBlocking().retrieve(HttpRequest.PUT("/%s".formatted(earnedCertification.getId()), update).basicAuth(member.getWorkEmail(), MEMBER_ROLE), EarnedCertification.class);
        assertEquals(earnedCertification.getId(), updated.getId());
        assertEquals(member.getId(), updated.getMemberId());
        assertEquals(certification.getId(), updated.getCertificationId());
        assertEquals(earnedDate.minusDays(1), updated.getEarnedDate());
        assertEquals(earnedDate.plusYears(2), updated.getExpirationDate());
        assertEquals("https://certificate.url", updated.getValidationUrl());
    }

    @Test
    void canUpdateEarnedCertificationForOthersIfHavePermission() {
        MemberProfile member = createADefaultMemberProfile();
        Certification certification = createDefaultCertification();
        LocalDate earnedDate = LocalDate.now();
        EarnedCertification earnedCertification = createEarnedCertification(member, certification, earnedDate);

        EarnedCertificationDTO update = new EarnedCertificationDTO(
                member.getId(),
                certification.getId(),
                earnedDate.minusDays(1),
                earnedDate.plusYears(2),
                "https://certificate.url"
        );

        EarnedCertification updated = earnedCertificationClient.toBlocking().retrieve(HttpRequest.PUT("/%s".formatted(earnedCertification.getId()), update).basicAuth(ADMIN_ROLE, ADMIN_ROLE), EarnedCertification.class);
        assertEquals(earnedCertification.getId(), updated.getId());
        assertEquals(member.getId(), updated.getMemberId());
        assertEquals(certification.getId(), updated.getCertificationId());
        assertEquals(earnedDate.minusDays(1), updated.getEarnedDate());
        assertEquals(earnedDate.plusYears(2), updated.getExpirationDate());
        assertEquals("https://certificate.url", updated.getValidationUrl());
    }

    @Test
    void cannotUpdateEarnedCertificationForOthers() {
        MemberProfile member = createADefaultMemberProfile();
        MemberProfile tim = memberWithoutBoss("Tim");
        Certification certification = createDefaultCertification();
        LocalDate earnedDate = LocalDate.now();
        EarnedCertification earnedCertification = createEarnedCertification(member, certification, earnedDate);

        EarnedCertificationDTO update = new EarnedCertificationDTO(
                member.getId(),
                certification.getId(),
                earnedDate.minusDays(1),
                earnedDate.plusYears(2),
                "https://certificate.url"
        );
        HttpClientResponseException exception = assertThrows(HttpClientResponseException.class, () -> earnedCertificationClient.toBlocking().retrieve(HttpRequest.PUT("/%s".formatted(earnedCertification.getId()), update).basicAuth(tim.getWorkEmail(), MEMBER_ROLE)));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("User %s does not have permission to update Earned Certificate for user %s".formatted(tim.getId(), member.getId()), exception.getMessage());
    }

    @Test
    void cannotUpdateEarnedCertificationToSomeoneElse() {
        MemberProfile tim = memberWithoutBoss("Tim");
        MemberProfile sarah = memberWithoutBoss("Sarah");

        Certification certification = createDefaultCertification();
        EarnedCertification sarahsCertification = createEarnedCertification(sarah, certification, LocalDate.now());

        // Try to change the owner to me
        EarnedCertificationDTO update = new EarnedCertificationDTO(
                tim.getId(),
                sarahsCertification.getCertificationId(),
                sarahsCertification.getEarnedDate()
        );
        HttpClientResponseException exception = assertThrows(HttpClientResponseException.class, () ->
                earnedCertificationClient.toBlocking()
                        .retrieve(HttpRequest.PUT("/%s".formatted(sarahsCertification.getId()), update)
                                .basicAuth(sarah.getWorkEmail(), MEMBER_ROLE))
        );
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("User %s does not have permission to update Earned Certificate for user %s".formatted(sarah.getId(), tim.getId()), exception.getMessage());
    }

    @Test
    void cannotUpdateSomeoneElsesEarnedCertificateToMe() {
        MemberProfile tim = memberWithoutBoss("Tim");
        MemberProfile sarah = memberWithoutBoss("Sarah");

        Certification certification = createDefaultCertification();
        EarnedCertification sarahsCertification = createEarnedCertification(sarah, certification, LocalDate.now());

        // Try to change the owner to me
        EarnedCertificationDTO update = new EarnedCertificationDTO(
                tim.getId(),
                sarahsCertification.getCertificationId(),
                sarahsCertification.getEarnedDate()
        );
        HttpClientResponseException exception = assertThrows(HttpClientResponseException.class, () ->
                earnedCertificationClient.toBlocking()
                        .retrieve(HttpRequest.PUT("/%s".formatted(sarahsCertification.getId()), update)
                        .basicAuth(tim.getWorkEmail(), MEMBER_ROLE))
        );
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("User %s does not have permission to update Earned Certificate for user %s".formatted(tim.getId(), sarah.getId()), exception.getMessage());
    }

    @Test
    void canDeleteEarnedCertificationWithRole() {
        MemberProfile member = createADefaultMemberProfile();
        Certification certification = createDefaultCertification();
        LocalDate earnedDate = LocalDate.now();
        EarnedCertification earnedCertification = createEarnedCertification(member, certification, earnedDate);

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
        EarnedCertification earnedCertification = createEarnedCertification(member, certification, earnedDate);

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
        EarnedCertification earnedCertification = createEarnedCertification(member, certification, earnedDate);

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
        EarnedCertificationDTO newEarnedCertification = new EarnedCertificationDTO(member.getId(), certification.getId(), earnedDate);

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

        // No earned date
        newEarnedCertification.setEarnedDate(null);
        exception = assertThrows(HttpClientResponseException.class, () -> create(newEarnedCertification, member.getWorkEmail(), MEMBER_ROLE));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    void canFindAllEarnedCertifications() {
        MemberProfile tim = memberWithoutBoss("Tim");
        MemberProfile sarah = memberWithoutBoss("Sarah");
        Certification source = createCertification("Source", "Source certification");
        Certification target = createCertification("Target", "Target certification");
        createEarnedCertification(tim, source, LocalDate.now().minusDays(1));
        createEarnedCertification(tim, target, LocalDate.now().minusDays(10));
        createEarnedCertification(sarah, target, LocalDate.now());

        List<EarnedCertification> list = earnedCertificationClient.toBlocking().retrieve(HttpRequest.GET("/").basicAuth(MEMBER_ROLE, MEMBER_ROLE), Argument.listOf(EarnedCertification.class));

        assertEquals(3, list.size());
    }

    @Test
    void sensibleOutputForInvalidMemberId() {
        Certification certification = createDefaultCertification();
        UUID memberId = UUID.randomUUID();
        EarnedCertificationDTO newEarnedCertification = new EarnedCertificationDTO(memberId, certification.getId(), LocalDate.now());

        HttpClientResponseException exception = assertThrows(HttpClientResponseException.class, () -> create(newEarnedCertification, ADMIN_ROLE, ADMIN_ROLE));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Member %s doesn't exist".formatted(memberId), exception.getMessage());
    }

    @Test
    void sensibleOutputForInvalidCertificateId() {
        MemberProfile member = createADefaultMemberProfile();
        UUID certificationId = UUID.randomUUID();
        EarnedCertificationDTO newEarnedCertification = new EarnedCertificationDTO(member.getId(), certificationId, LocalDate.now());

        HttpClientResponseException exception = assertThrows(HttpClientResponseException.class, () -> create(newEarnedCertification, member.getWorkEmail(), MEMBER_ROLE));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Certification %s doesn't exist".formatted(certificationId), exception.getMessage());
    }

    @Test
    void canFindEarnedCertificationsByCertification() {
        MemberProfile tim = memberWithoutBoss("Tim");
        MemberProfile sarah = memberWithoutBoss("Sarah");
        Certification source = createCertification("Source", "Source certification");
        Certification target = createCertification("Target", "Target certification");
        createEarnedCertification(tim, source, LocalDate.now().minusDays(1));
        createEarnedCertification(tim, target, LocalDate.now().minusDays(10));
        createEarnedCertification(sarah, target, LocalDate.now());

        List<EarnedCertification> sourceCertifications = earnedCertificationClient.toBlocking().retrieve(HttpRequest.GET("/?certificationId=%s".formatted(source.getId())).basicAuth(MEMBER_ROLE, MEMBER_ROLE), Argument.listOf(EarnedCertification.class));
        assertEquals(1, sourceCertifications.size());

        List<EarnedCertification> targetCertifications = earnedCertificationClient.toBlocking().retrieve(HttpRequest.GET("/?certificationId=%s".formatted(target.getId())).basicAuth(MEMBER_ROLE, MEMBER_ROLE), Argument.listOf(EarnedCertification.class));
        assertEquals(2, targetCertifications.size());
    }

    @Test
    void canFindEarnedCertificationsByMember() {
        MemberProfile tim = memberWithoutBoss("Tim");
        MemberProfile sarah = memberWithoutBoss("Sarah");
        Certification source = createCertification("Source", "Source certification");
        Certification target = createCertification("Target", "Target certification");
        createEarnedCertification(tim, source, LocalDate.now().minusDays(1));
        createEarnedCertification(tim, target, LocalDate.now().minusDays(10));
        createEarnedCertification(sarah, target, LocalDate.now());

        List<EarnedCertification> timCertifications = earnedCertificationClient.toBlocking().retrieve(HttpRequest.GET("/?memberId=%s".formatted(tim.getId())).basicAuth(MEMBER_ROLE, MEMBER_ROLE), Argument.listOf(EarnedCertification.class));
        assertEquals(2, timCertifications.size());

        List<EarnedCertification> sarahCertifications = earnedCertificationClient.toBlocking().retrieve(HttpRequest.GET("/?memberId=%s".formatted(sarah.getId())).basicAuth(MEMBER_ROLE, MEMBER_ROLE), Argument.listOf(EarnedCertification.class));
        assertEquals(1, sarahCertifications.size());
    }

    @Test
    void canFindEarnedCertificationsByMemberAndCertification() {
        MemberProfile tim = memberWithoutBoss("Tim");
        MemberProfile sarah = memberWithoutBoss("Sarah");
        Certification source = createCertification("Source", "Source certification");
        Certification target = createCertification("Target", "Target certification");
        createEarnedCertification(tim, source, LocalDate.now().minusDays(1));
        createEarnedCertification(tim, target, LocalDate.now().minusDays(10));
        createEarnedCertification(sarah, target, LocalDate.now());

        List<EarnedCertification> timSource = earnedCertificationClient.toBlocking().retrieve(HttpRequest.GET("/?memberId=%s&certificationId=%s".formatted(tim.getId(), source.getId())).basicAuth(MEMBER_ROLE, MEMBER_ROLE), Argument.listOf(EarnedCertification.class));

        assertEquals(1, timSource.size());

        List<EarnedCertification> timTarget = earnedCertificationClient.toBlocking().retrieve(HttpRequest.GET("/?memberId=%s&certificationId=%s".formatted(tim.getId(), target.getId())).basicAuth(MEMBER_ROLE, MEMBER_ROLE), Argument.listOf(EarnedCertification.class));

        assertEquals(1, timTarget.size());
    }

    @Test
    void canMerge() {
        MemberProfile tim = memberWithoutBoss("Tim");
        MemberProfile sarah = memberWithoutBoss("Sarah");
        Certification source = createCertification("Source", "Source certification");
        Certification target = createCertification("Target", "Target certification");
        createEarnedCertification(tim, source, LocalDate.now().minusDays(1));
        createEarnedCertification(tim, target, LocalDate.now().minusDays(10));
        createEarnedCertification(sarah, target, LocalDate.now());

        // Tim has one certification assigned from the source certification
        List<EarnedCertification> list = earnedCertificationClient.toBlocking().retrieve(HttpRequest.GET("/?memberId=%s".formatted(tim.getId())).basicAuth(tim.getWorkEmail(), MEMBER_ROLE), Argument.listOf(EarnedCertification.class));
        assertEquals(2, list.size());
        assertEquals(source.getId(), list.getFirst().getCertificationId());
        assertEquals(target.getId(), list.getLast().getCertificationId());

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

        // And when we list all of them, we should see that the source certification is gone
        List<Certification> certificationList = certificationClient.toBlocking().retrieve(HttpRequest.GET("/").basicAuth(tim.getWorkEmail(), MEMBER_ROLE), Argument.listOf(Certification.class));
        assertEquals(1, certificationList.size());
        assertEquals(target.getId(), certificationList.getFirst().getId());

        // And the earned certifications should be updated
        List<EarnedCertification> earnedCertifications = earnedCertificationClient.toBlocking().retrieve(HttpRequest.GET("/").basicAuth(tim.getWorkEmail(), MEMBER_ROLE), Argument.listOf(EarnedCertification.class));
        assertEquals(3, earnedCertifications.size());
        assertAll(earnedCertifications.stream().map(c -> () -> assertEquals(target.getId(), c.getCertificationId())));
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
