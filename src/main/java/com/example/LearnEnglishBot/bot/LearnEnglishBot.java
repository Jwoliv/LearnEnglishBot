package com.example.LearnEnglishBot.bot;

import com.example.LearnEnglishBot.model.ConditionAuth;
import com.example.LearnEnglishBot.model.User;
import com.example.LearnEnglishBot.service.UserService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

@Component
public class LearnEnglishBot extends TelegramLongPollingBot {
    private ConditionAuth cndAuth;
    private final UserService userService;
    private String username;
    private String password;

    public LearnEnglishBot(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            Message msg = update.getMessage();
            String text = msg.getText();
            long chatId = msg.getChatId();
            if (text.equals("/start")) {
                sendMessage(chatId, """
                        👋 Hi! I'm a bot for learning English words.
                        📖 Here, you can add new words and learn them.
                        ❗You need to authorized in the system
                        """,
                        createAccountKeyboard()
                );
                cndAuth = ConditionAuth.START_AUTH;
            }

            else if (text.equals("Login")) {
                cndAuth = ConditionAuth.LOGIN_WAIT_FOR_USERNAME;
                sendMessage(chatId, "🔒 Please enter your username");
            }
            else if (text.equals("Sing in")) {
                if (userService.findByChatId(chatId) == null) {
                    cndAuth = ConditionAuth.SING_IN_WAIT_FOR_USERNAME;
                    sendMessage(chatId, "🔒 Please enter new username");
                } else {
                    sendMessage(chatId, "❌ There is already an active user in this session\n🤔 Please try again", createAccountKeyboard());
                }
            }

            else if (cndAuth.toString().startsWith("SING_IN")) {
                if (cndAuth.equals(ConditionAuth.SING_IN_WAIT_FOR_USERNAME)) {
                    username = text;
                    User user = userService.findByUsername(username);
                    if (user != null) {
                        sendMessage(chatId, "❗ User with so username has already exist\n🔒 Enter other username");
                    } else {
                        sendMessage(chatId, "🔒 Please enter new password");
                        cndAuth = ConditionAuth.SING_IN_WAIT_FOR_PASSWORD;
                    }
                }
                else if (cndAuth.equals(ConditionAuth.SING_IN_WAIT_FOR_PASSWORD)) {
                    password = text;
                    User newUser = User.builder().username(username).password(password).chatId(chatId).build();
                    userService.save(newUser);
                    sendMessage(chatId, "✅ User saved successfully");
                    cndAuth = ConditionAuth.FINISH;
                }
            }
            else if (cndAuth.toString().startsWith("LOGIN")) {
                if (cndAuth.equals(ConditionAuth.LOGIN_WAIT_FOR_USERNAME)) {
                    username = text;
                    User user = userService.findByUsername(username);
                    if (user == null) {
                        sendMessage(chatId, "❗ User with so username doesn't exist\n🔒 Enter username again");
                    } else {
                        sendMessage(chatId, "🔒 Enter new password");
                        cndAuth = ConditionAuth.LOGIN_WAIT_FOR_PASSWORD;
                    }
                }
                else if (cndAuth.equals(ConditionAuth.LOGIN_WAIT_FOR_PASSWORD)) {
                    password = text;
                    User user = userService.findByUsername(username);
                    if (user.getPassword().equals(password)) {
                        user.setChatId(chatId);
                        userService.save(user);
                        sendMessage(chatId, "✅ User login successful");
                        cndAuth = ConditionAuth.FINISH;
                    }
                    else {
                        sendMessage(chatId, "❌ Wrong password please try again");
                    }
                }
            }
        }
    }

    private ReplyKeyboardMarkup createAccountKeyboard() {
        ReplyKeyboardMarkup keyboard = new ReplyKeyboardMarkup();
        keyboard.setResizeKeyboard(true);
        keyboard.setOneTimeKeyboard(true);
        List<KeyboardRow> rows = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();
        row.add(new KeyboardButton("Login"));
        row.add(new KeyboardButton("Sing in"));
        rows.add(row);
        keyboard.setKeyboard(rows);
        return keyboard;
    }


    private void sendMessage(long chatId, String messageText) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(messageText);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    private void sendMessage(long chatId, String text, ReplyKeyboardMarkup keyboardMarkup) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(text);
        message.setReplyMarkup(keyboardMarkup);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
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
