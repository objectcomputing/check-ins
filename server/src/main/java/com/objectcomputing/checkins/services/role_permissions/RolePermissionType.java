package com.objectcomputing.checkins.services.role_permissions;

public enum RolePermissionType {
    READCHECKIN,
    CREATECHECKIN,
    DELETECHECKIN;

    @Override
    public String toString() {
        return name();
    }

    public static class Constants {
        public static final String READCHECKIN_PERMISSION = "READCHECKIN";
        public static final String CREATECHECKIN_PERMISSION = "CREATECHECKIN";
        public static final String DELETECHECKIN_PERMISSION = "DELETECHECKIN";
    }
}