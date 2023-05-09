package com.example.LearnEnglishBot.model.notification;

public enum NotificationFrequency {
    DAILY("Daily"),
    WEEKLY("Weekly"),
    MONTHLY("Monthly"),
    QUARTERLY("Quarterly"),
    SEMI_ANNUALLY("Semi-annually"),
    YEARLY("Yearly");

    private final String displayName;

    NotificationFrequency(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
