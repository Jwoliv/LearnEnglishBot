package com.example.LearnEnglishBot.util;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

public class KeyboardBuilder {
    public static ReplyKeyboardMarkup createAccountKeyboard() {
        ReplyKeyboardMarkup keyboard = new ReplyKeyboardMarkup();
        keyboard.setResizeKeyboard(true);
        keyboard.setOneTimeKeyboard(true);
        List<KeyboardRow> rows = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();
        row.add(new KeyboardButton("Login"));
        row.add(new KeyboardButton("Sing in"));
        rows.add(row);
        keyboard.setKeyboard(rows);
        return keyboard;
    }
    public static ReplyKeyboardMarkup createFunctionalKeyboard() {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setOneTimeKeyboard(true);
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setSelective(true);

        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow row1 = new KeyboardRow();
        row1.add("New word");
        row1.add("New list");
        keyboard.add(row1);
        KeyboardRow row2 = new KeyboardRow();
        row2.add("Delete list");
        row2.add("Delete word");
        keyboard.add(row2);
        KeyboardRow row3 = new KeyboardRow();
        row3.add("Learned word");
        keyboard.add(row3);
        keyboardMarkup.setKeyboard(keyboard);

        return keyboardMarkup;
    }

}
