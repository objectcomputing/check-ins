package com.objectcomputing.checkins.services.member_skill;

public class MemberSkillAlreadyExistsException extends RuntimeException {
    public MemberSkillAlreadyExistsException(String message) {
        super(message);
    }
}
