package com.objectcomputing.checkins.services.team;

import io.micronaut.test.annotation.MicronautTest;
import io.micronaut.validation.validator.Validator;
import nu.studer.sample.tables.pojos.Team;
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
class TeamTest {

    @Inject
    private Validator validator;


    @Test
    void testTeamInstantiation() {
        final String name = "name";
        final String description = "description";
        Team teamEntity = new Team(null, name, description);
        assertEquals(teamEntity.getName(), name);
        assertEquals(teamEntity.getDescription(), description);
    }

    @Test
    void testTeamInstantiation2() {
        final UUID uuid = UUID.randomUUID();
        final String name = "name";
        final String description = "description";
        Team teamEntity = new Team(uuid.toString(), name, description);
        assertEquals(teamEntity.getId(), uuid.toString());
        assertEquals(teamEntity.getName(), name);
        assertEquals(teamEntity.getDescription(), description);

        Set<ConstraintViolation<Team>> violations = validator.validate(teamEntity);
        assertTrue(violations.isEmpty());
    }


    @Test
    void testConstraintViolation() {
        final UUID uuid = UUID.randomUUID();
        final String name = "name";
        final String description = "description";
        Team teamEntity = new Team(uuid.toString(), name, description);

        Set<ConstraintViolation<Team>> violations = validator.validate(new Team(teamEntity.getId(), "", ""));
        assertEquals(violations.size(), 2);
        for (ConstraintViolation<Team> violation : violations) {
            assertEquals(violation.getMessage(), "must not be blank");
        }
    }

    @Test
    void testEquals() {
        final UUID uuid = UUID.randomUUID();
        final String name = "name";
        final String description = "description";
        Team tm = new Team(uuid.toString(), name, description);
        Team tm2 = new Team(uuid.toString(), name, description);

        assertEquals(tm, tm2);

        Team nullId = new Team(null, tm2.getName(), tm2.getDescription());

        assertNotEquals(tm, nullId);
    }

    @Test
    void testHash() {
        HashMap<Team, Boolean> map = new HashMap<>();
        final UUID uuid = UUID.randomUUID();
        final String name = "name";
        final String description = "description";
        Team tm = new Team(uuid.toString(), name, description);

        map.put(tm, true);

        assertTrue(map.get(tm));
    }

    @Test
    void testToString() {
        final UUID uuid = UUID.randomUUID();
        final String name = "name------name";
        final String description = "description------description";
        Team tm = new Team(uuid.toString(), name, description);

        assertTrue(tm.toString().contains(name));
        assertTrue(tm.toString().contains(uuid.toString()));
        assertTrue(tm.toString().contains(description));
    }
}
