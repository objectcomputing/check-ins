package com.objectcomputing.checkins.services.memberprofile.retentionreport;

public enum FrequencyType {
    MONTHLY,
    WEEKLY,
    DAILY;

    @Override
    public String toString() { return name(); }

    public static class Constants {
        public static final String MONTHLY_FREQUENCY = "MONTHLY";
        public static final String WEEKLY_FREQUENCY = "WEEKLY";
        public static final String DAILY_FREQUENCY = "DAILY";
    }
}
