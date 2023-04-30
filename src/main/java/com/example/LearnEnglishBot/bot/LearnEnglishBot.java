package com.example.LearnEnglishBot.bot;

import com.example.LearnEnglishBot.model.user.ConditionAuth;
import com.example.LearnEnglishBot.model.user.User;
import com.example.LearnEnglishBot.service.UserService;
import com.example.LearnEnglishBot.util.KeyboardBuilder;
import com.example.LearnEnglishBot.util.MessageSender;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class LearnEnglishBot extends TelegramLongPollingBot {
    private ConditionAuth cndAuth;
    private final UserService userService;
    private String username;
    private String password;
    private final MessageSender msgSender;

    public LearnEnglishBot(UserService userService, MessageSender messageSender) {
        this.userService = userService;
        this.msgSender = messageSender;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            Message msg = update.getMessage();
            String text = msg.getText();
            long chatId = msg.getChatId();
            if (text.equals("/start")) {
                if (cndAuth.equals(ConditionAuth.FINISH)) {
                    msgSender.sendMessage(chatId, "👋 Hi! I'm a bot for learning English words.\n📖 Here, you can add new words and learn them.");
                } else {
                    msgSender.sendMessage(chatId, """
                                    👋 Hi! I'm a bot for learning English words.
                                    📖 Here, you can add new words and learn them.
                                    ❗You need to authorized in the system
                                    """,
                            KeyboardBuilder.createAccountKeyboard()
                    );
                    cndAuth = ConditionAuth.START_AUTH;
                }
            }

            else if (text.equals("Login")) {
                cndAuth = ConditionAuth.LOGIN_WAIT_FOR_USERNAME;
                msgSender.sendMessage(chatId, "🔒 Please enter your username");
            }
            else if (text.equals("Sing in")) {
                if (userService.findByChatId(chatId) == null) {
                    cndAuth = ConditionAuth.SING_IN_WAIT_FOR_USERNAME;
                    msgSender.sendMessage(chatId, "🔒 Please enter new username");
                } else {
                    msgSender.sendMessage(chatId, "❌ There is already an active user in this session\n🤔 Please try again", KeyboardBuilder.createAccountKeyboard());
                }
            }

            else if (cndAuth.toString().startsWith("SING_IN")) {
                if (cndAuth.equals(ConditionAuth.SING_IN_WAIT_FOR_USERNAME)) {
                    username = text;
                    User user = userService.findByUsername(username);
                    if (user != null) {
                        msgSender.sendMessage(chatId, "❗ User with so username has already exist\n🔒 Enter other username");
                    } else {
                        msgSender.sendMessage(chatId, "🔒 Please enter new password");
                        cndAuth = ConditionAuth.SING_IN_WAIT_FOR_PASSWORD;
                    }
                }
                else if (cndAuth.equals(ConditionAuth.SING_IN_WAIT_FOR_PASSWORD)) {
                    password = text;
                    User newUser = User.builder().username(username).password(password).chatId(chatId).build();
                    userService.save(newUser);
                    msgSender.sendMessage(chatId, "✅ User saved successfully");
                    cndAuth = ConditionAuth.FINISH;
                }
            }
            else if (cndAuth.toString().startsWith("LOGIN")) {
                if (cndAuth.equals(ConditionAuth.LOGIN_WAIT_FOR_USERNAME)) {
                    username = text;
                    User user = userService.findByUsername(username);
                    if (user == null) {
                        msgSender.sendMessage(chatId, "❗ User with so username doesn't exist\n🔒 Enter username again");
                    } else {
                        msgSender.sendMessage(chatId, "🔒 Enter new password");
                        cndAuth = ConditionAuth.LOGIN_WAIT_FOR_PASSWORD;
                    }
                }
                else if (cndAuth.equals(ConditionAuth.LOGIN_WAIT_FOR_PASSWORD)) {
                    password = text;
                    User user = userService.findByUsername(username);
                    if (user.getPassword().equals(password)) {
                        user.setChatId(chatId);
                        userService.save(user);
                        msgSender.sendMessage(chatId, "✅ User login successful");
                        msgSender.sendMessage(chatId, "👉 Yo, you're in! You can use all the cool features of this bot now 😎", KeyboardBuilder.createFunctionalKeyboard());
                        cndAuth = ConditionAuth.FINISH;
                    }
                    else {
                        msgSender.sendMessage(chatId, "❌ Wrong password please try again");
                    }
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
