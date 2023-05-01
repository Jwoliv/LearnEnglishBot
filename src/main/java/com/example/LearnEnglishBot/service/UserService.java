package com.example.LearnEnglishBot.service;

import com.example.LearnEnglishBot.model.user.User;
import com.example.LearnEnglishBot.repository.UserRepository;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

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

    @Transactional
    public void singIn(String username, String password, Long chatId) {
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        User newUser = User.builder()
                .username(username)
                .password(hashedPassword)
                .chatId(chatId)
                .registrationTime(LocalDateTime.now())
                .build();
        save(newUser);
    }

    @Transactional
    public boolean login(String username, String password, Long chatId) {
        User user = findByUsername(username);
        if (BCrypt.checkpw(password, user.getPassword())) {
            user.setChatId(chatId);
            user.setRegistrationTime(LocalDateTime.now());
            save(user);
            return true;
        }
        return false;
    }

}