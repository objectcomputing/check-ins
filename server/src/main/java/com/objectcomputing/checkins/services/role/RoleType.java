package com.objectcomputing.checkins.services.role;

public enum RoleType {
    ADMIN,
    PDL,
    MEMBER;

    public String getRole() {
        return name();
    }

    @Override
    public String toString() {
        return getRole();
    }

    public static class Constants {
        static final String ADMIN_ROLE_STR = "ADMIN";
        static final String PDL_ROLE_STR = "PDL";
        static final String MEMBER_ROLE_STR = "MEMBER";
    }
}


