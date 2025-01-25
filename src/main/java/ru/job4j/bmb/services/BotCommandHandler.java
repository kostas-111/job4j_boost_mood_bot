package ru.job4j.bmb.services;

/*
Компонент взаимодействия с Telegram API
Класс для обработки команд, поступающих от пользователей
 */

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.job4j.bmb.content.Content;
import ru.job4j.bmb.model.User;
import ru.job4j.bmb.repository.UserRepository;

import java.util.Optional;

@Service
public class BotCommandHandler {
    private final UserRepository userRepository;
    private final MoodService moodService;
    private final TgUI tgUI;

    public BotCommandHandler(UserRepository userRepository,
                             MoodService moodService,
                             TgUI tgUI) {
        this.userRepository = userRepository;
        this.moodService = moodService;
        this.tgUI = tgUI;
    }

    /*
    Метод commands обрабатывает входящие текстовые сообщения от пользователя
    и выполняет соответствующие действия на основе команды, содержащейся в сообщении.
    Он возвращает объект Optional<Content>, который содержит результат
    выполнения команды, либо пустое значение, если команда не распознана
     */
    Optional<Content> commands(Message message) {
        long userId = message.getFrom().getId();
        long chatId = message.getChatId();
        String userMessage = message.getText();
        Optional<Content> content = Optional.empty();
        if ("/start".equals(userMessage)) {
            content = handleStartCommand(chatId, userId);
        }
        if ("/week_mood_log".equals(userMessage)) {
            content = moodService.weekMoodLogCommand(chatId, userId);
        }
        if ("/month_mood_log".equals(userMessage)) {
            content = moodService.monthMoodLogCommand(chatId, userId);
        }
        if ("/award".equals(userMessage)) {
            content = moodService.awards(chatId, userId);
        }
        return content;
    }

    Optional<Content> handleCallback(CallbackQuery callback) {
        var moodId = Long.valueOf(callback.getData());
        var user = userRepository.findById(callback.getFrom().getId());
        return user.map(value -> moodService.chooseMood(value, moodId));
    }

    private Optional<Content> handleStartCommand(long chatId, Long clientId) {
        var user = new User();
        user.setClientId(clientId);
        user.setChatId(chatId);
        if (userRepository.findByClientId(clientId).getClientId() != user.getClientId()) {
            userRepository.save(user);
        }
        var content = new Content(user.getChatId());
        content.setText("Как настроение?");
        content.setMarkup(tgUI.buildButtons());
        return Optional.of(content);
    }

    void receive(Content content) {
        System.out.println(content);
    }
}