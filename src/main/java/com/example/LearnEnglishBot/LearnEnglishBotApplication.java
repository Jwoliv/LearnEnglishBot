package com.example.LearnEnglishBot;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
@AutoConfiguration
public class LearnEnglishBotApplication {

	public static void main(String[] args) {
		SpringApplication.run(LearnEnglishBotApplication.class, args);
	}
}
