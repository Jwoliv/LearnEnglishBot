package com.example.LearnEnglishBot.handlers;

import com.example.LearnEnglishBot.model.wordList.*;
import com.example.LearnEnglishBot.service.UserService;
import com.example.LearnEnglishBot.service.WordListService;
import com.example.LearnEnglishBot.util.KeyboardBuilder;
import com.example.LearnEnglishBot.util.MessageSender;
import lombok.Getter;
import lombok.Setter;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Getter
@Setter
public class WordListHandler {
    private String title;
    private Category category;
    private EnglishLevel engLvl;
    private AccessLevel accessLevel;

    private ConditionWordList cndWordList;
    private final UserService userService;
    private final WordListService wordListService;
    private MessageSender msgSender;

    public WordListHandler(UserService userService, WordListService wordListService) {
        this.userService = userService;
        this.wordListService = wordListService;
    }

    @Autowired
    @Lazy
    public void setMsgSender(MessageSender msgSender) {
        this.msgSender = msgSender;
    }

    public void activeWithList(Long chatId, String text) {
        if (text.equals("🆕 New list")) {
            handleNameOfList(chatId);
        }
        else if (getCndWordList().equals(ConditionWordList.WAIT_FOR_NAME)) {
            handleCategoryOfList(chatId, text);
        }
        else if (getCndWordList().equals(ConditionWordList.WAIT_FOR_CATEGORY)) {
            handlerEnglishLevel(chatId, Category.valueOf(text));
        }
        else if (getCndWordList().equals(ConditionWordList.WAIT_FOR_ENGLISH_LEVEL)) {
            handlerAccessLevel(chatId, EnglishLevel.valueOf(text));
        }
        else if (getCndWordList().equals(ConditionWordList.WAIT_FOR_ACCESS_LEVEL)) {
            finallyCreatedListOfWords(chatId, AccessLevel.valueOf(text));
        }
        else if (getCndWordList().equals(ConditionWordList.DELETE_LIST) && userService.findByChatId(chatId).getWordLists().stream().map(WordList::getTitle).toList().contains(text)) {
            deleteList(chatId, text);
        }
        else if (getCndWordList().equals(ConditionWordList.DELETE_ALL)) {
            deleteAllListByUser(chatId, text);
        }
        else if (getCndWordList().equals(ConditionWordList.SELECT_ALL)) {
            selectedWordsOfList(chatId, text);
        }
    }

    public void handlerGetAllListsByUser(Long chatId) {
        if (userService.findByChatId(chatId).getWordLists().size() != 0) {
            cndWordList = ConditionWordList.SELECT_ALL;
            msgSender.sendMessage(chatId, "📚 Your lists of words", KeyboardBuilder.createKeyboardOfWordListOfUser(userService.findByChatId(chatId)));
        }
        else {
            msgSender.sendMessage(chatId, "📚 Your collection of lists is empty", KeyboardBuilder.createFunctionalKeyboard());
        }
    }

    public void activeWithDeleteList(Long chatId, String text) {
        if (text.equals("🗑️ Delete list")) {
            handlerDeleteSelectedList(chatId);
        }
        else if (text.equals("❌ Delete all lists")) {
            handlerDeleteAllList(chatId);
        }
    }



    private void selectedWordsOfList(Long chatId, String text) {
        var user = userService.findByChatId(chatId);
        var list = wordListService.findByTitleAndUser(text, user);
        if (list != null) {
            var sb = new StringBuilder();
            var size = list.getWords().size();
            sb.append(String.format("""
                    📜 List: %s
                    📈 Number of words: %d
                    🔑 Access level: %s
                    🔤 English level: %s
                    🗂️ Category: %s\n\n
                    """, list.getTitle(), size, list.getAccessLevel().getDisplayName(), list.getEnglishLevel(), list.getCategory().getDisplayName()
            ));

            if (size > 0) {
                sb.append(" 📊 Words of the list:\n");
                for (var word : list.getWords()) {
                    sb.append(word.getSourceWord()).append(" - ").append(word.getTranslateWord()).append("\n");
                }
            }
            else {
                sb.append("🔍 This list is empty");
            }
            msgSender.sendMessage(chatId, sb.toString(), KeyboardBuilder.createFunctionalKeyboard());
            cndWordList = null;
        }
        else {
            msgSender.sendMessage(chatId, "Please enter a correct title of the list", KeyboardBuilder.createKeyboardOfWordListOfUser(user));
        }
    }

