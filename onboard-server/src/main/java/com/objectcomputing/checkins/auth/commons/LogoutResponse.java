package com.objectcomputing.checkins.auth.commons;

public class LogoutResponse {
    private final Boolean success;

    public LogoutResponse(Boolean success) {
        this.success = success;
    }

    public static LogoutResponse success() {
        return new LogoutResponse(true);
    }

    public static LogoutResponse failure() {
        return new LogoutResponse(false);
    }

    public Boolean getSuccess() {
        return success;
    }
}
