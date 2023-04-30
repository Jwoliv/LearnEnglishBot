package com.example.LearnEnglishBot.service;

import com.example.LearnEnglishBot.model.User;
import com.example.LearnEnglishBot.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService extends BaseService<User, UserRepository> {

    public UserService(UserRepository repository) {
        super(repository);
    }

    public User findByUsername(String username) {
        return repository.findByUsername(username);
    }
    public User findByChatId(Long chatId) {
        return repository.findByChatId(chatId);
    }

}