package com.objectcomputing.checkins.services.role;

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
class RoleTest {

    @Inject
    private Validator validator;


    @Test
    void testRoleInstantiation() {
        final RoleType roleType = RoleType.ADMIN;
        final UUID memberId = UUID.randomUUID();
        Role role = new Role(roleType, memberId);
        assertEquals(roleType, role.getRole());
        assertEquals(memberId, role.getMemberid());
    }

    @Test
    void testRoleInstantiation2() {
        final UUID id = UUID.randomUUID();
        final RoleType roleType = RoleType.ADMIN;
        final UUID memberId = UUID.randomUUID();
        Role role = new Role(id, roleType, memberId);
        assertEquals(id, role.getId());
        assertEquals(roleType, role.getRole());
        assertEquals(memberId, role.getMemberid());

        Set<ConstraintViolation<Role>> violations = validator.validate(role);
        assertTrue(violations.isEmpty());
    }


    @Test
    void testConstraintViolation() {
        final UUID id = UUID.randomUUID();
        final RoleType roleType = RoleType.ADMIN;
        final UUID memberId = UUID.randomUUID();
        Role role = new Role(id, roleType, memberId);

        role.setRole(null);
        role.setMemberid(null);

        Set<ConstraintViolation<Role>> violations = validator.validate(role);
        assertEquals(2, violations.size());
        for (ConstraintViolation<Role> violation : violations) {
            assertEquals(violation.getMessage(), "must not be null");
        }
    }

    @Test
    void testEquals() {
        final UUID id = UUID.randomUUID();
        final RoleType role = RoleType.MEMBER;
        final UUID memberId = UUID.randomUUID();
        Role r = new Role(id, role, memberId);
        Role r2 = new Role(id, role, memberId);

        assertEquals(r, r2);

        r2.setId(null);

        assertNotEquals(r, r2);

        r2.setId(r.getId());

        assertEquals(r, r2);

        r2.setRole(RoleType.PDL);

        assertNotEquals(r, r2);
    }

    @Test
    void testHash() {
        HashMap<Role, Boolean> map = new HashMap<>();
        final UUID id = UUID.randomUUID();
        final RoleType role = RoleType.MEMBER;
        final UUID memberId = UUID.randomUUID();
        Role r = new Role(id, role, memberId);

        map.put(r, true);

        assertTrue(map.get(r));
    }

    @Test
    void testToString() {
        final UUID id = UUID.randomUUID();
        final RoleType role = RoleType.ADMIN;
        final UUID memberId = UUID.randomUUID();
        Role r = new Role(id, role, memberId);

        String toString = r.toString();
        assertTrue(toString.contains(role.toString()));
        assertTrue(toString.contains(id.toString()));
        assertTrue(toString.contains(memberId.toString()));
    }
}
