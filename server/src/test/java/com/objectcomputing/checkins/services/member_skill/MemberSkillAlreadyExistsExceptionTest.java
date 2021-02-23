package com.objectcomputing.checkins.services.member_skill;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MemberSkillAlreadyExistsExceptionTest {

    @Test
    void testExceptionMessage() {
        final String message = "Hello world";
        MemberSkillAlreadyExistsException alreadyExistException = new MemberSkillAlreadyExistsException(message);
        assertEquals(alreadyExistException.getMessage(), message);
    }

}
