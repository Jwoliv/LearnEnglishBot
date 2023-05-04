package com.example.LearnEnglishBot.handlers;

import com.example.LearnEnglishBot.model.test.ConditionTest;
import com.example.LearnEnglishBot.model.test.Test;
import com.example.LearnEnglishBot.model.test.TypeTest;
import com.example.LearnEnglishBot.model.word.Word;
import com.example.LearnEnglishBot.model.wordList.WordList;
import com.example.LearnEnglishBot.service.TestService;
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
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

@Component
@Getter
@Setter
public class TestHandler {
    private final UserService userService;
    private final WordListService wordListService;
    private final WordService wordService;
    private final TestService testService;

    private MessageSender msgSender;
    private ConditionTest cndTest;

    private Iterator<Word> iteratorWords;
    private List<Word> words;
    private WordList wordList;
    private Word selectedWord;
    private TypeTest typeTest;

    public TestHandler(UserService userService, WordListService wordListService, WordService wordService, TestService testService) {
        this.userService = userService;
        this.wordListService = wordListService;
        this.wordService = wordService;
        this.testService = testService;
    }

    @Autowired
    @Lazy
    public void setMsgSender(MessageSender msgSender) {
        this.msgSender = msgSender;
    }

    public void activeTest(Long chatId, String text) {
        if (text.equals("📝 Take test")) {
            handleSelectedList(chatId);
        }
        else if (cndTest.equals(ConditionTest.SELECT_LIST)) {
            selectedListForTest(chatId, text);
        }
        else if (cndTest.equals(ConditionTest.SELECT_TYPE)) {
            typeTest = TypeTest.valueOf(text);
            if (typeTest.equals(TypeTest.FLASH_CARD)) {
                flashCardInstruction(chatId);
            }
        }

        else if (typeTest.equals(TypeTest.FLASH_CARD)) {
            if (cndTest.equals(ConditionTest.ITEM_TRANSACTION_WORD)) {
                if (text.equals("Yup, I know")) {
                    msgSender.sendMessage(
                            chatId,
                            "👀 You translated this word like this: " + selectedWord.getTranslateWord(),
                            new ReplyKeyboardMarkup(
                                    List.of(
                                            new KeyboardRow(Set.of(new KeyboardButton("Yup"))),
                                            new KeyboardRow(Set.of(new KeyboardButton("Nope")))
                                    )
                            ));
                    cndTest = ConditionTest.WAIT_FOR_CONFIRM_MSG;
                }
                else if (text.equals("Nope, don't know")) {
                    selectedWord.setIsLearned(false);
                    wordService.save(selectedWord);
                    msgSender.sendMessage(chatId, "🔍 Translate: " + selectedWord.getTranslateWord());
                    getMsgOfSourceWord(chatId);
                }
            }
            else if (cndTest.equals(ConditionTest.WAIT_FOR_CONFIRM_MSG)) {
                confirmTranslate(text);
                getMsgOfSourceWord(chatId);
            }

            else if (cndTest.equals(ConditionTest.ITEM_SOURCE_WORD)) {
                getMsgOfSourceWord(chatId);
            }
        }
    }


    private void selectedListForTest(Long chatId, String text) {
        var user = userService.findByChatId(chatId);
        var list = wordListService.findByTitleAndUser(text, user);
        if (list != null) {
            words = list.getWords();
            if (words.size() > 0) {
                wordList = words.get(0).getWordList();
                cndTest = ConditionTest.SELECT_TYPE;
                iteratorWords = words.iterator();
                msgSender.sendMessage(chatId, "📝 Selected the type of test", KeyboardBuilder.createKeyboardOfEnum(TypeTest.class));
            }
            else {
                msgSender.sendMessage(chatId, "🗒️ List is empty\nPlease try again", KeyboardBuilder.createKeyboardOfWordListOfUser(user));
            }
        }
        else {
            msgSender.sendMessage(chatId, "🤔 Wrong title of list\nPlease try again", KeyboardBuilder.createKeyboardOfWordListOfUser(user));
        }
    }

    private void flashCardInstruction(Long chatId) {
        msgSender.sendMessage(chatId,
                """
                👋 Hey, welcome to the flashcard test! It's time to test your word knowledge, my friend. Here's the deal - you'll have two options:
                👉 'Yup, I know' or
                🤔 'Nope, don't know'
                                    
                If you choose the first option, you are asked about your knowledge of the word.
                👌 Easy peasy, right? So, let's get started and good luck to ya! 🍀
                """);
        sendMessageForWordWithOptions(chatId);
        cndTest = ConditionTest.ITEM_TRANSACTION_WORD;
    }

    private void confirmTranslate(String text) {
        if (text.equals("Yup")) {
            selectedWord.setIsLearned(true);
        }
        else if (text.equals("Nope")) {
            selectedWord.setIsLearned(false);
        }
        wordService.save(selectedWord);
        cndTest = ConditionTest.ITEM_SOURCE_WORD;
    }

    private void getMsgOfSourceWord(Long chatId) {
        if (iteratorWords.hasNext()) {
            sendMessageForWordWithOptions(chatId);
            cndTest = ConditionTest.ITEM_TRANSACTION_WORD;
        }
        else {
            var user = userService.findByChatId(chatId);
            var numberOfCorrect = Math.toIntExact(words.stream().filter(x -> x.getIsLearned().equals(true)).count());
            var numberOfWrong = Math.toIntExact(words.stream().filter(x -> x.getIsLearned().equals(false)).count());
            Test test = Test.builder()
                    .wordList(wordList)
                    .numberOfCorrect(numberOfCorrect)
                    .numberOfWrong(numberOfWrong)
                    .user(user)
                    .typeTest(typeTest)
                    .build();

            testService.save(test);
            msgSender.sendMessage(chatId, String.format("""
                    🏁 Test is completed
                    ✅ Correct answer: %d
                    🚫 Wrong answer: %d
                    📚 List: %s
                    
                    ⭐ Assessment: %d%%
                    """, numberOfCorrect, numberOfWrong, wordList.getTitle(), test.getAssessment()), KeyboardBuilder.createFunctionalKeyboard());
            cndTest = null;
        }
    }

    private void sendMessageForWordWithOptions(Long chatId) {
        selectedWord =  iteratorWords.next();
        msgSender.sendMessage(chatId, selectedWord.getSourceWord(), new ReplyKeyboardMarkup(
                List.of(
                        new KeyboardRow(Set.of(new KeyboardButton("Yup, I know"))),
                        new KeyboardRow(Set.of(new KeyboardButton("Nope, don't know")))
                )
        ));
    }

    private void handleSelectedList(Long chatId) {
        cndTest = ConditionTest.SELECT_LIST;
        var user = userService.findByChatId(chatId);
        if (user.getWords().size() > 0) {
            msgSender.sendMessage(chatId, "🔍 Select the list for test", KeyboardBuilder.createKeyboardOfWordListOfUser(user));
        }
        else {
            msgSender.sendMessage(chatId, "😕 You don't have any words", KeyboardBuilder.createFunctionalKeyboard());
            cndTest = null;
        }
    }

}
