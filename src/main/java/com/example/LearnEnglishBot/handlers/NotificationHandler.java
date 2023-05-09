package com.example.LearnEnglishBot.handlers;

import com.example.LearnEnglishBot.model.notification.ConditionNotification;
import com.example.LearnEnglishBot.service.NotificationService;
import com.example.LearnEnglishBot.service.UserService;
import com.example.LearnEnglishBot.util.MessageSender;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
public class NotificationHandler {
    private final UserService userService;
    private final NotificationService notificationService;
    private MessageSender msgSender;
    private ConditionNotification cndNotification;

    public NotificationHandler(UserService userService, NotificationService notificationService) {
        this.userService = userService;
        this.notificationService = notificationService;
    }

    @Autowired
    @Lazy
    public void setMsgSender(MessageSender msgSender) {
        this.msgSender = msgSender;
    }

    /*
        "🔔 Notifications"
        "🆕 New notification"
        "🗑️ Delete notification"
     */
}
