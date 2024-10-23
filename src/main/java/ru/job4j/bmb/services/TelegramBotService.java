package ru.job4j.bmb.services;

/*
Компонент взаимодействия с Telegram API
Oсновной класс, использует Telegram API для получения и отправки сообщений
Класс описывает интеграцию с Telegram API
 */

import org.springframework.stereotype.Service;
import ru.job4j.bmb.content.Content;

@Service
public class TelegramBotService {
    private final BotCommandHandler handler;

    public TelegramBotService(BotCommandHandler handler) {
        this.handler = handler;
    }

    public void content(Content content) {
        handler.receive(content);
    }
}