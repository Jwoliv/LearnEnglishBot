package com.example.LearnEnglishBot.handlers;

import com.example.LearnEnglishBot.model.user.ConditionAuth;
import com.example.LearnEnglishBot.model.user.User;
import com.example.LearnEnglishBot.service.UserService;
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
public class UserAuthHandler {
    private ConditionAuth cndAuth;
    private MessageSender msgSender;
    private final UserService userService;
    private String username;
    private String password;

    public UserAuthHandler(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    @Lazy
    public void setMsgSender(MessageSender msgSender) {
        this.msgSender = msgSender;
    }

    public void handleInitialAuthInput(Long chatId, String text) {
        if (text.equals("Login")) {
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
    }
    public void handleSignUpInput(Long chatId, String text) {
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
            userService.singIn(username, password, chatId);
            msgSender.sendMessage(chatId, "✅ User saved successfully");
            msgSender.sendMessage(chatId, "👉 You can use all the cool features of this bot now 😎", KeyboardBuilder.createFunctionalKeyboard());
            cndAuth = ConditionAuth.FINISH;
        }
    }

    public void handleLoginInput(Long chatId, String text) {
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
            boolean isLogin = userService.login(username, password, chatId);
            if (isLogin) {
                msgSender.sendMessage(chatId, "✅ User login successful");
                msgSender.sendMessage(chatId, "👉 You can use all the cool features of this bot now 😎", KeyboardBuilder.createFunctionalKeyboard());
                cndAuth = ConditionAuth.FINISH;
            }
            else {
                msgSender.sendMessage(chatId, "❌ Wrong password please try again");
            }
        }
    }
}
