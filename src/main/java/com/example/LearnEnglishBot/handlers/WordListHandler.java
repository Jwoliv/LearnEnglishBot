package com.example.LearnEnglishBot.handlers;

import com.example.LearnEnglishBot.model.user.User;
import com.example.LearnEnglishBot.model.word.wordList.*;
import com.example.LearnEnglishBot.service.UserService;
import com.example.LearnEnglishBot.service.WordListService;
import com.example.LearnEnglishBot.util.KeyboardBuilder;
import com.example.LearnEnglishBot.util.MessageSender;
import lombok.Getter;
import lombok.Setter;
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

    public void handleNameOfList(Long chatId) {
        cndWordList = ConditionWordList.WAIT_FOR_NAME;
        msgSender.sendMessage(chatId, "📝 Please enter the name of list");
    }

    public void handleCategoryOfList(Long chatId, String text) {
        title = text;
        boolean listWithSoNameIsExisted = false;
        User user = userService.findByChatId(chatId);
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

    public void handlerEnglishLevel(Long chatId, Category text) {
        category = text;
        cndWordList = ConditionWordList.WAIT_FOR_ENGLISH_LEVEL;
        msgSender.sendMessage(chatId, "🌐 Please selected an english level", KeyboardBuilder.createKeyboardOfEnum(EnglishLevel.class));
    }

    public void handlerAccessLevel(Long chatId, EnglishLevel text) {
        engLvl = text;
        cndWordList = ConditionWordList.WAIT_FOR_ACCESS_LEVEL;
        msgSender.sendMessage(chatId, "🔐 Select an access level", KeyboardBuilder.createKeyboardOfEnum(AccessLevel.class));
    }
    public void finallyCreatedListOfWords(Long chatId, AccessLevel text) {
        accessLevel = text;
        cndWordList = ConditionWordList.NONE;
        saveListAndSetFields(chatId);
    }


    private void saveListAndSetFields(Long chatId) {
        User user = userService.findByChatId(chatId);
        WordList wordList = WordList.builder()
                .title(title)
                .category(category)
                .user(user)
                .accessLevel(accessLevel)
                .englishLevel(engLvl)
                .build();

        wordListService.save(wordList);
        msgSender.sendMessage(chatId, "✅ Saved the list successfully", KeyboardBuilder.createFunctionalKeyboard());
    }
}
