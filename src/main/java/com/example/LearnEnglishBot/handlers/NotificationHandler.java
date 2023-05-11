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
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Getter
@Setter
@EnableScheduling
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
            msgSender.sendMessage(chatId, "📝 Enter a new title");
            cndNot = ConditionNotification.WAIT_FOR_TITLE;
        }
        else if (text.equals("🗑️ Delete notification")) {
            prepareDeleteNotification(chatId);
        }
        else if (cndNot.equals(ConditionNotification.WAIT_FOR_TITLE)) {
            handleTitleNotification(chatId, text);
        }
        else if (cndNot.equals(ConditionNotification.WAIT_FOR_DATE)) {
            handleDateNotification(chatId, text);
        }
        else if (cndNot.equals(ConditionNotification.WAIT_FOR_FREQUENCY)) {
            handleFrequencyNotification(chatId, text);
        }
        else if (cndNot.equals(ConditionNotification.DELETE_ITEM)) {
            deleteNotification(chatId, text);
        }
    }

    public void messageOfAllNotifications(Long chatId) {
        var user = userService.findByChatId(chatId);
        var notifications = user.getNotifications();

        if (notifications.size() != 0) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("📚 Your notifications\n\n");
            for (var not : notifications) {
                stringBuilder.append("💣")
                        .append(not.getTitle())
                        .append(" ")
                        .append(FormatTime.formattedTimeOnlyDate(not.getLocalDateTime()))
                        .append(" ")
                        .append(not.getFrequency().getDisplayName())
                        .append("\n");
            }
            msgSender.sendMessage(chatId, stringBuilder.toString(), KeyboardBuilder.createFunctionalKeyboard());
        }
        else {
            msgSender.sendMessage(chatId, "👀 You don't have a notifications", KeyboardBuilder.createFunctionalKeyboard());
        }
    }

    @Scheduled(fixedDelay =  1000 * 60)
    public void checkNotifications() {
        var users = userService.findAll();
        var currentDate = FormatTime.formattedTimeOnlyDate(LocalDateTime.now());
        for (var user : users) {
            var notifications = user.getNotifications().stream()
                    .filter(x -> FormatTime.formattedTimeOnlyDate(x.getLocalDateTime()).equals(currentDate))
                    .toList();

            var sb = new StringBuilder();
            notifications.forEach(nt -> {
                sb.append("🔥")
                        .append(nt.getTitle())
                        .append(" - ")
                        .append(nt.getLocalDateTime())
                        .append("\n");

                if (nt.getFrequency().equals(NotificationFrequency.ONCE)) {
                    notificationService.deleteById(nt.getId());
                } else {
                    switch (nt.getFrequency()) {
                        case DAILY   -> nt.setLocalDateTime(LocalDateTime.now().plusDays(1));
                        case WEEKLY -> nt.setLocalDateTime(LocalDateTime.now().plusWeeks(1));
                        case MONTHLY -> nt.setLocalDateTime(LocalDateTime.now().plusMonths(1));
                        case YEARLY  -> nt.setLocalDateTime(LocalDateTime.now().plusYears(1));
                    }
                    notificationService.save(nt);
                }
                msgSender.sendMessage(user.getChatId(), sb.toString(), KeyboardBuilder.createFunctionalKeyboard());
            });
        }
    }


    private void prepareDeleteNotification(Long chatId) {
        var user = userService.findByChatId(chatId);
        var notifications = user.getNotifications().stream().map(Notification::getTitle).toList();
        if (notifications.size() != 0) {
            msgSender.sendMessage(chatId, "📚 All notifications\n📝 Select a notification that you want to delete", KeyboardBuilder.createKeyboardOfList(notifications));
            cndNot = ConditionNotification.DELETE_ITEM;
        }
        else {
            msgSender.sendMessage(chatId, "❌ List of the notifications is empty", KeyboardBuilder.createFunctionalKeyboard());
            cndNot = null;
        }
    }

    private void handleTitleNotification(Long chatId, String text) {
        msgSender.sendMessage(chatId, "📝 Enter a date `dd.mm.yyyy`");
        notification.setTitle(text);
        cndNot = ConditionNotification.WAIT_FOR_DATE;
    }

    private void handleDateNotification(Long chatId, String text) {
        var dateStr = text.split("\\.");
        if (dateStr.length == 3) {
            int day = Integer.parseInt(dateStr[0]);
            int month = Integer.parseInt(dateStr[1]);
            int year = Integer.parseInt(dateStr[2]);

            var date = LocalDateTime.of(year, month, day, 0, 0, 0);

            notification.setLocalDateTime(date);
            msgSender.sendMessage(
                    chatId,
                    "🌐 Select a frequency of the notification",
                    KeyboardBuilder.createKeyboardOfEnum(NotificationFrequency.class)
            );
            cndNot = ConditionNotification.WAIT_FOR_FREQUENCY;
        }
        else {
            msgSender.sendMessage(chatId, "🚫 Wrong format of the date\n📝 Please try again");
        }
    }


    private void handleFrequencyNotification(Long chatId, String text) {
        try {
            var user = userService.findByChatId(chatId);
            notification.setFrequency(NotificationFrequency.valueOf(text));
            notification.setUser(user);

            notificationService.save(notification);
            msgSender.sendMessage(chatId, "✅ Notification is saved successfully", KeyboardBuilder.createFunctionalKeyboard());
            cndNot = null;
        }
        catch (Exception e) {
            msgSender.sendMessage(chatId, "🚫 Wrong format of the frequency\n📝 Please try again");
        }
    }

    private void deleteNotification(Long chatId, String text) {
        var user = userService.findByChatId(chatId);
        var notifications = user.getNotifications();
        if (notifications.size() != 0) {
            var notification = notifications.stream().filter(x -> x.getTitle().equals(text)).findFirst().orElse(null);
            if (notification != null) {
                long id = notification.getId();
                notificationService.deleteById(id);
                msgSender.sendMessage(chatId, "✅ Notification delete successfully");
                cndNot = null;
            }
            else {
                var strNotification = notifications.stream().map(Notification::getTitle).toList();
                msgSender.sendMessage(chatId, "🚫 Wrong title of the notifications\n📝 Select a notification that you want to delete", KeyboardBuilder.createKeyboardOfList(strNotification));
            }
        }
    }

}
