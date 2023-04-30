package com.example.LearnEnglishBot.util;

import com.example.LearnEnglishBot.bot.LearnEnglishBot;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class MessageSender {
    private LearnEnglishBot learnEnglishBot;

    public void setLearnEnglishBot(LearnEnglishBot learnEnglishBot) {
        this.learnEnglishBot = learnEnglishBot;
    }

    public void sendMessage(long chatId, String messageText) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(messageText);
        try {
            learnEnglishBot.execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(long chatId, String text, ReplyKeyboardMarkup keyboardMarkup) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(text);
        message.setReplyMarkup(keyboardMarkup);

        try {
            learnEnglishBot.execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

}