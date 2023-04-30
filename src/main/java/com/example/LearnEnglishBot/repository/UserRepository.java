package com.example.LearnEnglishBot.repository;

import com.example.LearnEnglishBot.model.user.User;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends BaseRepository<User> {
    User findByUsername(String username);
    User findByChatId(Long chatId);
}
