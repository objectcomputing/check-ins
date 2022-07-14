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
        public static final String ADMIN_ROLE = "ADMIN";
        public static final String PDL_ROLE = "PDL";
        public static final String MEMBER_ROLE = "MEMBER";
    }
}


