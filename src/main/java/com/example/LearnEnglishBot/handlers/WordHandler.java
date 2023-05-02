package com.example.LearnEnglishBot.handlers;

import com.example.LearnEnglishBot.model.word.ConditionWord;
import com.example.LearnEnglishBot.model.word.Word;
import com.example.LearnEnglishBot.service.UserService;
import com.example.LearnEnglishBot.service.WordListService;
import com.example.LearnEnglishBot.service.WordService;
import com.example.LearnEnglishBot.util.KeyboardBuilder;
import com.example.LearnEnglishBot.util.MessageSender;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
public class WordHandler {
    private ConditionWord cndWord;
    private MessageSender msgSender;
    private final WordService wordService;
    private final UserService userService;
    private final WordListService wordListService;

    private final Word word;

    public WordHandler(WordService wordService, UserService userService, WordListService wordListService, Word word) {
        this.wordService = wordService;
        this.userService = userService;
        this.wordListService = wordListService;
        this.word = word;
    }

    @Autowired
    @Lazy
    public void setMsgSender(MessageSender msgSender) {
        this.msgSender = msgSender;
    }


    public void activeWord(Long chatId, String text) {
        if (text.equals("🆕 New word")) {
            handleSourceWordOfList(chatId);
        }
        else if (cndWord.equals(ConditionWord.WAIT_FOR_SOURCE_WORD)) {
            handleTranslateWordOfList(chatId, text);
        }
        else if (cndWord.equals(ConditionWord.WAIT_FOR_TRANSLATE_WORD)) {
            handleListForWord(chatId, text);
        }
        else if (cndWord.equals(ConditionWord.WAIT_FOR_LIST_OF_WORD)) {
            finallyCreatedWord(chatId, text);
        }
    }


    private void handleSourceWordOfList(Long chatId) {
        if (userService.findByChatId(chatId).getWordLists().size() > 0) {
            cndWord = ConditionWord.WAIT_FOR_SOURCE_WORD;
            msgSender.sendMessage(chatId, "Enter a source word");
        }
        else {
            msgSender.sendMessage(
                    chatId,
                    "You don't have no one list of word\nPlease create one list before create word",
                    KeyboardBuilder.createFunctionalKeyboard()
            );
            cndWord = null;
        }
    }

    private void handleTranslateWordOfList(Long chatId, String sourceWord) {
        word.setSourceWord(sourceWord);
        cndWord = ConditionWord.WAIT_FOR_TRANSLATE_WORD;
        msgSender.sendMessage(chatId, "Enter a translate word");
    }

    private void handleListForWord(Long chatId, String translateWord) {
        word.setTranslateWord(translateWord);
        cndWord = ConditionWord.WAIT_FOR_LIST_OF_WORD;
        msgSender.sendMessage(chatId, "Enter a list of word", KeyboardBuilder.createKeyboardOfWordListOfUser(userService.findByChatId(chatId)));
    }

    private void finallyCreatedWord(Long chatId, String titleOfList) {
        var user = userService.findByChatId(chatId);
        var list = wordListService.findByTitleAndUser(titleOfList, user);
        if (list != null) {
            word.setWordList(list);
            word.setIsLearned(false);
            wordService.save(word);
            msgSender.sendMessage(chatId, "Word saved successfully", KeyboardBuilder.createFunctionalKeyboard());
            cndWord = null;
        }
        else {
            msgSender.sendMessage(chatId, "Enter a list of word", KeyboardBuilder.createKeyboardOfWordListOfUser(userService.findByChatId(chatId)));
        }
    }
}
