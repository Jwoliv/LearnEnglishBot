package com.example.LearnEnglishBot.model.word;

import com.example.LearnEnglishBot.model.BaseEntity;
import com.example.LearnEnglishBot.model.user.User;
import com.example.LearnEnglishBot.model.wordList.WordList;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.stereotype.Component;

@Entity
@Data
@Component
@ToString
@EqualsAndHashCode
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
