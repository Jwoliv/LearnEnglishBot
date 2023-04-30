package com.example.LearnEnglishBot.service;

import com.example.LearnEnglishBot.model.word.Word;
import com.example.LearnEnglishBot.repository.WordRepository;
import org.springframework.stereotype.Service;

@Service
public class WordService extends BaseService<Word, WordRepository> {

    public WordService(WordRepository repository) {
        super(repository);
    }

}