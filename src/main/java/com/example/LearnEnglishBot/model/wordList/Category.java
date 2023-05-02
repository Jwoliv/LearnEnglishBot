package com.example.LearnEnglishBot.model.wordList;

public enum Category {
    CATEGORY_ONE("Category one"),
    CATEGORY_TWO("Category two"),
    CATEGORY_THREE("Category three");

    private final String displayName;

    Category(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
