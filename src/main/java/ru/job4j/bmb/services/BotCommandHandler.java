package ru.job4j.bmb.services;

/*
Компонент взаимодействия с Telegram API
Класс для обработки команд, поступающих от пользователей
 */

import org.springframework.stereotype.Service;
import ru.job4j.bmb.content.Content;

@Service
public class BotCommandHandler {
    void receive(Content content) {
        System.out.println(content);
    }
}