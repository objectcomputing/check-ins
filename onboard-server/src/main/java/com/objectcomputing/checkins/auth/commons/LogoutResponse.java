package com.objectcomputing.checkins.auth.commons;

import lombok.Data;

@Data
public class LogoutResponse {
    private Boolean success;

    public LogoutResponse(Boolean success) {
        this.success = success;
    }

    public static LogoutResponse success() {
        return new LogoutResponse(true);
    }

    public static LogoutResponse failure() {
        return new LogoutResponse(false);
    }
}
