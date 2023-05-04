package com.example.LearnEnglishBot.bot;

import com.example.LearnEnglishBot.handlers.*;
import com.example.LearnEnglishBot.util.KeyboardBuilder;
import com.example.LearnEnglishBot.util.MessageSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class LearnEnglishBot extends TelegramLongPollingBot {
    private final UserAuthHandler authHandler;
    private final WordListHandler wordListHandler;
    private final CommandHandler cmdHandler;
    private final WordHandler wordHandler;
    private final ProfileHandler profileHandler;
    private final TestHandler testHandler;
    private MessageSender msgSender;

    public LearnEnglishBot(UserAuthHandler authHandler, WordListHandler wordListHandler, CommandHandler cmdHandler, WordHandler wordHandler, ProfileHandler profileHandler, TestHandler testHandler) {
        this.authHandler = authHandler;
        this.wordListHandler = wordListHandler;
        this.cmdHandler = cmdHandler;
        this.wordHandler = wordHandler;
        this.profileHandler = profileHandler;
        this.testHandler = testHandler;
    }

    @Autowired
    @Lazy
    public void setMsgSender(MessageSender msgSender) {
        this.msgSender = msgSender;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            Message msg = update.getMessage();
            String text = msg.getText();
            long chatId = msg.getChatId();
            if (text.equals("/start")) {
                resetTrackingStatus();
                cmdHandler.startMessage(chatId);
            }
            else if (text.equals("/reset")) {
                resetTrackingStatus();
                msgSender.sendMessage(chatId, "👉 You can use all the cool features of this bot now 😎", KeyboardBuilder.createFunctionalKeyboard());
            }
            else {
                if (text.equals("Login") || text.equals("Sing in")) {
                    authHandler.handleInitialAuthInput(chatId, text);
                }
                else if (text.equals("📚 All lists")) {
                    wordListHandler.handlerGetAllListsByUser(chatId);
                }
                else if (text.equals("🗑️ Delete list") || text.equals("❌ Delete all lists")) {
                    wordListHandler.activeWithDeleteList(chatId, text);
                }
                else if (text.equals("👤 Profile")) {
                    profileHandler.profileAnswer(chatId);
                }
                else if (text.equals("💪 Top 20")) {
                    profileHandler.profileTop20Users(chatId);
                }
                else if (testHandler.getCndTest() != null || text.equals("📝 Take test") || text.equals("📊 All tests")) {
                    testHandler.activeTest(chatId, text);
                }
                else if (wordListHandler.getCndWordList() != null || text.equals("🆕 New list")) {
                    wordListHandler.activeWithList(chatId, text);
                }
                else if (wordHandler.getCndWord() != null || text.equals("🆕 New word") || text.equals("🗑️ Delete word")) {
                    wordHandler.activeWord(chatId, text);
                }
                else if (authHandler.getCndAuth().toString().startsWith("SING_IN")) {
                    authHandler.handleSignUpInput(chatId, text);
                }
                else if (authHandler.getCndAuth().toString().startsWith("LOGIN")) {
                    authHandler.handleLoginInput(chatId, text);
                }
            }
        }
    }

    @Override
    public String getBotUsername() {
        return "learn_eng_ua_bot";
    }

    @Override
    public String getBotToken() {
        return "6222379522:AAEBvxLMf7xhpo1qzqiH3IomWhPLa2aiI40";
    }

    public void resetTrackingStatus() {
        testHandler.setCndTest(null);
        wordListHandler.setCndWordList(null);
        wordHandler.setCndWord(null);
        authHandler.setCndAuth(null);
    }
}
