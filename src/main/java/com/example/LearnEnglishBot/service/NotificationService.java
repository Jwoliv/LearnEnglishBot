package com.example.LearnEnglishBot.service;

import com.example.LearnEnglishBot.model.notification.Notification;
import com.example.LearnEnglishBot.repository.NotificationRepository;
import org.springframework.stereotype.Service;

@Service
public class NotificationService extends BaseService<Notification, NotificationRepository> {
    public NotificationService(NotificationRepository repository) {
        super(repository);
    }
}
