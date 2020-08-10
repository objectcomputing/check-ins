package com.objectcomputing.checkins.services.memberskills;

import com.objectcomputing.checkins.services.memberSkills.MemberSkillsBadArgException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MemberSkillsBadArgExceptionTest {

    @Test
    void testExceptionMessage() {
        final String message = "Hello world";
        MemberSkillsBadArgException argException = new MemberSkillsBadArgException(message);
        assertEquals(argException.getMessage(), message);
    }

}
