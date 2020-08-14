package com.objectcomputing.checkins.services.memberskill;

import com.objectcomputing.checkins.services.memberSkill.MemberSkillBadArgException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MemberSkillBadArgExceptionTest {

    @Test
    void testExceptionMessage() {
        final String message = "Hello world";
        MemberSkillBadArgException argException = new MemberSkillBadArgException(message);
        assertEquals(argException.getMessage(), message);
    }

}
