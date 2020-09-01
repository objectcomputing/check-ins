package com.objectcomputing.checkins.services.member_skill;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MemberSkillBadArgExceptionTest {

    @Test
    void testExceptionMessage() {
        final String message = "Hello world";
        MemberSkillBadArgException argException = new MemberSkillBadArgException(message);
        assertEquals(argException.getMessage(), message);
    }

}
