// package com.objectcomputing.checkins.services.guild;

// import io.micronaut.test.annotation.MicronautTest;
// import io.micronaut.validation.validator.Validator;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.TestInstance;

// import javax.inject.Inject;
// import javax.validation.ConstraintViolation;
// import java.util.HashMap;
// import java.util.Set;
// import java.util.UUID;

// import static org.junit.jupiter.api.Assertions.*;

// @MicronautTest
// @TestInstance(TestInstance.Lifecycle.PER_CLASS)
// class GuildTest {

//     @Inject
//     private Validator validator;


//     @Test
//     void testGuildInstantiation() {
//         final String name = "name";
//         final String description = "description";
//         Guild guild = new Guild(name, description);
//         assertEquals(guild.getName(), name);
//         assertEquals(guild.getDescription(), description);
//     }

//     @Test
//     void testGuildInstantiation2() {
//         final UUID guildid = UUID.randomUUID();
//         final String name = "name";
//         final String description = "description";
//         Guild guild = new Guild(guildid, name, description);
//         assertEquals(guild.getGuildid(), guildid);
//         assertEquals(guild.getName(), name);
//         assertEquals(guild.getDescription(), description);

//         Set<ConstraintViolation<Guild>> violations = validator.validate(guild);
//         assertTrue(violations.isEmpty());
//     }


//     @Test
//     void testConstraintViolation() {
//         final UUID guildid = UUID.randomUUID();
//         final String name = "name";
//         final String description = "description";
//         Guild guild = new Guild(guildid, name, description);

//         guild.setName("");
//         guild.setDescription("");

//         Set<ConstraintViolation<Guild>> violations = validator.validate(guild);
//         assertEquals(violations.size(), 2);
//         for (ConstraintViolation<Guild> violation : violations) {
//             assertEquals(violation.getMessage(), "must not be blank");
//         }
//     }

//     @Test
//     void testEquals() {
//         final UUID guildid = UUID.randomUUID();
//         final String name = "name";
//         final String description = "description";
//         Guild g = new Guild(guildid, name, description);
//         Guild g2 = new Guild(guildid, name, description);

//         assertEquals(g, g2);

//         g2.setGuildid(null);

//         assertNotEquals(g, g2);
//     }

//     @Test
//     void testHash() {
//         HashMap<Guild, Boolean> map = new HashMap<>();
//         final UUID guildid = UUID.randomUUID();
//         final String name = "name";
//         final String description = "description";
//         Guild g = new Guild(guildid, name, description);

//         map.put(g, true);

//         assertTrue(map.get(g));
//     }

//     @Test
//     void testToString() {
//         final UUID guildid = UUID.randomUUID();
//         final String name = "name------name";
//         final String description = "description------description";
//         Guild g = new Guild(guildid, name, description);

//         assertTrue(g.toString().contains(name));
//         assertTrue(g.toString().contains(guildid.toString()));
//         assertTrue(g.toString().contains(description));
//     }
// }
