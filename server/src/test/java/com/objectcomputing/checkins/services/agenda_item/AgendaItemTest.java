package com.objectcomputing.checkins.services.agenda_item;

import com.objectcomputing.checkins.services.TestContainersSuite;
import io.micronaut.validation.validator.Validator;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class AgendaItemTest extends TestContainersSuite {

    @Inject
    private Validator validator;


    @Test
    void testAgendaItemInstantiation() {
        final UUID checkinId = UUID.randomUUID();
        final UUID createById = UUID.randomUUID();
        final String description = "dnc";
        AgendaItem agendaItem = new AgendaItem(checkinId, createById, description);
        assertEquals(checkinId, agendaItem.getCheckinid());
        assertEquals(createById, agendaItem.getCreatedbyid());
        assertEquals(description, agendaItem.getDescription());
    }

    @Test
    void testAgendaItemInstantiation2() {
        final UUID id = UUID.randomUUID();
        final UUID checkinId = UUID.randomUUID();
        final UUID createById = UUID.randomUUID();
        final String description = "dnc";
        AgendaItem agendaItem = new AgendaItem(id, checkinId, createById, description);
        assertEquals(id, agendaItem.getId());
        assertEquals(checkinId, agendaItem.getCheckinid());
        assertEquals(createById, agendaItem.getCreatedbyid());
        assertEquals(agendaItem.getDescription(), description);

        Set<ConstraintViolation<AgendaItem>> violations = validator.validate(agendaItem);
        assertTrue(violations.isEmpty());
    }


    @Test
    void testConstraintViolation() {
        final UUID id = UUID.randomUUID();
        final UUID checkinId = UUID.randomUUID();
        final UUID createById = UUID.randomUUID();
        final String description = "dnc";
        AgendaItem agendaItem = new AgendaItem(id, checkinId, createById, description);

        agendaItem.setCheckinid(null);
        agendaItem.setCreatedbyid(null);

        Set<ConstraintViolation<AgendaItem>> violations = validator.validate(agendaItem);
        assertEquals(2, violations.size());
        for (ConstraintViolation<AgendaItem> violation : violations) {
            assertEquals(violation.getMessage(), "must not be null");
        }
    }

    @Test
    void testEquals() {
        final UUID id = UUID.randomUUID();
        final UUID checkinId = UUID.randomUUID();
        final UUID createById = UUID.randomUUID();
        final String description = "dnc";
        AgendaItem a = new AgendaItem(id, checkinId, createById, description);
        AgendaItem a2 = new AgendaItem(id, checkinId, createById, description);

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
        HashMap<AgendaItem, Boolean> map = new HashMap<>();
        final UUID id = UUID.randomUUID();
        final UUID checkinId = UUID.randomUUID();
        final UUID createById = UUID.randomUUID();
        final String description = "dnc";
        AgendaItem a = new AgendaItem(id, checkinId, createById, description);

        map.put(a, true);

        assertTrue(map.get(a));
    }

    @Test
    void testToString() {
        final UUID id = UUID.randomUUID();
        final UUID checkinId = UUID.randomUUID();
        final UUID createById = UUID.randomUUID();
        final String description = "dnc";
        AgendaItem a = new AgendaItem(id, checkinId, createById, description);

        String toString = a.toString();
        assertTrue(toString.contains(checkinId.toString()));
        assertTrue(toString.contains(id.toString()));
        assertTrue(toString.contains(createById.toString()));
        assertTrue(toString.contains(description));
    }
}
