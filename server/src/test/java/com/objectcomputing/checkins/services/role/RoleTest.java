package com.objectcomputing.checkins.services.role;

import com.objectcomputing.checkins.services.TestContainersSuite;
import io.micronaut.validation.validator.Validator;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class RoleTest extends TestContainersSuite {

    @Inject
    private Validator validator;


    @Test
    void testRoleInstantiation() {
        final RoleType roleType = RoleType.ADMIN;
        Role role = new Role(roleType.name(), "role description");
        assertEquals(roleType.toString(), role.getRole(), role.getDescription());

    }

    @Test
    void testRoleInstantiation2() {
        final UUID id = UUID.randomUUID();
        final RoleType roleType = RoleType.ADMIN;
        Role role = new Role(id, roleType.name(),"role description");
        assertEquals(id, role.getId());
        assertEquals(roleType.toString(), role.getRole());

        Set<ConstraintViolation<Role>> violations = validator.validate(role);
        assertTrue(violations.isEmpty());
    }


    @Test
    void testConstraintViolation() {
        final UUID id = UUID.randomUUID();
        final RoleType roleType = RoleType.ADMIN;
        final UUID memberId = UUID.randomUUID();
        Role role = new Role(id, roleType.toString(), "role description");

        role.setRole(null);


        Set<ConstraintViolation<Role>> violations = validator.validate(role);
        assertEquals(1, violations.size());
        for (ConstraintViolation<Role> violation : violations) {
            assertEquals(violation.getMessage(), "must not be null");
        }
    }

    @Test
    void testEquals() {
        final UUID id = UUID.randomUUID();
        final RoleType role = RoleType.MEMBER;
        Role r = new Role(id, role.toString(),  "role description");
        Role r2 = new Role(id, role.toString(), "role description");

        assertEquals(r, r2);

        r2.setId(null);

        assertNotEquals(r, r2);

        r2.setId(r.getId());

        assertEquals(r, r2);

        r2.setRole(RoleType.PDL.toString());

        assertNotEquals(r, r2);
    }

    @Test
    void testHash() {
        HashMap<Role, Boolean> map = new HashMap<>();
        final UUID id = UUID.randomUUID();
        final RoleType role = RoleType.MEMBER;
        Role r = new Role(id, role.toString(), "role description");

        map.put(r, true);

        assertTrue(map.get(r));
    }

    @Test
    void testToString() {
        final UUID id = UUID.randomUUID();
        final RoleType role = RoleType.ADMIN;
        Role r = new Role(id, role.toString(),"role description");

        String toString = r.toString();
        assertTrue(toString.contains(role.toString()));
        assertTrue(toString.contains(id.toString()));
    }
}
