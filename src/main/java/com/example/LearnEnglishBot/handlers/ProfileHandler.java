package com.example.LearnEnglishBot.handlers;

import com.example.LearnEnglishBot.model.user.User;
import com.example.LearnEnglishBot.service.UserService;
import com.example.LearnEnglishBot.util.KeyboardBuilder;
import com.example.LearnEnglishBot.util.MessageSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
public class ProfileHandler {
    private final UserService userService;
    private MessageSender msgSender;

    public ProfileHandler(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    @Lazy
    public void setMsgSender(MessageSender msgSender) {
        this.msgSender = msgSender;
    }

    public void profileAnswer(Long chatId) {
        var user = userService.findByChatId(chatId);
        var numberOfLists = user.getWordLists().size();
        var numberOfWords = user.getWords().size();
        var answer = String.format("""
               🆔 Id: %d
               👥 Username: %s
              
               📊 Number of lists: %d
               📝 Number of words: %d
               ⭐️ Reputation: %.2f
                """, user.getId(), user.getUsername(), numberOfLists, numberOfWords, user.getReputation());
        msgSender.sendMessage(chatId, answer, KeyboardBuilder.createFunctionalKeyboard());
    }

    public void profileTop20Users(Long chatId) {
        var numbers = IntStream.rangeClosed(1, 20).mapToObj(String::valueOf).toArray(String[]::new);
        var users = userService.findTop20Users();
        LinkedHashMap<String, User> numberUsers = IntStream.range(0, users.size())
                .boxed()
                .collect(Collectors.toMap(i -> numbers[i], users::get, (a, b) -> b, LinkedHashMap::new));

        StringBuilder sb = new StringBuilder();
        sb.append("👍 Top 20 users\n\n");
        for (var numberUser: numberUsers.entrySet()) {
            var number = numberUser.getKey();
            var user = numberUser.getValue();
            sb.append(number).append(". ").append(user.getUsername()).append(": ").append(user.getReputation()).append("\n");
        }
        msgSender.sendMessage(chatId, sb.toString(), KeyboardBuilder.createFunctionalKeyboard());
    }

}
