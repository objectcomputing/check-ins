package com.objectcomputing.checkins.services.checkindocument;

import com.objectcomputing.checkins.exceptions.BadArgException;
import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.CurrentUserServicesReplacement;
import com.objectcomputing.checkins.services.fixture.MemberProfileFixture;
import com.objectcomputing.checkins.services.fixture.CheckInDocumentFixture;
import com.objectcomputing.checkins.services.fixture.CheckInFixture;
import com.objectcomputing.checkins.services.fixture.RoleFixture;
import com.objectcomputing.checkins.services.checkins.CheckIn;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.role.RoleType;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.inject.Inject;
import io.micronaut.context.annotation.Property;
import io.micronaut.core.util.StringUtils;

import java.util.Set;
import java.util.UUID;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Property(name = "replace.currentuserservices", value = StringUtils.TRUE)
class CheckinDocumentServiceImplTest extends TestContainersSuite
                                     implements MemberProfileFixture, CheckInFixture, CheckInDocumentFixture, RoleFixture {
    @Inject
    CurrentUserServicesReplacement currentUserServices;

    @Inject
    private CheckinDocumentServicesImpl services;

    private MemberProfile pdl;
    private MemberProfile member;
    private CheckIn checkIn;

    @BeforeEach
    void reset() {
        pdl = createADefaultMemberProfile();
        member = createADefaultMemberProfileForPdl(pdl);
        checkIn = createADefaultCheckIn(member, pdl);
        currentUserServices.currentUser = member;
        createAndAssignRoles();
        assignAdminRole(member);
    }

    @Test
    void testRead() {
        Set<CheckinDocument> checkinDocumentSet = Set.of(
                createACustomCheckInDocument(checkIn, "doc1"),
                createACustomCheckInDocument(checkIn, "doc2"),
                createACustomCheckInDocument(checkIn, "doc3")
        );

        assertEquals(checkinDocumentSet, services.read(checkIn.getId()));
    }

    @Test
    void testReadNullId() {
        assertTrue(services.read(null).isEmpty());
    }

    @Test
    void testFindByUploadDocId() {
        CheckinDocument cd = createADefaultCheckInDocument(checkIn);
        assertEquals(cd, services.getFindByUploadDocId(cd.getUploadDocId()));
    }

    @Test
    void testFindByUploadDocIdWhenRecordDoesNotExist() {
        String id = "some.id";
        BadArgException exception = assertThrows(BadArgException.class, () -> services.getFindByUploadDocId(id));
        assertEquals(String.format("CheckinDocument with document id %s does not exist", id), exception.getMessage());
    }

    @Test
    void testSave() {
        CheckinDocument cd = new CheckinDocument(checkIn.getId(), "doc1");
        assertEquals(cd, services.save(cd));
    }

    @Test
    void testSaveWithId() {
        CheckinDocument cd = createADefaultCheckInDocument(checkIn);

        BadArgException exception = assertThrows(BadArgException.class, () -> services.save(cd));
        assertEquals(String.format("Found unexpected CheckinDocument id %s, please try updating instead", cd.getId()), exception.getMessage());
    }

    @Test
    void testSaveCheckinDocumentNullCheckinsId() {
        CheckinDocument cd = new CheckinDocument(null, "docId");

        BadArgException exception = assertThrows(BadArgException.class, () -> services.save(cd));
        assertEquals(String.format("Invalid CheckinDocument %s", cd), exception.getMessage());
    }

    @Test
    void testSaveCheckinDocumentNullUploadDocId() {
        CheckinDocument cd = new CheckinDocument(checkIn.getId(), null);

        BadArgException exception = assertThrows(BadArgException.class, () -> services.save(cd));
        assertEquals(String.format("Invalid CheckinDocument %s", cd), exception.getMessage());
    }

    @Test
    void testSaveNullCheckinDocument() {
        assertNull(services.save(null));
    }

    @Test
    void testSaveCheckinDocumentNonExistingCheckIn() {
        CheckinDocument cd = new CheckinDocument(UUID.randomUUID(), "docId");

        BadArgException exception = assertThrows(BadArgException.class, () -> services.save(cd));
        assertEquals(String.format("CheckIn %s doesn't exist", cd.getCheckinsId()), exception.getMessage());
    }

    @Test
    void testSaveCheckinDocumentExistingUploadDocId() {
        String docId = "doc1";
        CheckinDocument existing = createACustomCheckInDocument(checkIn, docId);

        CheckinDocument cd = new CheckinDocument(checkIn.getId(), docId);
        BadArgException exception = assertThrows(BadArgException.class, () -> services.save(cd));
        assertEquals(String.format("CheckinDocument with document ID %s already exists", cd.getUploadDocId()), exception.getMessage());
    }

    @Test
    void testUpdate() {
        CheckinDocument cd = createADefaultCheckInDocument(checkIn);
        assertEquals(cd, services.update(cd));
    }

    @Test
    void testUpdateWithoutId() {
        CheckinDocument cd = new CheckinDocument(checkIn.getId(), "docId");

        BadArgException exception = assertThrows(BadArgException.class, () -> services.update(cd));
        assertEquals(String.format("CheckinDocument id %s not found, please try inserting instead", cd.getId()), exception.getMessage());
    }

    @Test
    void testUpdateCheckinDocumentNullCheckinsId() {
        CheckinDocument cd = new CheckinDocument(null, "docId");

        BadArgException exception = assertThrows(BadArgException.class, () -> services.update(cd));
        assertEquals(String.format("Invalid CheckinDocument %s", cd), exception.getMessage());
    }

    @Test
    void testUpdateCheckinDocumentNullUploadDocId() {
        CheckinDocument cd = new CheckinDocument(checkIn.getId(), null);

        BadArgException exception = assertThrows(BadArgException.class, () -> services.update(cd));
        assertEquals(String.format("Invalid CheckinDocument %s", cd), exception.getMessage());
    }

    @Test
    void testUpdateCheckinDocumentDoesNotExist() {
        CheckinDocument cd = new CheckinDocument(UUID.randomUUID(), UUID.randomUUID(), "docId");

        BadArgException exception = assertThrows(BadArgException.class, () -> services.update(cd));
        assertEquals(String.format("CheckinDocument id %s not found, please try inserting instead", cd.getId()), exception.getMessage());
    }

    @Test
    void testUpdateCheckInDoesNotExist() {
        CheckinDocument existing = createADefaultCheckInDocument(checkIn);
        CheckinDocument cd = new CheckinDocument(existing.getId(),
                                                 UUID.randomUUID(), "docId");

        BadArgException exception = assertThrows(BadArgException.class, () -> services.update(cd));
        assertEquals(String.format("CheckIn %s doesn't exist", cd.getCheckinsId()), exception.getMessage());
    }

    @Test
    void testUpdateNullCheckinDocument() {
        assertNull(services.update(null));
    }

    @Test
    void testDeleteByCheckinId() {
        CheckinDocument toBeDeleted = createADefaultCheckInDocument(checkIn);
        services.deleteByCheckinId(checkIn.getId());
    }

    @Test
    void testDeleteNonExistingCheckinsId() {
        UUID uuid = UUID.randomUUID();

        BadArgException exception = assertThrows(BadArgException.class, () -> services.deleteByCheckinId(uuid));
        assertEquals(String.format("CheckinDocument with CheckinsId %s does not exist", uuid), exception.getMessage());
    }

    @Test
    void testDeleteByUploadDocId() {
        CheckinDocument toBeDeleted = createADefaultCheckInDocument(checkIn);
        services.deleteByUploadDocId(toBeDeleted.getUploadDocId());
    }

    @Test
    void testDeleteNonExistingUploadDocId() {
        String id = "Test.Id";
        BadArgException exception = assertThrows(BadArgException.class, () -> services.deleteByUploadDocId(id));

        assertEquals(String.format("CheckinDocument with uploadDocId %s does not exist", id), exception.getMessage());
    }
}

