package com.objectcomputing.checkins.services.action_item;

import io.micronaut.test.annotation.MicronautTest;
import io.micronaut.validation.validator.Validator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import java.util.HashMap;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@MicronautTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ActionItemTest {

    @Inject
    private Validator validator;


    @Test
    void testActionItemInstantiation() {
        final UUID checkinId = UUID.randomUUID();
        final UUID memberId = UUID.randomUUID();
        final String description = "dnc";
        ActionItem actionItem = new ActionItem(checkinId, memberId, description);
        assertEquals(checkinId, actionItem.getCheckinid());
        assertEquals(memberId, actionItem.getCreatedbyid());
        assertEquals(description, actionItem.getDescription());
    }

    @Test
    void testActionItemInstantiation2() {
        final UUID id = UUID.randomUUID();
        final UUID checkinId = UUID.randomUUID();
        final UUID memberId = UUID.randomUUID();
        final String description = "dnc";
        ActionItem actionItem = new ActionItem(id, checkinId, memberId, description);
        assertEquals(id, actionItem.getId());
        assertEquals(checkinId, actionItem.getCheckinid());
        assertEquals(memberId, actionItem.getCreatedbyid());
        assertEquals(actionItem.getDescription(), description);

        Set<ConstraintViolation<ActionItem>> violations = validator.validate(actionItem);
        assertTrue(violations.isEmpty());
    }


    @Test
    void testConstraintViolation() {
        final UUID id = UUID.randomUUID();
        final UUID checkinId = UUID.randomUUID();
        final UUID memberId = UUID.randomUUID();
        final String description = "dnc";
        ActionItem actionItem = new ActionItem(id, checkinId, memberId, description);

        actionItem.setCheckinid(null);
        actionItem.setCreatedbyid(null);

        Set<ConstraintViolation<ActionItem>> violations = validator.validate(actionItem);
        assertEquals(2, violations.size());
        for (ConstraintViolation<ActionItem> violation : violations) {
            assertEquals(violation.getMessage(), "must not be null");
        }
    }

    @Test
    void testEquals() {
        final UUID id = UUID.randomUUID();
        final UUID checkinId = UUID.randomUUID();
        final UUID memberId = UUID.randomUUID();
        final String description = "dnc";
        ActionItem g = new ActionItem(id, checkinId, memberId, description);
        ActionItem g2 = new ActionItem(id, checkinId, memberId, description);

        assertEquals(g, g2);

        g2.setId(null);

        assertNotEquals(g, g2);

        g2.setId(g.getId());

        assertEquals(g, g2);

        g2.setDescription("dnc2");

        assertNotEquals(g, g2);
    }

    @Test
    void testHash() {
        HashMap<ActionItem, Boolean> map = new HashMap<>();
        final UUID id = UUID.randomUUID();
        final UUID checkinId = UUID.randomUUID();
        final UUID memberId = UUID.randomUUID();
        final String description = "dnc";
        ActionItem g = new ActionItem(id, checkinId, memberId, description);

        map.put(g, true);

        assertTrue(map.get(g));
    }

    @Test
    void testToString() {
        final UUID id = UUID.randomUUID();
        final UUID checkinId = UUID.randomUUID();
        final UUID memberId = UUID.randomUUID();
        final String description = "dnc";
        ActionItem g = new ActionItem(id, checkinId, memberId, description);

        String toString = g.toString();
        assertTrue(toString.contains(checkinId.toString()));
        assertTrue(toString.contains(id.toString()));
        assertTrue(toString.contains(memberId.toString()));
        assertTrue(toString.contains(description));
    }
}
