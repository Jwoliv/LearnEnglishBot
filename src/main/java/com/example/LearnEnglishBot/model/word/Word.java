package com.example.LearnEnglishBot.model.word;

import com.example.LearnEnglishBot.model.BaseEntity;
import com.example.LearnEnglishBot.model.user.User;
import com.example.LearnEnglishBot.model.word.wordList.WordList;
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
    private String engWord;
    private String translateWord;
    private Boolean isLearned;
    @ManyToOne
    private User user;
    @ManyToOne
    private WordList wordList;
}
