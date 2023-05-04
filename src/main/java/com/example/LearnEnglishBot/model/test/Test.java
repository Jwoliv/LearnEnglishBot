package com.example.LearnEnglishBot.model.test;

import com.example.LearnEnglishBot.model.BaseEntity;
import com.example.LearnEnglishBot.model.user.User;
import com.example.LearnEnglishBot.model.wordList.WordList;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.stereotype.Component;

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
    @Enumerated(EnumType.STRING)
    private TypeTest typeTest;
    @ManyToOne
    private User user;

    public int getAssessment() {
        var mainSize = wordList.getWords().size();
        return (numberOfCorrect * 100) / mainSize;
    }
}