    private void handlerDeleteSelectedList(Long chatId) {
        if (userService.findByChatId(chatId).getWordLists().size() != 0) {
            setCndWordList(ConditionWordList.DELETE_LIST);
            msgSender.sendMessage(chatId, "📚 Your lists of words", KeyboardBuilder.createKeyboardOfWordListOfUser(userService.findByChatId(chatId)));
        }
        else {
            msgSender.sendMessage(chatId, "📚 Your collection of lists is empty", KeyboardBuilder.createFunctionalKeyboard());
        }
    }

    private void handlerDeleteAllList(Long chatId) {
        if (userService.findByChatId(chatId).getWordLists().size() != 0) {
            setCndWordList(ConditionWordList.DELETE_ALL);
            msgSender.sendMessage(chatId, "❗ Right now your deleted all your lists\n🔒 Please enter new password");
        }
        else {
            msgSender.sendMessage(chatId, "📚 Your collection of lists is empty", KeyboardBuilder.createFunctionalKeyboard());
        }
    }

    private void deleteAllListByUser(Long chatId, String text) {
        var user = userService.findByChatId(chatId);
        if (BCrypt.checkpw(text, user.getPassword())) {
            wordListService.deleteAllByUser(user);
            msgSender.sendMessage(chatId, "🗑️ The all list deleted successfully", KeyboardBuilder.createFunctionalKeyboard());
            cndWordList = ConditionWordList.NONE;
        } else {
            msgSender.sendMessage(chatId, "🚫 Wrong password\nPlease try again");
        }
    }

    private void deleteList(Long chatId, String text) {
        var user = userService.findByChatId(chatId);
        var list = wordListService.findByTitleAndUser(text, user);
        if (list != null) {
            wordListService.deleteById(list.getId());
            msgSender.sendMessage(chatId, "🗑️ The list has deleted correct", KeyboardBuilder.createFunctionalKeyboard());
            cndWordList = ConditionWordList.NONE;
        }
    }

    private void handleNameOfList(Long chatId) {
        cndWordList = ConditionWordList.WAIT_FOR_NAME;
        msgSender.sendMessage(chatId, "📝 Please enter the name of list");
    }

    private void handleCategoryOfList(Long chatId, String text) {
        title = text;
        var listWithSoNameIsExisted = false;
        var user = userService.findByChatId(chatId);
        List<WordList> wordLists = user.getWordLists();
        if (wordLists.size() != 0) {
            for (WordList wordList : wordLists) {
                if (wordList.getTitle().equals(title)) {
                    listWithSoNameIsExisted = true;
                    break;
                }
            }
        }
        if (!listWithSoNameIsExisted) {
            cndWordList = ConditionWordList.WAIT_FOR_CATEGORY;
            msgSender.sendMessage(chatId, "📂 Please selected a category", KeyboardBuilder.createKeyboardOfEnum(Category.class));
        }
        else {
            String message = String.format("❌ You already have a list with the name: `%s`", title);
            msgSender.sendMessage(chatId, message);
        }
    }


    private void handlerEnglishLevel(Long chatId, Category text) {
        category = text;
        cndWordList = ConditionWordList.WAIT_FOR_ENGLISH_LEVEL;
        msgSender.sendMessage(chatId, "🌐 Please selected an english level", KeyboardBuilder.createKeyboardOfEnum(EnglishLevel.class));
    }

    private void handlerAccessLevel(Long chatId, EnglishLevel text) {
        engLvl = text;
        cndWordList = ConditionWordList.WAIT_FOR_ACCESS_LEVEL;
        msgSender.sendMessage(chatId, "🔐 Select an access level", KeyboardBuilder.createKeyboardOfEnum(AccessLevel.class));
    }

    private void finallyCreatedListOfWords(Long chatId, AccessLevel text) {
        accessLevel = text;
        cndWordList = ConditionWordList.NONE;
        saveListAndSetFields(chatId);
    }

    private void saveListAndSetFields(Long chatId) {
        var user = userService.findByChatId(chatId);
        var wordList = WordList.builder()
                .title(title)
                .category(category)
                .user(user)
                .accessLevel(accessLevel)
                .englishLevel(engLvl)
                .build();

        wordListService.save(wordList);
        msgSender.sendMessage(chatId, "✅ Saved the list successfully", KeyboardBuilder.createFunctionalKeyboard());
        cndWordList = null;
    }
}
