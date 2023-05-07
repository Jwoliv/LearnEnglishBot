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
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.Collections;
import java.util.List;

@Component
@Getter
@Setter
public class WordListHandler {
    private String title;
    private Category category;
    private EnglishLevel engLvl;
    private AccessLevel accessLevel;
    private Integer number;
    private String username;
    private List<WordList> wordLists;
    private WordList selectedList;
    private String newTitle;

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
        else if (text.equals("👀 Find lists")) {
            cndWordList = ConditionWordList.WAIT_FOR_CATEGORY_OF_SUGGESTED_TEST;
            msgSender.sendMessage(chatId, "👀 Select category of lists:", KeyboardBuilder.createKeyboardOfEnum(Category.class));
        }
        else if (cndWordList.equals(ConditionWordList.WAIT_FOR_CATEGORY_OF_SUGGESTED_TEST)) {
            handleCategoryOfSuggestedList(chatId, text);
        }
        else if (cndWordList.equals(ConditionWordList.WAIT_FOR_NUMBER_OF_LIST)) {
            handleNumberOfSuggestedList(chatId, text);
        }
        else if (cndWordList.equals(ConditionWordList.WAIT_FOR_SELECT_LIST)) {
            handleSelectedList(chatId, text);
        }
        else if (cndWordList.equals(ConditionWordList.WAIT_FOR_TITLE_OF_ADDED_LIST)) {
            handleNewTitleOfList(chatId, text);
        }
        else if (cndWordList.equals(ConditionWordList.SELECTED_LIST_FOR_ACTIVITY)) {
            activeWithSuggestedList(chatId, text);
        }
        else if (cndWordList.equals(ConditionWordList.WAIT_FOR_NAME)) {
            handleCategoryOfList(chatId, text);
        }
        else if (cndWordList.equals(ConditionWordList.WAIT_FOR_CATEGORY)) {
            handlerEnglishLevel(chatId, text);
        }
        else if (cndWordList.equals(ConditionWordList.WAIT_FOR_ENGLISH_LEVEL)) {
            handlerAccessLevel(chatId, EnglishLevel.valueOf(text));
        }
        else if (cndWordList.equals(ConditionWordList.WAIT_FOR_ACCESS_LEVEL)) {
            finallyCreatedListOfWords(chatId, AccessLevel.valueOf(text));
        }
        else if (cndWordList.equals(ConditionWordList.DELETE_LIST) && userService.findByChatId(chatId).getWordLists().stream().map(WordList::getTitle).toList().contains(text)) {
            deleteList(chatId, text);
        }
        else if (cndWordList.equals(ConditionWordList.DELETE_ALL)) {
            deleteAllListByUser(chatId, text);
        }
        else if (cndWordList.equals(ConditionWordList.SELECT_ALL)) {
            selectedWordsOfList(chatId, text);
        }
    }

    public void handlerGetAllListsByUser(Long chatId) {
        if (userService.findByChatId(chatId).getWordLists().size() != 0) {
            cndWordList = ConditionWordList.SELECT_ALL;
            msgSender.sendMessage(chatId, "📚 Your lists of words", KeyboardBuilder.createKeyboardOfWordListOfUser(userService.findByChatId(chatId).getWordLists()));
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



    private void handleNewTitleOfList(Long chatId, String text) {
        newTitle = text;
        msgSender.sendMessage(chatId, "❓ What do you want to do?", new ReplyKeyboardMarkup(
                Collections.singletonList(
                        new KeyboardRow(List.of(new KeyboardButton("Add"), new KeyboardButton("Pass")))
                )
        ));
        cndWordList = ConditionWordList.SELECTED_LIST_FOR_ACTIVITY;
    }
    private void handleNumberOfSuggestedList(Long chatId, String text) {
        try {
            number = Integer.valueOf(text);
            if (number > 0) {
                var user = userService.findByChatId(chatId);
                wordLists = wordListService.findSuggestedOfList(category, user.getId());
                if (wordLists.size() > number) {
                    wordLists.subList(0, number);
                }
                if (wordLists.size() > 0) {
                    msgSender.sendMessage(chatId, "📚 Select the list", KeyboardBuilder.createKeyboardOfWordListOfUser(wordLists));
                    cndWordList = ConditionWordList.WAIT_FOR_SELECT_LIST;
                } else {
                    msgSender.sendMessage(chatId, "👀 Lists are empty", KeyboardBuilder.createFunctionalKeyboard());
                    cndWordList = null;
                }
            } else {
                msgSender.sendMessage(chatId, "🚫 Wrong format of number\n📝 Enter a number of suggested lists");
            }
        }
        catch (Exception e) {
            msgSender.sendMessage(chatId, "🚫 Wrong format of number\n📝 Enter a number of suggested lists");
        }
    }

    private void activeWithSuggestedList(Long chatId, String text) {
        if (text.equals("Add")) {
            wordListService.save(selectedList);
            var currentUser = userService.findByChatId(chatId);
            var size = selectedList.getWords().size();
            if (size > 0) {
                if (!selectedList.getUser().getId().equals(currentUser.getId())) {
                    if (currentUser.getWordLists().stream().noneMatch(x -> x.getTitle().equals(newTitle))) {
                        var list = WordList.builder()
                                .id(null)
                                .title(newTitle)
                                .user(currentUser)
                                .reputation(0.0F)
                                .englishLevel(selectedList.getEnglishLevel())
                                .accessLevel(selectedList.getAccessLevel())
                                .category(selectedList.getCategory())
                                .build();

                        selectedList.setReputation(selectedList.getReputation() + 0.1F);
                        wordListService.save(selectedList);
                        var ownerOfList = selectedList.getUser();
                        ownerOfList.setReputation(ownerOfList.getReputation() + 0.1F);
                        userService.save(ownerOfList);

                        var words = selectedList.getWords();
                        wordListService.save(list);
                        words.forEach(x -> { x.setId(null); x.setWordList(list); x.setUser(currentUser);});
                        list.setWords(words);
                        wordListService.save(list);
                        msgSender.sendMessage(chatId, "✅ Add list to your lists", KeyboardBuilder.createFunctionalKeyboard());
                    }
                    else {
                        msgSender.sendMessage(chatId, "🗂️ You has already saved this list", KeyboardBuilder.createFunctionalKeyboard());
                    }
                }
                else {
                    msgSender.sendMessage(chatId, "😥 Sorry you can't save your own list", KeyboardBuilder.createFunctionalKeyboard());
                }
            }
            else {
                msgSender.sendMessage(chatId, "😥 This list is empty", KeyboardBuilder.createFunctionalKeyboard());
            }
        }
        else if (text.equals("Pass")) {
            msgSender.sendMessage(chatId, "🗂️ Main functional of this bot", KeyboardBuilder.createFunctionalKeyboard());
        }
        else {
            msgSender.sendMessage(chatId, "❌ Wrong operation", KeyboardBuilder.createFunctionalKeyboard());
        }
        cndWordList = null;
    }

    private void handleSelectedList(Long chatId, String text) {
        selectedList = wordLists.stream().filter(x -> x.getTitle().equals(text)).findFirst().orElse(null);
        if (selectedList != null && selectedList.getWords().size() > 0) {
            msgSender.sendMessage(chatId, sendMessageOfList(selectedList));
            msgSender.sendMessage(chatId, "❓ What title do you want to be set for this list?");
            cndWordList = ConditionWordList.WAIT_FOR_TITLE_OF_ADDED_LIST;
        }
        else {
            msgSender.sendMessage(chatId, "🚫 List is empty or don't has any words", KeyboardBuilder.createKeyboardOfWordListOfUser(wordLists));
        }
    }

    private void handleCategoryOfSuggestedList(Long chatId, String text) {
        try {
            category = Category.valueOf(text);
            cndWordList = ConditionWordList.WAIT_FOR_NUMBER_OF_LIST;
            msgSender.sendMessage(chatId, "📝 Enter a number of suggested lists: ");
        }
        catch (Exception e) {
            msgSender.sendMessage(chatId, "🚫 Wrong category\n📝 Select category of lists:", KeyboardBuilder.createKeyboardOfEnum(Category.class));
        }
    }

    private void selectedWordsOfList(Long chatId, String text) {
        var user = userService.findByChatId(chatId);
        var list = wordListService.findByTitleAndUser(text, user);
        if (list != null) {
            String str = sendMessageOfList(list);
            msgSender.sendMessage(chatId, str, KeyboardBuilder.createFunctionalKeyboard());
        }
        else {
            msgSender.sendMessage(chatId, "📝 Please enter a correct title of the list", KeyboardBuilder.createKeyboardOfWordListOfUser(user.getWordLists()));
        }
    }

    private void handlerDeleteSelectedList(Long chatId) {
        if (userService.findByChatId(chatId).getWordLists().size() != 0) {
            setCndWordList(ConditionWordList.DELETE_LIST);
            msgSender.sendMessage(chatId, "📚 Your lists of words", KeyboardBuilder.createKeyboardOfWordListOfUser(userService.findByChatId(chatId).getWordLists()));
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

    private void handlerEnglishLevel(Long chatId, String text) {
        category = Category.valueOf(text);
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
                .reputation(0.0F)
                .user(user)
                .accessLevel(accessLevel)
                .englishLevel(engLvl)
                .build();

        wordListService.save(wordList);
        msgSender.sendMessage(chatId, "✅ Saved the list successfully", KeyboardBuilder.createFunctionalKeyboard());
        cndWordList = null;
    }

    private String sendMessageOfList(WordList list) {
        var sb = new StringBuilder();
        var size = list.getWords().size();
        sb.append(String.format("""
                    📜 List: %s
                    📈 Number of words: %d
                    🔑 Access level: %s
                    🔤 English level: %s
                    🗂️ Category: %s
                    
                    
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
        cndWordList = null;
        return sb.toString();
    }

}
