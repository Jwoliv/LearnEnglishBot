package com.example.LearnEnglishBot.model.test;

import com.example.LearnEnglishBot.model.BaseEntity;
import com.example.LearnEnglishBot.model.user.User;
import com.example.LearnEnglishBot.model.wordList.WordList;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.stereotype.Component;

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
    @ManyToOne
    private User user;

    public int getRating() {
        return (numberOfCorrect * (numberOfCorrect + numberOfCorrect)) / 100;
    }
}
