package com.example.LearnEnglishBot.handlers;

import com.example.LearnEnglishBot.model.notification.ConditionNotification;
import com.example.LearnEnglishBot.model.notification.Notification;
import com.example.LearnEnglishBot.model.notification.NotificationFrequency;
import com.example.LearnEnglishBot.service.NotificationService;
import com.example.LearnEnglishBot.service.UserService;
import com.example.LearnEnglishBot.util.FormatTime;
import com.example.LearnEnglishBot.util.KeyboardBuilder;
import com.example.LearnEnglishBot.util.MessageSender;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Getter
@Setter
public class NotificationHandler {
    private final UserService userService;
    private final NotificationService notificationService;
    private final Notification notification;


    private MessageSender msgSender;
    private ConditionNotification cndNot;

    public NotificationHandler(UserService userService, NotificationService notificationService, Notification notification) {
        this.userService = userService;
        this.notificationService = notificationService;
        this.notification = notification;
    }

    @Autowired
    @Lazy
    public void setMsgSender(MessageSender msgSender) {
        this.msgSender = msgSender;
    }

    public void activeOfNotification(Long chatId, String text) {
        if (text.equals("🆕 New notification")) {
            msgSender.sendMessage(chatId, "Enter a new title");
            cndNot = ConditionNotification.WAIT_FOR_TITLE;
        }
        else if (text.equals("🗑️ Delete notification")) {
            var user = userService.findByChatId(chatId);
            var notifications = user.getNotifications().stream().map(Notification::getTitle).toList();
            if (notifications.size() != 0) {
                msgSender.sendMessage(chatId, "All notifications\nSelect a notification that you want to delete", KeyboardBuilder.createKeyboardOfList(notifications));
                cndNot = ConditionNotification.DELETE_ITEM;
            }
            else {
                msgSender.sendMessage(chatId, "List of the notifications is empty", KeyboardBuilder.createFunctionalKeyboard());
                cndNot = null;
            }
        }
        else if (cndNot.equals(ConditionNotification.WAIT_FOR_TITLE)) {
            msgSender.sendMessage(chatId, "Enter a date `dd.mm.yyyy`");
            notification.setTitle(text);
            cndNot = ConditionNotification.WAIT_FOR_DATE;
        }
        else if (cndNot.equals(ConditionNotification.WAIT_FOR_DATE)) {
            var dateStr = text.split("\\.");
            if (dateStr.length == 3) {
                int day = Integer.parseInt(dateStr[0]);
                int month = Integer.parseInt(dateStr[1]);
                int year = Integer.parseInt(dateStr[2]);

                var date = LocalDateTime.of(year, month, day, 0, 0, 0);

                notification.setLocalDateTime(date);
                msgSender.sendMessage(chatId, "Wrong format of the time `hh:mm`");
                cndNot = ConditionNotification.WAIT_FOR_TIME;
            }
            else {
                msgSender.sendMessage(chatId, "Wrong format of the date\nPlease try again");
            }
        }
        else if (cndNot.equals(ConditionNotification.WAIT_FOR_TIME)) {
            var timeStr = text.split(":");
            if (timeStr.length == 2) {
                int hour = Integer.parseInt(timeStr[0]);
                int minute = Integer.parseInt(timeStr[1]);
                notification.getLocalDateTime().plusHours(hour);
                notification.getLocalDateTime().plusMinutes(minute);
                msgSender.sendMessage(
                        chatId,
                        "Select a frequency of the notification",
                        KeyboardBuilder.createKeyboardOfEnum(NotificationFrequency.class)
                );
                cndNot = ConditionNotification.WAIT_FOR_FREQUENCY;
            }
            else {
                msgSender.sendMessage(chatId, "Wrong format of the time\nPlease try again");
            }
        }
        else if (cndNot.equals(ConditionNotification.WAIT_FOR_FREQUENCY)) {
            try {
                var user = userService.findByChatId(chatId);
                notification.setFrequency(NotificationFrequency.valueOf(text));
                notification.setUser(user);

                notificationService.save(notification);
                msgSender.sendMessage(chatId, "Notification is saved successfully", KeyboardBuilder.createFunctionalKeyboard());
                cndNot = null;
            }
            catch (Exception e) {
                msgSender.sendMessage(chatId, "Wrong format of the frequency\nPlease try again");
            }
        }
        else if (cndNot.equals(ConditionNotification.DELETE_ITEM)) {
            var user = userService.findByChatId(chatId);
            var notifications = user.getNotifications();
            if (notifications.size() != 0) {
                var notification = notifications.stream().filter(x -> x.getTitle().equals(text)).findFirst().orElse(null);
                if (notification != null) {
                    long id = notification.getId();
                    notificationService.deleteById(id);
                    msgSender.sendMessage(chatId, "Notification delete successfully");
                    cndNot = null;
                }
                else {
                    var strNotification = notifications.stream().map(Notification::getTitle).toList();
                    msgSender.sendMessage(chatId, "Wrong title of the notifications\nSelect a notification that you want to delete", KeyboardBuilder.createKeyboardOfList(strNotification));
                }
            }
        }
    }

    public void messageOfAllNotifications(Long chatId) {
        var user = userService.findByChatId(chatId);
        var notifications = user.getNotifications();

        if (notifications.size() != 0) {
            StringBuilder stringBuilder = new StringBuilder();
            for (var not : notifications) {
                stringBuilder.append(not.getTitle())
                        .append(" ")
                        .append(FormatTime.formattedTime(not.getLocalDateTime()))
                        .append(" ")
                        .append(not.getFrequency().getDisplayName())
                        .append("\n");
            }
            msgSender.sendMessage(chatId, stringBuilder.toString());
        }
        else {
            msgSender.sendMessage(chatId, "You don't has a notifications", KeyboardBuilder.createFunctionalKeyboard());
        }
    }

}
