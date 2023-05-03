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

import java.util.Arrays;

@Component
@Getter
@Setter
public class WordHandler {
    private ConditionWord cndWord;
    private MessageSender msgSender;
    private final WordService wordService;
    private final UserService userService;
    private final WordListService wordListService;
    private String titleList;
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
        else if (text.equals("🗑️ Delete word")) {
            handleForSelectedListDuringDelete(chatId);
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
        else if (cndWord.equals(ConditionWord.SELECTED_LIST_FOR_DELETE)) {
            handleChoiceList(chatId, text);
        }
        else if (cndWord.equals(ConditionWord.DELETE_WORD)) {
            deleteSelectedWord(chatId, text);
        }
    }


    private void handleForSelectedListDuringDelete(Long chatId) {
        var user = userService.findByChatId(chatId);
        if (user.getWordLists().size() > 0) {
            cndWord = ConditionWord.SELECTED_LIST_FOR_DELETE;
            msgSender.sendMessage(chatId,
                    "📋 Select the list from which you want to remove word",
                    KeyboardBuilder.createKeyboardOfWordListOfUser(user)
            );
        }
        else {
            msgSender.sendMessage(chatId,
                    "❌ You dont have any list",
                    KeyboardBuilder.createFunctionalKeyboard()
            );
        }
    }

    private void handleChoiceList(Long chatId, String text) {
        titleList = text;
        var user = userService.findByChatId(chatId);
        var list = wordListService.findByTitleAndUser(titleList, user);
        var sizeOfList = list.getWords().size();
        if (sizeOfList > 0) {
            cndWord = ConditionWord.DELETE_WORD;
            msgSender.sendMessage(chatId,
                    "📝 Select the word what you want to delete",
                    KeyboardBuilder.createKeyboardOfWordsOfUser(userService.findByChatId(chatId), text)
            );
        }
        else {
            msgSender.sendMessage(chatId,
                    "❌ This list is empty",
                    KeyboardBuilder.createFunctionalKeyboard()
            );
        }
    }

    private void deleteSelectedWord(Long chatId, String text) {
        var sourceTitle = Arrays.stream(text.split(" - ")).toList().get(0);
        var user = userService.findByChatId(chatId);
        var list = user.getWordLists().stream().filter(x -> x.getTitle().equals(titleList)).findFirst().orElse(null);
        if (list != null) {
            var word = list.getWords().stream().filter(x -> x.getSourceWord().equals(sourceTitle)).findFirst().orElse(null);
            if (word != null) {
                wordService.deleteById(word.getId());
                msgSender.sendMessage(chatId, "🗑️ Word deleted successfully", KeyboardBuilder.createFunctionalKeyboard());
                cndWord = null;
            }
            else {
                msgSender.sendMessage(chatId, "🤔 This word isn't exist in this list\n✍️ Please try selected correct word", KeyboardBuilder.createKeyboardOfWordsOfUser(user, titleList));
            }
        }
    }


    private void handleSourceWordOfList(Long chatId) {
        if (userService.findByChatId(chatId).getWordLists().size() > 0) {
            cndWord = ConditionWord.WAIT_FOR_SOURCE_WORD;
            msgSender.sendMessage(chatId, "✍️ Enter a source word");
        }
        else {
            msgSender.sendMessage(
                    chatId,
                    "🤔 You don't have no one list of word\n ✍️ Please create one list before create word",
                    KeyboardBuilder.createFunctionalKeyboard()
            );
            cndWord = null;
        }
    }

    private void handleTranslateWordOfList(Long chatId, String sourceWord) {
        word.setSourceWord(sourceWord);
        cndWord = ConditionWord.WAIT_FOR_TRANSLATE_WORD;
        msgSender.sendMessage(chatId, "🌐 Enter a translate word");
    }

    private void handleListForWord(Long chatId, String translateWord) {
        word.setTranslateWord(translateWord);
        cndWord = ConditionWord.WAIT_FOR_LIST_OF_WORD;
        msgSender.sendMessage(chatId, "📋 Enter a list of word", KeyboardBuilder.createKeyboardOfWordListOfUser(userService.findByChatId(chatId)));
    }

    private void finallyCreatedWord(Long chatId, String titleOfList) {
        var user = userService.findByChatId(chatId);
        var list = wordListService.findByTitleAndUser(titleOfList, user);
        if (list != null) {
            word.setId(null);
            word.setWordList(list);
            word.setIsLearned(false);
            word.setUser(user);
            wordService.save(word);
            msgSender.sendMessage(chatId, "✅ Word saved successfully", KeyboardBuilder.createFunctionalKeyboard());
            cndWord = null;
        }
        else {
            msgSender.sendMessage(chatId, "📋 Enter a list of word", KeyboardBuilder.createKeyboardOfWordListOfUser(userService.findByChatId(chatId)));
        }
    }
}
