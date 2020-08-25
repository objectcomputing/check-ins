// package com.objectcomputing.checkins.services.skills;

// import io.micronaut.http.HttpStatus;
// import io.micronaut.http.client.HttpClient;
// import io.micronaut.http.client.annotation.Client;
// import io.micronaut.test.annotation.MicronautTest;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;

// import javax.inject.Inject;
// import java.util.*;

// import static org.junit.Assert.assertNotNull;
// import static org.junit.jupiter.api.Assertions.assertEquals;
// import static org.junit.jupiter.api.Assertions.assertThrows;
// import static org.mockito.Mockito.*;

// @MicronautTest
// public class SkillServicesTest {

//     @Inject
//     @Client("/skill")
//     private HttpClient client;

//     @Inject
//     SkillServices itemUnderTest;

//     SkillRepository mockSkillRepository = mock(SkillRepository.class);
//     Skill mockSkill = mock(Skill.class);

//     private static String fakeSkillName = "testSkillName";
//     private static String fakeSkillName2 = "testSkillName2";
//     private static boolean fakePending = true;

//     String fakeUuid = "12345678-9123-4567-abcd-123456789abc";
//     String fakeUuid2 = "22345678-9123-4567-abcd-123456789abc";

//     private static final Map<String, Object> fakeBody = new HashMap<String, Object>() {{
//         put("name", fakeSkillName);
//         put("pending", true);
//     }};

//     @BeforeEach
//     void setup() {
//         itemUnderTest.setSkillRepository(mockSkillRepository);
//         reset(mockSkillRepository);
//         reset(mockSkill);
//     }

//     // Saves skill to db given id
//     @Test
//     public void testSaveSkill() {

//         String testSkillName = "testSkill";
//         UUID uuid = UUID.fromString(fakeUuid);
//         Skill skill = new Skill();
//         skill.setSkillid(uuid);
//         skill.setName(testSkillName);
//         skill.setPending(fakePending);

//         when(mockSkillRepository.save(skill)).thenReturn(skill);
//         Skill returned = itemUnderTest.saveSkill(skill);

//         assertEquals(skill.getSkillid(), returned.getSkillid());

//     }

//     // Saves skill to db given id
//     @Test
//     public void testSaveSkill_skill_not_found() {

//         String testSkillName = "testSkill";
//         UUID uuid = UUID.fromString(fakeUuid);
//         Skill skill = new Skill();
//         skill.setSkillid(uuid);
//         skill.setName(testSkillName);
//         skill.setPending(fakePending);
//         Skill skillNotThere = new Skill();
//         skillNotThere.setSkillid(UUID.fromString(fakeUuid2));
//         skillNotThere.setName(testSkillName+"notThere");
//         skillNotThere.setPending(fakePending);

//         when(mockSkillRepository.save(skill)).thenReturn(skill);
//         Skill returned = itemUnderTest.saveSkill(skillNotThere);

//         assertEquals(null, returned);

//     }

//     // Reads skill from db given id
//     @Test
//     public void testReadSkill() {

//         String testSkillName = "testSkill";
//         UUID uuid = UUID.fromString(fakeUuid);
//         Skill skill = new Skill();
//         skill.setSkillid(uuid);
//         skill.setName(testSkillName);
//         skill.setPending(fakePending);

//         when(mockSkillRepository.findBySkillid(uuid)).thenReturn(skill);
//         Skill returned = itemUnderTest.readSkill(uuid);

//         assertEquals(skill.getSkillid(), returned.getSkillid());
//         assertEquals(skill.getName(), returned.getName());
//         assertEquals(skill.isPending(), returned.isPending());

//     }

//     // Tries to read nonexistant skill from db given id
//     @Test
//     public void testReadSkill_skill_not_found() {

//         String testSkillName = "testSkill";
//         Skill skill = new Skill();
//         skill.setSkillid(UUID.fromString(fakeUuid));
//         skill.setName(testSkillName);
//         skill.setPending(fakePending);

//         when(mockSkillRepository.findBySkillid(UUID.fromString(fakeUuid))).thenReturn(skill);
//         Skill returned = itemUnderTest.readSkill(UUID.fromString(fakeUuid2));

//         assertEquals(null, returned);

//     }

//     // Reads skill from db given name or pending status
//     @Test
//     public void testFindByValue_using_name_and_pending() {

//         String testSkillName = "testSkill";
// //        UUID uuid = UUID.fromString(fakeUuid);
//         Skill skill = new Skill();
//         skill.setSkillid(UUID.fromString(fakeUuid));
//         skill.setName(testSkillName);
//         skill.setPending(fakePending);
//         List<Skill> result = new ArrayList<Skill>();
//         result.add(skill);

//         when(mockSkillRepository.findByNameIlike("%" + testSkillName + "%" )).thenReturn(result);
//         List<Skill> returned = itemUnderTest.findByValue(testSkillName, fakePending);

//         assertEquals(skill.getSkillid(), returned.get(0).getSkillid());
//         assertEquals(skill.getName(), returned.get(0).getName());
//         assertEquals(skill.isPending(), returned.get(0).isPending());

//     }

//     // Tries to read nonexistant skill from db given id
//     @Test
//     public void testFindByValue_skill_not_found_using_name_and_pending() {

