package com.objectcomputing.checkins.services.team;

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
class TeamTest {

    @Inject
    private Validator validator;


    @Test
    void testTeamInstantiation() {
        final String name = "name";
        final String description = "description";
        Team team = new Team(name, description);
        assertEquals(team.getName(), name);
        assertEquals(team.getDescription(), description);
    }

    @Test
    void testTeamInstantiation2() {
        final UUID uuid = UUID.randomUUID();
        final String name = "name";
        final String description = "description";
        Team team = new Team(uuid, name, description);
        assertEquals(team.getUuid(), uuid);
        assertEquals(team.getName(), name);
        assertEquals(team.getDescription(), description);

        Set<ConstraintViolation<Team>> violations = validator.validate(team);
        assertTrue(violations.isEmpty());
    }


    @Test
    void testConstraintViolation() {
        final UUID uuid = UUID.randomUUID();
        final String name = "name";
        final String description = "description";
        Team team = new Team(uuid, name, description);

        team.setName("");
        team.setDescription("");

        Set<ConstraintViolation<Team>> violations = validator.validate(team);
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
        Team g = new Team(uuid, name, description);
        Team g2 = new Team(uuid, name, description);

        assertEquals(g, g2);

        g2.setUuid(null);

        assertNotEquals(g, g2);
    }

    @Test
    void testHash() {
        HashMap<Team, Boolean> map = new HashMap<>();
        final UUID uuid = UUID.randomUUID();
        final String name = "name";
        final String description = "description";
        Team g = new Team(uuid, name, description);

        map.put(g, true);

        assertTrue(map.get(g));
    }

    @Test
    void testToString() {
        final UUID uuid = UUID.randomUUID();
        final String name = "name------name";
        final String description = "description------description";
        Team g = new Team(uuid, name, description);

        assertTrue(g.toString().contains(name));
        assertTrue(g.toString().contains(uuid.toString()));
        assertTrue(g.toString().contains(description));
    }
}
