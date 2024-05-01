package com.objectcomputing.checkins.services.guild;

import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.validation.validator.Validator;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import javax.validation.ConstraintViolation;
import java.util.HashMap;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@MicronautTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class GuildTest {

    @Inject
    private Validator validator;


    @Test
    void testGuildInstantiation() {
        final String name = "name";
        final String description = "description";
        Guild guild = new Guild(name, description, null, false);
        assertEquals(guild.getName(), name);
        assertEquals(guild.getDescription(), description);
    }

    @Test
    void testGuildInstantiation2() {
        final UUID id = UUID.randomUUID();
        final String name = "name";
        final String description = "description";
        final String link = "https://www.compass.objectcomputing.com/guilds/name";
        Guild guild = new Guild(id, name, description, link, false);
        assertEquals(guild.getId(), id);
        assertEquals(guild.getName(), name);
        assertEquals(guild.getDescription(), description);

        Set<ConstraintViolation<Guild>> violations = validator.validate(guild);
        assertTrue(violations.isEmpty());
    }


    @Test
    void testConstraintViolation() {
        final UUID id = UUID.randomUUID();
        final String name = "name";
        final String description = "description";
        Guild guild = new Guild(id, name, description, null, false);

        guild.setName("");
        guild.setDescription("");

        Set<ConstraintViolation<Guild>> violations = validator.validate(guild);
        assertEquals(violations.size(), 2);
        for (ConstraintViolation<Guild> violation : violations) {
            assertEquals(violation.getMessage(), "must not be blank");
        }
    }

    @Test
    void testEquals() {
        final UUID id = UUID.randomUUID();
        final String name = "name";
        final String description = "description";
        final String link = "https://www.compass.objectcomputing.com/guilds/name";
        Guild g = new Guild(id, name, description, link, false);
        Guild g2 = new Guild(id, name, description, link, false);

        assertEquals(g, g2);

        g2.setId(null);

        assertNotEquals(g, g2);
    }

    @Test
    void testHash() {
        HashMap<Guild, Boolean> map = new HashMap<>();
        final UUID id = UUID.randomUUID();
        final String name = "name";
        final String description = "description";
        final String link = "https://www.compass.objectcomputing.com/guilds/name";
        Guild g = new Guild(id, name, description, link, false);

        map.put(g, true);

        assertTrue(map.get(g));
    }

    @Test
    void testToString() {
        final UUID id = UUID.randomUUID();
        final String name = "name------name";
        final String description = "description------description";
        final String link = "https://www.compass.objectcomputing.com/guilds/name";
        final String isCommunity = "false";
        Guild g = new Guild(id, name, description,link, false);

        String s = g.toString();
        assertTrue(s.contains(name));
        assertTrue(s.contains(id.toString()));
        assertTrue(s.contains(description));
        assertTrue(s.contains(isCommunity));
    }
}
