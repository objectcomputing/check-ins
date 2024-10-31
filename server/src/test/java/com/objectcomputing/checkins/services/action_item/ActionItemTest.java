package com.objectcomputing.checkins.services.action_item;

import com.objectcomputing.checkins.services.TestContainersSuite;
import io.micronaut.validation.validator.Validator;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ActionItemTest extends TestContainersSuite {

    @Inject
    private Validator validator;


    @Test
    void testActionItemInstantiation() {
        final UUID checkinId = UUID.randomUUID();
        final UUID createById = UUID.randomUUID();
        final String description = "dnc";
        ActionItem actionItem = new ActionItem(checkinId, createById, description);
        assertEquals(checkinId, actionItem.getCheckinid());
        assertEquals(createById, actionItem.getCreatedbyid());
        assertEquals(description, actionItem.getDescription());
    }

    @Test
    void testActionItemInstantiation2() {
        final UUID id = UUID.randomUUID();
        final UUID checkinId = UUID.randomUUID();
        final UUID createById = UUID.randomUUID();
        final String description = "dnc";
        ActionItem actionItem = new ActionItem(id, checkinId, createById, description);
        assertEquals(id, actionItem.getId());
        assertEquals(checkinId, actionItem.getCheckinid());
        assertEquals(createById, actionItem.getCreatedbyid());
        assertEquals(actionItem.getDescription(), description);

        Set<ConstraintViolation<ActionItem>> violations = validator.validate(actionItem);
        assertTrue(violations.isEmpty());
    }


    @Test
    void testConstraintViolation() {
        final UUID id = UUID.randomUUID();
        final UUID checkinId = UUID.randomUUID();
        final UUID createById = UUID.randomUUID();
        final String description = "dnc";
        ActionItem actionItem = new ActionItem(id, checkinId, createById, description);

        actionItem.setCheckinid(null);
        actionItem.setCreatedbyid(null);

        Set<ConstraintViolation<ActionItem>> violations = validator.validate(actionItem);
        assertEquals(2, violations.size());
        for (ConstraintViolation<ActionItem> violation : violations) {
            assertEquals("must not be null", violation.getMessage());
        }
    }

    @Test
    void testEquals() {
        final UUID id = UUID.randomUUID();
        final UUID checkinId = UUID.randomUUID();
        final UUID createById = UUID.randomUUID();
        final String description = "dnc";
        ActionItem a = new ActionItem(id, checkinId, createById, description);
        ActionItem a2 = new ActionItem(id, checkinId, createById, description);

        assertEquals(a, a2);

        a2.setId(null);

        assertNotEquals(a, a2);

        a2.setId(a.getId());

        assertEquals(a, a2);

        a2.setDescription("dnc2");

        assertNotEquals(a, a2);
    }

    @Test
    void testHash() {
        HashMap<ActionItem, Boolean> map = new HashMap<>();
        final UUID id = UUID.randomUUID();
        final UUID checkinId = UUID.randomUUID();
        final UUID createById = UUID.randomUUID();
        final String description = "dnc";
        ActionItem a = new ActionItem(id, checkinId, createById, description);

        map.put(a, true);

        assertTrue(map.get(a));
    }

    @Test
    void testToString() {
        final UUID id = UUID.randomUUID();
        final UUID checkinId = UUID.randomUUID();
        final UUID createById = UUID.randomUUID();
        final String description = "dnc";
        ActionItem a = new ActionItem(id, checkinId, createById, description);

        String toString = a.toString();
        assertTrue(toString.contains(checkinId.toString()));
        assertTrue(toString.contains(id.toString()));
        assertTrue(toString.contains(createById.toString()));
        assertTrue(toString.contains(description));
    }
}
