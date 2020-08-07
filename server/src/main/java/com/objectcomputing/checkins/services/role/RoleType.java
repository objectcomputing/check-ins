package com.objectcomputing.checkins.services.role;

public enum RoleType {
    ADMIN,
    PDL,
    MEMBER;

    @Override
    public String toString() {
        return name();
    }

    public static class Constants {
        static final String ADMIN_ROLE_STR = "ADMIN";
        static final String PDL_ROLE_STR = "PDL";
        static final String MEMBER_ROLE_STR = "MEMBER";
    }
}