//         String testSkillName = "testSkill";
//         Skill skill = new Skill();
//         skill.setSkillid(UUID.fromString(fakeUuid));
//         skill.setName(testSkillName);
//         skill.setPending(fakePending);

//         List<Skill> result = new ArrayList<Skill>();
//         result.add(skill);

//         when(mockSkillRepository.findByNameIlike("%" + testSkillName + "%" )).thenReturn(result);
//         List<Skill> returned = itemUnderTest.findByValue(testSkillName+"notThere", fakePending);

//         // findByNameLike is returning a linked list of size 0 if not found
//         assertEquals(0, returned.size());

//     }

//     // Reads skill from db given name or pending status
//     @Test
//     public void testFindByValue_using_name_only() {

//         String testSkillName = "testSkill";
//         Skill skill = new Skill();
//         skill.setSkillid(UUID.fromString(fakeUuid));
//         skill.setName(testSkillName);
//         List<Skill> result = new ArrayList<Skill>();
//         result.add(skill);

//         when(mockSkillRepository.findByNameIlike("%" + testSkillName + "%" )).thenReturn(result);
//         List<Skill> returned = itemUnderTest.findByValue(testSkillName, null);

//         assertEquals(skill.getSkillid(), returned.get(0).getSkillid());
//         assertEquals(skill.getName(), returned.get(0).getName());
//         assertEquals(skill.isPending(), returned.get(0).isPending());

//     }

//     // Tries to read nonexistant skill from db given id
//     @Test
//     public void testFindByValue_skill_not_found_using_name_only() {

//         String testSkillName = "testSkill";
//         Skill skill = new Skill();
//         skill.setSkillid(UUID.fromString(fakeUuid));
//         skill.setName(testSkillName);
//         skill.setPending(fakePending);

//         List<Skill> result = new ArrayList<Skill>();
//         result.add(skill);

//         when(mockSkillRepository.findByNameIlike("%" + testSkillName + "%" )).thenReturn(result);
//         List<Skill> returned = itemUnderTest.findByValue(testSkillName+"notThere", null);

//         // findByNameLike is returning a linked list of size 0 if not found
//         assertEquals(0, returned.size());

//     }

//     @Test
//     public void testfindByNameLike() {

//         String fakeSkillName = "fakeSkill";
//         Skill skill = new Skill();
//         skill.setSkillid(UUID.fromString(fakeUuid));
//         skill.setName(fakeSkillName);
//         skill.setPending(fakePending);
//         List<Skill> result = new ArrayList<Skill>();
//         result.add(skill);

//         when(mockSkillRepository.findByNameIlike("%" + fakeSkillName + "%" )).thenReturn(result);
//         List<Skill> returned = itemUnderTest.findByNameLike(fakeSkillName);

//         assertEquals(skill.getSkillid(), returned.get(0).getSkillid());
//         assertEquals(skill.getName(), returned.get(0).getName());
//         assertEquals(skill.isPending(), returned.get(0).isPending());

//     }

//     @Test
//     public void testfindByNameLike_unfound() {

//         String fakeSkillName = "fakeSkill";
//         Skill skill = new Skill();
//         skill.setSkillid(UUID.fromString(fakeUuid));
//         skill.setName(fakeSkillName);
//         skill.setPending(fakePending);
//         Skill skillNotFound = new Skill();
//         skillNotFound.setSkillid(UUID.fromString(fakeUuid2));
//         skillNotFound.setName(fakeSkillName+"notFound");
//         skillNotFound.setPending(fakePending);
//         List<Skill> result = new ArrayList<Skill>();
//         result.add(skillNotFound);

//         when(mockSkillRepository.findByNameIlike("%" + fakeSkillName + "%" )).thenReturn(result);
//         List<Skill> returned = itemUnderTest.findByNameLike(skillNotFound.getName());

//         assertEquals(0, returned.size());

//     }

//     @Test
//     public void testfindByPending() {

//         String fakeSkillName = "fakeSkill";
//         Skill skill = new Skill();
//         skill.setSkillid(UUID.fromString(fakeUuid));
//         skill.setName(fakeSkillName);
//         skill.setPending(fakePending);
//         List<Skill> result = new ArrayList<Skill>();
//         result.add(skill);

//         when(mockSkillRepository.findByPending(fakePending)).thenReturn(result);
//         List<Skill> returned = itemUnderTest.findByPending(fakePending);

//         assertEquals(skill.getSkillid(), returned.get(0).getSkillid());
//         assertEquals(skill.getName(), returned.get(0).getName());
//         assertEquals(skill.isPending(), returned.get(0).isPending());

//     }

//     @Test
//     public void testUpdate() {

//         String fakeSkillName = "fakeSkill";
//         Skill skill = new Skill();
//         skill.setSkillid(UUID.fromString(fakeUuid));
//         skill.setName(fakeSkillName);
//         skill.setPending(fakePending);

//         when(mockSkillRepository.update(skill)).thenReturn(skill);
//         when(itemUnderTest.readSkill(skill.getSkillid())).thenReturn(skill);
//         Skill returned = itemUnderTest.update(skill);

//         assertEquals(skill.getSkillid(), returned.getSkillid());
//         assertEquals(skill.getName(), returned.getName());
//         assertEquals(skill.isPending(), returned.isPending());

//     }

// }