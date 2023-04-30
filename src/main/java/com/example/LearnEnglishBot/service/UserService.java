package com.example.LearnEnglishBot.service;

import com.example.LearnEnglishBot.model.user.User;
import com.example.LearnEnglishBot.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService extends BaseService<User, UserRepository> {

    public UserService(UserRepository repository) {
        super(repository);
    }

    @Override
    public void save(User entity) {
        if (findByUsername(entity.getUsername()) == null) {
            super.save(entity);
        }
    }

    public User findByUsername(String username) {
        return repository.findByUsername(username);
    }
    public User findByChatId(Long chatId) {
        return repository.findByChatId(chatId);
    }

}