package com.example.LearnEnglishBot.model.wordList;

import com.example.LearnEnglishBot.model.BaseEntity;
import com.example.LearnEnglishBot.model.test.Test;
import com.example.LearnEnglishBot.model.user.User;
import com.example.LearnEnglishBot.model.word.Word;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@ToString
@Component
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class WordList implements BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    @OneToMany(mappedBy = "wordList", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<Word> words = new ArrayList<>();
    @OneToMany(mappedBy = "wordList", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<Test> tests = new ArrayList<>();
    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    private User user;
    private Float reputation;
    @Enumerated(EnumType.STRING)
    private AccessLevel accessLevel;
    @Enumerated(EnumType.STRING)
    private EnglishLevel englishLevel;
    @Enumerated(EnumType.STRING)
    private Category category;
}
