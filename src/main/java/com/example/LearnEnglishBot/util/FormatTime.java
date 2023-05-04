package com.example.LearnEnglishBot.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FormatTime {
    public static String formattedTime(LocalDateTime localDateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        return localDateTime.format(formatter);
    }
}
