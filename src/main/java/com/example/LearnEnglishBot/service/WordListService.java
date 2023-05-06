package com.example.LearnEnglishBot.service;

import com.example.LearnEnglishBot.model.user.User;
import com.example.LearnEnglishBot.model.wordList.Category;
import com.example.LearnEnglishBot.model.wordList.EnglishLevel;
import com.example.LearnEnglishBot.model.wordList.WordList;
import com.example.LearnEnglishBot.repository.WordListRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class WordListService extends BaseService<WordList, WordListRepository> {
    public WordListService(WordListRepository repository) {
        super(repository);
    }

    public List<WordList> findByUser(User user) {
        return repository.findByUser(user);
    }

    public List<WordList> findByCategoryAndEnglishLevel(Category category, EnglishLevel engLvl) {
        return repository.findByCategoryAndEnglishLevel(category, engLvl);
    }

    public WordList findByTitleAndUser(String title, User user) {
        return repository.findByTitleAndUser(title, user);
    }

    @Transactional
    public void deleteAllByUser(User user) {
        repository.deleteAllByUser(user);
    }

    public List<WordList> findSuggestedOfList(Category category, Long userId) {
        return repository.findSuggestedOfList(category, userId);
    }
}
