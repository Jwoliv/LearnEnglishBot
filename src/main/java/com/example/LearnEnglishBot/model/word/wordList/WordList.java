package com.example.LearnEnglishBot.model.word.wordList;

import com.example.LearnEnglishBot.model.BaseEntity;
import com.example.LearnEnglishBot.model.user.User;
import com.example.LearnEnglishBot.model.word.Word;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Component
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class WordList implements BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToMany(mappedBy = "wordList", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<Word> words = new ArrayList<>();
    @ManyToOne
    private User user;
    private Boolean isPublic;
    @Enumerated(EnumType.STRING)
    private EnglishLevel englishLevel;
    @Enumerated(EnumType.STRING)
    private Category category;
}
