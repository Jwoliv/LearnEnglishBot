package com.example.LearnEnglishBot.model.word;

import com.example.LearnEnglishBot.model.BaseEntity;
import com.example.LearnEnglishBot.model.user.User;
import com.example.LearnEnglishBot.model.wordList.WordList;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.stereotype.Component;

@Entity
@Data
@Component
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Word implements BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String sourceWord;
    private String translateWord;
    private Boolean isLearned;
    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    private WordList wordList;
    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    private User user;
}
