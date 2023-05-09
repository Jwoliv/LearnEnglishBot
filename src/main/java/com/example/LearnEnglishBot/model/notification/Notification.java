package com.example.LearnEnglishBot.model.notification;

import com.example.LearnEnglishBot.model.BaseEntity;
import com.example.LearnEnglishBot.model.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Entity
@Data
@ToString
@Builder
@Component
@NoArgsConstructor
@AllArgsConstructor
public class Notification implements BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private LocalDateTime localDateTime;
    @Enumerated(EnumType.STRING)
    private NotificationFrequency frequency;
    @ManyToOne
    private User user;
}
