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
    public final static List<String> newTitles = new ArrayList<>(List.of("🆕 New word", "🆕 New list", "🆕 New notification", "📝 Take test"));
    public final static List<String> deleteTitles = new ArrayList<>(List.of("🗑️ Delete list", "🗑️ Delete word", "❌ Delete all lists","🗑️ Delete notification", "🗑️ Delete profile"));
    public final static List<String> socialTitles = new ArrayList<>(List.of("💪 Top 20", "👀 Find lists"));
    public final static List<String> accountTitles = new ArrayList<>(List.of("🔔 Notifications", "📚 Lists", "📊 Tests", "👤 Profile"));
    public final static List<String> authTitles = new ArrayList<>(List.of("Login", "Sing in"));

    public static ReplyKeyboardMarkup createFunctionalKeyboard() {
        var keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setOneTimeKeyboard(true);
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setSelective(true);

        List<KeyboardRow> keyboard = new ArrayList<>();
        var row1 = new KeyboardRow();
        row1.add("🆕 New");
        row1.add("🗑️ Delete");

        keyboard.add(row1);
        var row2 = new KeyboardRow();
        row2.add("📚 Social");
        row2.add("👤 Account");

        keyboard.add(row2);

        keyboardMarkup.setKeyboard(keyboard);

        return keyboardMarkup;
    }

    public static ReplyKeyboardMarkup createKeyboardOfList(List<String> strings) {
        var keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setOneTimeKeyboard(true);
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setSelective(true);

        List<KeyboardRow> keyboard = new ArrayList<>();

        int midpoint = (int) Math.ceil(strings.size() / 2.0);
        List<String> leftColumn = strings.subList(0, midpoint);
        List<String> rightColumn = strings.subList(midpoint, strings.size());

        KeyboardRow leftRow = new KeyboardRow();
        KeyboardRow rightRow = new KeyboardRow();

        for (String str : leftColumn) {
            leftRow.add(new KeyboardButton(str));
        }

        for (String str : rightColumn) {
            rightRow.add(new KeyboardButton(str));
        }

        // Додати рядки до клавіатури
        keyboard.add(leftRow);
        keyboard.add(rightRow);

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
