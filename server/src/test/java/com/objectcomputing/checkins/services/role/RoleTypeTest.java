package com.objectcomputing.checkins.services.role;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class RoleTypeTest {

    @Test
    void testConstants() {
        RoleType.Constants dnc = new RoleType.Constants(); // Test coverage hack to get to 100%
        assertEquals(RoleType.Constants.PDL_ROLE, RoleType.PDL.name());
        assertEquals(RoleType.Constants.MEMBER_ROLE, RoleType.MEMBER.name());
        assertEquals(RoleType.Constants.ADMIN_ROLE, RoleType.ADMIN.name());
    }

    @Test
    void testValues() {
        RoleType[] roles = RoleType.values();
        assertEquals(roles.length, 3);
        for (RoleType roleType : roles) {
            switch (roleType) {
                case PDL:
                    assertEquals("PDL", roleType.toString());
                    break;
                case ADMIN:
                    assertEquals("ADMIN", roleType.toString());
                    break;
                case MEMBER:
                    assertEquals("MEMBER", roleType.toString());
                    break;
                default:
                    fail(String.format("%s not supported", roleType.toString()));
            }
        }
    }
}
