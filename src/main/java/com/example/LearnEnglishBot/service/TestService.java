package com.example.LearnEnglishBot.service;

import com.example.LearnEnglishBot.model.test.Test;
import com.example.LearnEnglishBot.repository.TestRepository;
import org.springframework.stereotype.Service;

@Service
public class TestService extends BaseService<Test, TestRepository> {
    public TestService(TestRepository repository) {
        super(repository);
    }
}
