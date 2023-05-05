package com.example.LearnEnglishBot.util;

import com.example.LearnEnglishBot.model.user.User;
import com.example.LearnEnglishBot.model.wordList.WordList;
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
        var keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setOneTimeKeyboard(true);
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setSelective(true);

        List<KeyboardRow> keyboard = new ArrayList<>();
        var row1 = new KeyboardRow();
        row1.add("🆕 New word");
        row1.add("🆕 New list");
        row1.add("📚 All lists");
        keyboard.add(row1);
        var row2 = new KeyboardRow();
        row2.add("🗑️ Delete list");
        row2.add("🗑️ Delete word");
        row2.add("❌ Delete all lists");
        keyboard.add(row2);
        var row3 = new KeyboardRow();
        row3.add("💪 Top 20");
        row3.add("📝 Take test");
        row3.add("👤 Profile");
        keyboard.add(row3);
        var row4 = new KeyboardRow();
        row4.add("📊 All tests");
        row4.add("👀 Find lists");
        row4.add("🗑️ Delete profile");
        keyboard.add(row4);
        keyboardMarkup.setKeyboard(keyboard);

        return keyboardMarkup;
    }

    public static <E extends Enum<E>> ReplyKeyboardMarkup createKeyboardOfEnum(Class<E> enumClass) {
        var keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setOneTimeKeyboard(true);
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setSelective(true);

        var enumGroups = Arrays.stream(enumClass.getEnumConstants())
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

    public static ReplyKeyboardMarkup createKeyboardOfWordListOfUser(List<WordList> wordLists) {

        var keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setOneTimeKeyboard(true);
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setSelective(true);

        List<KeyboardRow> keyboardRows = new ArrayList<>();

        for (WordList wordList : wordLists) {
            KeyboardButton button = new KeyboardButton(wordList.getTitle());
            KeyboardRow row = new KeyboardRow();
            row.add(button);
            keyboardRows.add(row);
        }

        keyboardMarkup.setKeyboard(keyboardRows);

        return keyboardMarkup;
    }

    public static ReplyKeyboardMarkup createKeyboardOfWordsOfUser(User user, String title) {
        var wordLists = user.getWordLists();
        var selectedWordList = new WordList();
        for (var wl: wordLists) {
            if (wl.getTitle().equals(title)) {
                selectedWordList = wl;
                break;
            }
        }

        var keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setOneTimeKeyboard(true);
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setSelective(true);

        List<KeyboardRow> keyboardRows = new ArrayList<>();

        for (var word : selectedWordList.getWords()) {
            KeyboardButton button = new KeyboardButton(word.getSourceWord() + " - " + word.getTranslateWord());
            KeyboardRow row = new KeyboardRow();
            row.add(button);
            keyboardRows.add(row);
        }

        keyboardMarkup.setKeyboard(keyboardRows);

        return keyboardMarkup;
    }

    public static ReplyKeyboardMarkup createKeyboardOfTests(User user) {
        var tests = user.getTests();


        var keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setOneTimeKeyboard(true);
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setSelective(true);

        List<KeyboardRow> keyboardRows = new ArrayList<>();

        for (var test : tests) {
            KeyboardButton button = new KeyboardButton(
                    test.getId() + ") " +
                    FormatTime.formattedTime(test.getStartTime()) + " - " +
                    FormatTime.formattedTime(test.getFinishTime()));

            KeyboardRow row = new KeyboardRow();
            row.add(button);
            keyboardRows.add(row);
        }

        keyboardMarkup.setKeyboard(keyboardRows);

        return keyboardMarkup;
    }
}
