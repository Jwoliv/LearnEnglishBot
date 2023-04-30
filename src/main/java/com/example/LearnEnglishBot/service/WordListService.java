package com.example.LearnEnglishBot.service;

import com.example.LearnEnglishBot.model.user.User;
import com.example.LearnEnglishBot.model.word.wordList.Category;
import com.example.LearnEnglishBot.model.word.wordList.EnglishLevel;
import com.example.LearnEnglishBot.model.word.wordList.WordList;
import com.example.LearnEnglishBot.repository.WordListRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
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

}
