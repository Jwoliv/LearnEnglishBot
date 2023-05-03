package com.example.LearnEnglishBot.repository;

import com.example.LearnEnglishBot.model.user.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends BaseRepository<User> {
    User findByUsername(String username);
    User findByChatId(Long chatId);
    @Query("SELECT U FROM User AS U ORDER BY U.reputation DESC LIMIT 20")
    List<User> findTop20Users();
}
