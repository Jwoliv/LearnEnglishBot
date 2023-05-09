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
import com.example.LearnEnglishBot.util.FormatTime;
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

import java.time.Duration;
import java.time.LocalDateTime;
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
    private LocalDateTime startTime;

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
        else if (text.equals("📊 All tests")) {
            selectedAllTests(chatId);
        }
        else if (cndTest.equals(ConditionTest.SELECT_ALL)) {
            handleSelectedListForTest(chatId, text);
        }
        else if (cndTest.equals(ConditionTest.SELECT_LIST)) {
            selectedListForTest(chatId, text);
        }
        else if (cndTest.equals(ConditionTest.SELECT_TYPE)) {
            handleTypeOfTest(chatId, text);
        }
        else if (typeTest.equals(TypeTest.FLASH_CARD)) {
            processingOfFlashCard(chatId, text);
        }
        else if (typeTest.equals(TypeTest.WRITING_TEST)) {
            processingOfWritingTest(chatId, text);
        }
    }

    //region methods for test king of writing check
    private void writingTestInstruction(Long chatId) {
        msgSender.sendMessage(chatId, """
                👋 Hey, welcome to the writing test!
                👉 Here we checked a your writing of the words
                🤔 Don't worry register of the word isn't important

                👌 Let's get started and good luck to ya! 🍀
                """);
        sendMessageForWordOfWritingTest(chatId);
        cndTest = ConditionTest.ITEM_TRANSLATION_WORD;
    }

    public void processingOfWritingTest(Long chatId, String text) {
        if (cndTest.equals(ConditionTest.ITEM_TRANSLATION_WORD)) {
            processOfTranslationWord(chatId, text);
        }
        else if (cndTest.equals(ConditionTest.ITEM_SOURCE_WORD)) {
            sendMessageForWordOfWritingTest(chatId);
        }
    }

    private void sendWordForWriting(Long chatId) {
        if (iteratorWords.hasNext()) {
            selectedWord = iteratorWords.next();
            msgSender.sendMessage(chatId, selectedWord.getTranslateWord());
            cndTest = ConditionTest.ITEM_TRANSLATION_WORD;
        }
        else {
            saveTestResultsAndSendInfoToUser(chatId);
        }
    }

    private void processOfTranslationWord(Long chatId, String text) {
        if (text.equalsIgnoreCase(selectedWord.getSourceWord())) {
            if (!selectedWord.getIsLearned()) {
                selectedWord.setIsLearned(true);
                wordService.save(selectedWord);
            }
            msgSender.sendMessage(chatId, "Correct answer :)");
        }
        else {
            if (selectedWord.getIsLearned()) {
                selectedWord.setIsLearned(false);
                wordService.save(selectedWord);
            }
            msgSender.sendMessage(chatId, "Wrong answer :)");
        }
        cndTest = ConditionTest.ITEM_SOURCE_WORD;
        sendWordForWriting(chatId);
    }

    private void sendMessageForWordOfWritingTest(Long chatId) {
        selectedWord =  iteratorWords.next();
        msgSender.sendMessage(chatId, selectedWord.getTranslateWord());
    }
    //endregion methods for test king of writing check


    //region methods for test kind of the flash card
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
        cndTest = ConditionTest.ITEM_TRANSLATION_WORD;
    }

    private void processingOfFlashCard(Long chatId, String text) {
        if (cndTest.equals(ConditionTest.ITEM_TRANSLATION_WORD)) {
            processingTranslateWord(chatId, text);
        }
        else if (cndTest.equals(ConditionTest.WAIT_FOR_CONFIRM_MSG)) {
            confirmTranslate(text);
            getMsgOfSourceWord(chatId);
        }

        else if (cndTest.equals(ConditionTest.ITEM_SOURCE_WORD)) {
            getMsgOfSourceWord(chatId);
        }
    }

    private void confirmTranslate(String text) {
        if (text.equals("Yup")) {
            if (selectedWord.getIsLearned()) {
                selectedWord.setIsLearned(true);
                wordService.save(selectedWord);
            }
        }
        else if (text.equals("Nope")) {
            if (!selectedWord.getIsLearned()) {
                selectedWord.setIsLearned(false);
                wordService.save(selectedWord);
            }
        }
        cndTest = ConditionTest.ITEM_SOURCE_WORD;
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
    private void getMsgOfSourceWord(Long chatId) {
        if (iteratorWords.hasNext()) {
            sendMessageForWordWithOptions(chatId);
            cndTest = ConditionTest.ITEM_TRANSLATION_WORD;
        }
        else {
            saveTestResultsAndSendInfoToUser(chatId);
        }
    }
    //endregion methods for test kind of the flash card


    //region main methods for processing test
    private void processingTranslateWord(Long chatId, String text) {
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
    private void selectedAllTests(Long chatId) {
        var user = userService.findByChatId(chatId);
        if (user.getTests().size() > 0) {
            cndTest = ConditionTest.SELECT_ALL;
            msgSender.sendMessage(chatId, "Your tests", KeyboardBuilder.createKeyboardOfTests(user));
        }
        else {
            msgSender.sendMessage(chatId, "You don't have tests", KeyboardBuilder.createFunctionalKeyboard());
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
                msgSender.sendMessage(chatId, "🗒️ List is empty\nPlease try again", KeyboardBuilder.createKeyboardOfWordListOfUser(user.getWordLists()));
            }
        }
        else {
            msgSender.sendMessage(chatId, "🤔 Wrong title of list\nPlease try again", KeyboardBuilder.createKeyboardOfWordListOfUser(user.getWordLists()));
        }
    }

    private void handleSelectedList(Long chatId) {
        cndTest = ConditionTest.SELECT_LIST;
        var user = userService.findByChatId(chatId);
        if (user.getWords().size() > 0) {
            msgSender.sendMessage(chatId, "🔍 Select the list for test", KeyboardBuilder.createKeyboardOfWordListOfUser(user.getWordLists()));
        }
        else {
            msgSender.sendMessage(chatId, "😕 You don't have any words", KeyboardBuilder.createFunctionalKeyboard());
            cndTest = null;
        }
    }

    private void handleTypeOfTest(Long chatId, String text) {
        typeTest = TypeTest.valueOf(text);
        if (typeTest.equals(TypeTest.FLASH_CARD)) {
            flashCardInstruction(chatId);
        }
        else if (typeTest.equals(TypeTest.WRITING_TEST)) {
            writingTestInstruction(chatId);
        }
        startTime = LocalDateTime.now();
    }

    private void handleSelectedListForTest(Long chatId, String text) {
        var parts = text.split("\\)");
        var user = userService.findByChatId(chatId);
        if (parts.length > 0 && parts[0].matches("\\d+")) {
            var id = Long.parseLong(parts[0]);
            var test = testService.findById(id).get();
            if (test.getUser().getId().equals(user.getId())) {
                sendMessageAboutTest(chatId, test, new StringBuilder());
                cndTest = null;
            }
        } else {
            msgSender.sendMessage(chatId, "👀 Wrong name of test\n🔍 Try again", KeyboardBuilder.createKeyboardOfTests(user));
        }
    }

    private void saveTestResultsAndSendInfoToUser(Long chatId) {
        var user = userService.findByChatId(chatId);
        var numberOfCorrect = Math.toIntExact(wordList.getWords().stream().filter(x -> x.getIsLearned().equals(true)).count());
        var numberOfWrong = Math.toIntExact(wordList.getWords().stream().filter(x -> x.getIsLearned().equals(false)).count());

        var finishTime = LocalDateTime.now();


        Test test = Test.builder()
                .wordList(wordList)
                .numberOfCorrect(numberOfCorrect)
                .numberOfWrong(numberOfWrong)
                .user(user)
                .startTime(startTime)
                .finishTime(finishTime)
                .spendTime(Duration.between(startTime, finishTime))
                .typeTest(typeTest)
                .build();

        testService.save(test);
        StringBuilder sb = new StringBuilder();
        sb.append("🏁 Tests is finished\n");
        sendMessageAboutTest(chatId, test, sb);
        cndTest = null;
    }
    private void sendMessageAboutTest(Long chatId, Test test, StringBuilder sb) {
        sb.append(String.format("""
                    📚  Info about test
                    ✅ Correct answer: %d
                    🚫 Wrong answer: %d
                    📚 List: %s
                    🕰️ Start time: %s
                    ⏰ End Time:  %s
                    🕔 Spend Time: %s
                    
                    ⭐ Assessment: %d%%""",
                test.getNumberOfCorrect(), test.getNumberOfWrong(), test.getWordList().getTitle(),
                FormatTime.formattedTime(test.getStartTime()), FormatTime.formattedTime(test.getFinishTime()),
                test.getSpendTime() , test.getAssessment())
        );
        msgSender.sendMessage(chatId, sb.toString(), KeyboardBuilder.createFunctionalKeyboard());
    }
    //endregion main methods for processing test

}
