package com.example.LearnEnglishBot.repository;

import com.example.LearnEnglishBot.model.user.User;
import com.example.LearnEnglishBot.model.word.wordList.Category;
import com.example.LearnEnglishBot.model.word.wordList.EnglishLevel;
import com.example.LearnEnglishBot.model.word.wordList.WordList;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WordListRepository extends BaseRepository<WordList> {
    WordList findByTitleAndUser(String title, User user);
    void deleteAllByUser(User user);
    List<WordList> findByUser(User user);
    List<WordList> findByCategoryAndEnglishLevel(Category category, EnglishLevel englishLevel);
}
