package com.example.LearnEnglishBot.model.user;

import com.example.LearnEnglishBot.model.BaseEntity;
import com.example.LearnEnglishBot.model.wordList.WordList;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Builder
@ToString
@Component
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "_user")
public class User implements BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String password;
    private Long chatId;
    private LocalDateTime registrationTime;
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<WordList> wordLists = new ArrayList<>();
}
