package com.example.LearnEnglishBot.bot;

import com.example.LearnEnglishBot.handlers.UserAuthHandler;
import com.example.LearnEnglishBot.handlers.WordListHandler;
import com.example.LearnEnglishBot.model.user.ConditionAuth;
import com.example.LearnEnglishBot.service.UserService;
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

    private final UserService userService;
    private MessageSender msgSender;

    public LearnEnglishBot(UserService userService, UserAuthHandler authHandler, WordListHandler wordListHandler) {
        this.userService = userService;
        this.authHandler = authHandler;
        this.wordListHandler = wordListHandler;
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
                if (userService.findByChatId(chatId) != null) {
                    msgSender.sendMessage(chatId, "👋 Hi! I'm a bot for learning English words.\n📖 Here, you can add new words and learn them.");
                    msgSender.sendMessage(chatId, "👉 You can use all the cool features of this bot now 😎", KeyboardBuilder.createFunctionalKeyboard());
                    authHandler.setCndAuth(ConditionAuth.FINISH);
                } else {
                    msgSender.sendMessage(chatId, """
                                    👋 Hi! I'm a bot for learning English words.
                                    📖 Here, you can add new words and learn them.
                                    ❗You need to authorized in the system
                                    """,
                            KeyboardBuilder.createAccountKeyboard()
                    );
                }
            }
            else {
                if (text.equals("Login") || text.equals("Sing in")) {
                    authHandler.handleInitialAuthInput(chatId, text);
                }
                else if (text.equals("📚 All lists")) {
                    wordListHandler.handlerGetAllListsByUser(chatId);
                }
                else if (text.equals("🗑️ Delete list")) {
                    wordListHandler.handlerDeleteSelectedList(chatId);
                }
                else if (text.equals("❌ Delete all lists")) {
                    wordListHandler.handlerDeleteAllList(chatId);
                }
                else if (wordListHandler.getCndWordList() != null || text.equals("🆕 New list")) {
                    wordListHandler.activeWithList(chatId, text);
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

}
