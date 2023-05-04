package com.example.LearnEnglishBot.model.test;

import com.example.LearnEnglishBot.model.BaseEntity;
import com.example.LearnEnglishBot.model.user.User;
import com.example.LearnEnglishBot.model.wordList.WordList;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;

@Entity
@Data
@Builder
@ToString
@Component
@NoArgsConstructor
@AllArgsConstructor
public class Test implements BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private WordList wordList;
    private Integer numberOfCorrect;
    private Integer numberOfWrong;
    private LocalDateTime startTime;
    private LocalDateTime finishTime;
    private Duration spendTime;
    @Enumerated(EnumType.STRING)
    private TypeTest typeTest;
    @ManyToOne
    private User user;

    public int getAssessment() {
        var mainSize = wordList.getWords().size();
        return (numberOfCorrect * 100) / mainSize;
    }

    public String getSpendTime() {
        if (spendTime != null) {
            long hours = spendTime.toHours();
            long minutes = spendTime.toMinutes() % 60;
            long seconds = spendTime.toSeconds() % 60;
            return String.format("%02d:%02d:%02d", hours, minutes, seconds);
        }
        return null;
    }
}
