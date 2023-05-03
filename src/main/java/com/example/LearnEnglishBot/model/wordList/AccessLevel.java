package com.example.LearnEnglishBot.model.wordList;

public enum AccessLevel {
    PRIVATE("🔒Private"),
    PUBLIC("📢 Public");

    private final String displayName;
    AccessLevel(String displayName) {
        this.displayName = displayName;
    }
    public String getDisplayName() {
        return displayName;
    }
}
