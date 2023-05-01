package com.example.LearnEnglishBot.util;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
        row1.add("🆕 New word");
        row1.add("🆕 New list");
        row1.add("📚 All lists");
        keyboard.add(row1);
        KeyboardRow row2 = new KeyboardRow();
        row2.add("🗑️ Delete list");
        row2.add("🗑️ Delete word");
        row2.add("❌ Delete all lists");
        keyboard.add(row2);
        KeyboardRow row3 = new KeyboardRow();
        row3.add("✅ Learned word");
        row3.add("📝 Take test");
        row3.add("👤 Profile");
        keyboard.add(row3);
        keyboardMarkup.setKeyboard(keyboard);

        return keyboardMarkup;
    }
    public static <E extends Enum<E>> ReplyKeyboardMarkup createKeyboardOfEnum(Class<E> enumClass) {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setOneTimeKeyboard(true);
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setSelective(true);

        List<List<E>> enumGroups = Arrays.stream(enumClass.getEnumConstants())
                .collect(Collectors.partitioningBy(i -> (i.ordinal() % 2 == 0)))
                .values()
                .stream()
                .toList();

        List<KeyboardRow> keyboard = enumGroups.stream()
                .map(enumGroup -> {
                    KeyboardRow row = new KeyboardRow();
                    enumGroup.forEach(enumValue -> row.add(new KeyboardButton(enumValue.toString())));
                    return row;
                })
                .toList();

        keyboardMarkup.setKeyboard(keyboard);

        return keyboardMarkup;
    }

}
