package com.example.LearnEnglishBot.handlers;

import com.example.LearnEnglishBot.service.UserService;
import com.example.LearnEnglishBot.util.KeyboardBuilder;
import com.example.LearnEnglishBot.util.MessageSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
public class CommandHandler {
    private final UserService userService;
    private final UserAuthHandler authHandler;
    private MessageSender msgSender;
    public CommandHandler(UserService userService, UserAuthHandler authHandler) {
        this.userService = userService;
        this.authHandler = authHandler;
    }

    @Autowired
    @Lazy
    public void setMsgSender(MessageSender msgSender) {
        this.msgSender = msgSender;
    }

    public void startMessage(Long chatId) {
        if (userService.findByChatId(chatId) != null) {
            msgSender.sendMessage(chatId, "👋 Hi! I'm a bot for learning new words.\n📖 Here, you can add new words and learn them.");
            msgSender.sendMessage(chatId, "👉 You can use all the cool features of this bot now 😎", KeyboardBuilder.createFunctionalKeyboard());
            authHandler.setCndAuth(null);
        } else {
            msgSender.sendMessage(chatId, """
                                    👋 Hi! I'm a bot for learning English words.
                                    📖 Here, you can add new words and learn them.
                                    ❗You need to authorized in the system
                                    """,
                    KeyboardBuilder.createKeyboardOfList(KeyboardBuilder.authTitles)
            );
        }
    }
}
