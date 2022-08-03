package com.objectcomputing.checkins.security.authentication.token.time;

public class TimeToLive {
    private long time;

    public TimeToLive(long time) {
        this.time = time;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
